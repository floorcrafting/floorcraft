package com.boyninja1555.floorcraft.world;

import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.entities.Player;
import com.boyninja1555.floorcraft.world.format.WorldFile;
import com.boyninja1555.floorcraft.world.format.WorldState;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class World {
    private final Player playerRef;
    private final Map<Vector2i, Chunk> chunks = new HashMap<>();
    private final WorldFile file;

    public World(Player playerRef) {
        this.playerRef = playerRef;
        this.file = new WorldFile(this);
    }

    public void init(Map<Vector2i, Block[]> defaultChunks) {
        if (!file.exists()) {
            for (Map.Entry<Vector2i, Block[]> chunk : defaultChunks.entrySet())
                addChunk(chunk.getKey(), chunk.getValue());

            save();
            refreshMeshes();
            return;
        }

        load();
        refreshMeshes();
    }

    public void load() {
        WorldState state = file.load();
        playerRef.teleport(state.playerPosition());
        playerRef.direction(state.playerDirection());

        for (Chunk chunk : state.chunks())
            chunks.put(chunk.position(), chunk);
    }

    public void save() {
        file.save(state());
    }

    public void addChunk(Vector2i position, Block[] blocks) {
        Chunk chunk = new Chunk(this, position, blocks);
        chunks.put(position, chunk);
    }

    public void refreshMeshes() {
        for (Chunk chunk : chunks.values())
            chunk.generateMesh();
    }

    public Block blockAt(Vector3i position) {
        Chunk chunk = chunkByBlockPosition(new Vector2i(position.x, position.z));

        if (chunk == null) return null;
        int lx = Math.floorMod(position.x, Chunk.WIDTH);
        int lz = Math.floorMod(position.z, Chunk.DEPTH);

        if (position.y < 0 || position.y >= Chunk.HEIGHT) return null;
        return chunk.blockAt(lx, position.y, lz);
    }

    public void setBlock(Vector3i position, Class<? extends Block> block) {
        Chunk chunk = chunkByBlockPosition(new Vector2i(position.x, position.z));

        if (chunk == null) return;
        int lx = Math.floorMod(position.x, Chunk.WIDTH);
        int lz = Math.floorMod(position.z, Chunk.DEPTH);
        if (position.y < 0 || position.y >= Chunk.HEIGHT) return;

        chunk.setBlock(lx, position.y, lz, block);
    }

    // World rendering

    public void render(int uModel, float[] matrixBuffer) {
        if (playerRef == null) return;

        List<Chunk> sortedChunks = new ArrayList<>(chunks.values());
        sortedChunks.sort(Comparator.comparingDouble(c -> c.position().distanceSquared((int) playerRef.position().x / Chunk.WIDTH, (int) playerRef.position().z / Chunk.DEPTH)));

        // Opaque
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);

        for (Chunk chunk : sortedChunks) {
            glUniformMatrix4fv(uModel, false, chunk.opaque().modelMatrix().get(matrixBuffer));
            chunk.opaque().render();
        }

        // Transparent
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);

        for (int i = sortedChunks.size() - 1; i >= 0; i--) {
            Chunk chunk = sortedChunks.get(i);
            glUniformMatrix4fv(uModel, false, chunk.transparent().modelMatrix().get(matrixBuffer));
            chunk.transparent().render();
        }

        glDepthMask(true);
    }

    // Raycasting

    public Vector3i raycast(Vector3f origin, Vector3f direction, float maxDistance, boolean stopAtLastAir) {
        Vector3f current = new Vector3f(origin);
        Vector3i lastAir = null;

        float step = .05f;
        for (float traveled = 0; traveled <= maxDistance; traveled += step) {
            Vector3i blockPos = new Vector3i((int) Math.floor(current.x), (int) Math.floor(current.y), (int) Math.floor(current.z));
            Block block = blockAt(blockPos);

            if (block != null) return stopAtLastAir ? lastAir : blockPos;

            lastAir = blockPos;
            current.fma(step, direction);
        }

        return null;
    }

    // Unique utilities

    public WorldState state() {
        return new WorldState(playerRef.position(), playerRef.direction(), playerRef.activeBlock(), chunks.values().toArray(new Chunk[0]));
    }

    private Chunk chunkByBlockPosition(Vector2i position) {
        int cx = Math.floorDiv(position.x, Chunk.WIDTH);
        int cz = Math.floorDiv(position.y, Chunk.DEPTH);
        return chunks.get(new Vector2i(cx, cz));
    }
}
