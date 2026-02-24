package com.boyninja1555.floorcraft.world.format;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.lib.AssetManager;
import com.boyninja1555.floorcraft.lib.ErrorHandler;
import com.boyninja1555.floorcraft.world.Chunk;
import com.boyninja1555.floorcraft.world.World;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WorldFile {
    private final World worldRef;
    private final File file;

    public WorldFile(World world) {
        this.worldRef = world;
        this.file = AssetManager.storagePath().resolve("world.bin").toFile();
    }

    public boolean exists() {
        return file.isFile();
    }

    public WorldState load() {
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            // Player data
            Vector3f playerPosition = readPositionF(in);
            Vector2f playerDirection = readDirection(in);
            Block playerActiveBlock = Floorcraft.blockRegistry().getById(in.readInt());

            // Chunks
            List<Chunk> chunks = new ArrayList<>();
            int chunkCount = in.readInt();
            for (int i = 0; i < chunkCount; i++)
                chunks.add(readChunk(in));

            return new WorldState(playerPosition, playerDirection, playerActiveBlock, chunks.toArray(new Chunk[0]));
        } catch (IOException ex) {
            ErrorHandler.error("Could not load world!\n" + ex);
            return null;
        }
    }

    public void save(WorldState state) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            // Player data
            writePositionF(out, state.playerPosition());
            writeDirection(out, state.playerDirection());
            out.writeInt(WorldBlockIDs.all().get(state.activeBlock().getClass()));

            // Chunks
            out.writeInt(state.chunks().length);

            for (Chunk chunk : state.chunks())
                writeChunk(out, chunk);

            // Write
            out.flush();
        } catch (IOException ex) {
            ErrorHandler.error("Could not save world!\n" + ex);
        }
    }

    // Chunk

    private Chunk readChunk(DataInputStream in) throws IOException {
        int x = in.readInt();
        int z = in.readInt();
        int[] blocks = new int[Chunk.WIDTH * Chunk.HEIGHT * Chunk.DEPTH];

        for (int i = 0; i < blocks.length; i++)
            blocks[i] = readBlock(in);

        return new Chunk(worldRef, new Vector2i(x, z), blocks);
    }

    private void writeChunk(DataOutputStream out, Chunk chunk) throws IOException {
        out.writeInt(chunk.position().x);
        out.writeInt(chunk.position().y);

        for (int block : chunk.blocks())
            writeBlock(out, block);
    }

    // Block

    private int readBlock(DataInputStream in) throws IOException {
        return in.readInt();
    }

    private void writeBlock(DataOutputStream out, int block) throws IOException {
        out.writeInt(block);
    }

    // Position

    private Vector3f readPositionF(DataInputStream in) throws IOException {
        float x = in.readFloat();
        float y = in.readFloat();
        float z = in.readFloat();
        return new Vector3f(x, y, z);
    }

    private Vector3i readPosition(DataInputStream in) throws IOException {
        int x = in.readInt();
        int y = in.readInt();
        int z = in.readInt();
        return new Vector3i(x, y, z);
    }

    private void writePositionF(DataOutputStream out, Vector3f position) throws IOException {
        out.writeFloat(position.x);
        out.writeFloat(position.y);
        out.writeFloat(position.z);
    }

    private void writePosition(DataOutputStream out, Vector3i position) throws IOException {
        out.writeInt(position.x);
        out.writeInt(position.y);
        out.writeInt(position.z);
    }

    // Direction

    private Vector2f readDirection(DataInputStream in) throws IOException {
        float pitch = in.readFloat();
        float yaw = in.readFloat();
        return new Vector2f(pitch, yaw);
    }

    private void writeDirection(DataOutputStream out, Vector2f direction) throws IOException {
        out.writeFloat(direction.x);
        out.writeFloat(direction.y);
    }
}
