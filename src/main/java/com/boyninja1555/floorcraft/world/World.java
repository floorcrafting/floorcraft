package com.boyninja1555.floorcraft.world;

import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.entities.Player;
import org.joml.Vector2i;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class World {
    private final Player playerRef;
    private final Map<Vector2i, Chunk> chunks = new HashMap<>();

    public World(Player playerRef) {
        this.playerRef = playerRef;
    }

    public void addChunk(Vector2i position, Block[] blocks) {
        Chunk chunk = new Chunk(this, position, blocks);
        chunks.put(position, chunk);
    }

    public void refreshMeshes() {
        for (Chunk chunk : chunks.values())
            chunk.generateMesh();
    }

    public Block blockAt(int x, int y, int z) {
        int cx = Math.floorDiv(x, Chunk.WIDTH);
        int cz = Math.floorDiv(z, Chunk.DEPTH);

        Chunk chunk = chunks.get(new Vector2i(cx, cz));

        if (chunk == null) return null;
        int lx = Math.floorMod(x, Chunk.WIDTH);
        int lz = Math.floorMod(z, Chunk.DEPTH);

        if (y < 0 || y >= Chunk.HEIGHT) return null;
        return chunk.blockAt(lx, y, lz);
    }

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

    public Map<Vector2i, Chunk> chunks() {
        return chunks;
    }
}
