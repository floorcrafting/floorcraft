package com.boyninja1555.floorcraft.world;

import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.entities.Entity;
import com.boyninja1555.floorcraft.entities.Player;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.world.format.WorldBlockIDs;
import com.boyninja1555.floorcraft.world.format.WorldFile;
import com.boyninja1555.floorcraft.world.format.WorldState;
import com.boyninja1555.floorcraft.world.tick.WorldTicker;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class World {
    private final Player playerRef;
    private final List<Entity> entities;
    private final Map<Vector2i, Chunk> chunks;
    private final List<Chunk> chunkCache;
    private final WorldFile file;

    public World(Player playerRef) {
        this.playerRef = playerRef;
        this.entities = new ArrayList<>();
        this.chunks = new HashMap<>();
        this.chunkCache = new ArrayList<>();
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

    public void spawnEntity(Class<? extends Entity> type, Vector3f position, Vector2f direction, float gravity) {
        try {
            Entity entity = type.getConstructor(Vector3f.class, Vector2f.class, Float.class).newInstance(position, direction, gravity);
            entities.add(entity);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException ex) {
            String message = "Could not spawn " + type.getName() + "!\n" + ex;
            System.err.println(message);
            ErrorHandler.error(message);
        }
    }

    public void spawnEntity(Class<? extends Entity> type, Vector3f position, Vector2f direction) {
        spawnEntity(type, position, direction, Entity.DEFAULT_GRAVITY);
    }

    public void addChunk(Vector2i position, Block[] blocks) {
        int[] ids = new int[blocks.length];
        for (int i = 0; i < blocks.length; i++)
            ids[i] = WorldBlockIDs.idFromBlock(blocks[i].getClass());

        Chunk chunk = new Chunk(this, position, ids);
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

    public void setBlock(Vector3i position, Class<? extends Block> blockClass, boolean useRemoveHook) {
        Chunk chunk = chunkByBlockPosition(new Vector2i(position.x, position.z));

        if (chunk == null) return;

        Block oldBlock = blockAt(position);

        if (useRemoveHook && oldBlock != null) oldBlock.onBreak(this, position);
        int lx = Math.floorMod(position.x, Chunk.WIDTH);
        int lz = Math.floorMod(position.z, Chunk.DEPTH);
        if (position.y < 0 || position.y >= Chunk.HEIGHT) return;

        chunk.setBlock(lx, position.y, lz, blockClass);
        Block block = blockAt(position);

        if (block != null) block.onPlace(this, position);
    }

    public void setBlock(Vector3i position, Class<? extends Block> blockClass) {
        setBlock(position, blockClass, blockClass == null);
    }

    public void removeBlock(Vector3i position) {
        setBlock(position, null);
    }

    public void moveBlock(Vector3i oldPosition, Vector3i newPosition, boolean switchBlocks) {
        Block oldBlock = blockAt(oldPosition);
        Block newBlock = blockAt(newPosition);

        if (switchBlocks) {
            setBlock(oldPosition, newBlock.getClass());
            setBlock(newPosition, oldBlock.getClass());
        } else {
            setBlock(oldPosition, null, false);
            setBlock(newPosition, oldBlock.getClass());
        }
    }

    public void moveBlock(Vector3i oldPosition, Vector3i newPosition) {
        moveBlock(oldPosition, newPosition, false);
    }

    // World updates

    public void tick(float deltaTime) {
        WorldTicker.tick(this, deltaTime);
    }

    public void render(int uModel, float[] matrixBuffer) {
        if (playerRef == null) return;

        // Update cache only when needed or once per frame without recreating the list
        chunkCache.clear();
        chunkCache.addAll(chunks.values());

        // Only sort if we have transparent blocks to worry about
        int px = (int) playerRef.position().x / Chunk.WIDTH;
        int pz = (int) playerRef.position().z / Chunk.DEPTH;
        chunkCache.sort(Comparator.comparingDouble(c -> c.position().distanceSquared(px, pz)));

        // Opaque Pass
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);

        for (Chunk chunk : chunkCache) {
            glUniformMatrix4fv(uModel, false, chunk.opaque().modelMatrix().get(matrixBuffer));
            chunk.opaque().render();
        }

        // Transparent Pass (Back-to-Front)
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);

        for (int i = chunkCache.size() - 1; i >= 0; i--) {
            Chunk chunk = chunkCache.get(i);
            glUniformMatrix4fv(uModel, false, chunk.transparent().modelMatrix().get(matrixBuffer));
            chunk.transparent().render();
        }
        glDepthMask(true);
    }

    // Raycasting

    public Vector3i raycast(Vector3f origin, Vector3f direction, float maxDistance, boolean stopAtLastAir) {
        Vector3f current = new Vector3f(origin);
        Vector3i lastAir = null;
        Vector3i blockPos = new Vector3i();

        float step = .05f;
        for (float traveled = 0; traveled <= maxDistance; traveled += step) {
            blockPos.set((int) Math.floor(current.x), (int) Math.floor(current.y), (int) Math.floor(current.z));

            if (blockAt(blockPos) != null) {
                return stopAtLastAir ? lastAir : new Vector3i(blockPos);
            }

            if (stopAtLastAir) {
                if (lastAir == null) lastAir = new Vector3i();
                lastAir.set(blockPos);
            }

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
