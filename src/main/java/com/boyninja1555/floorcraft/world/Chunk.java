package com.boyninja1555.floorcraft.world;

import com.boyninja1555.floorcraft.Floorcraft;
import com.boyninja1555.floorcraft.blocks.Block;
import com.boyninja1555.floorcraft.lib.PrimitiveShit;
import com.boyninja1555.floorcraft.mesh.Mesh;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import com.boyninja1555.floorcraft.world.format.WorldBlockIDs;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Arrays;

public class Chunk {
    public static final int WIDTH = 32;
    public static final int HEIGHT = 64;
    public static final int DEPTH = 32;
    private static final float[] meshBuffer = new float[WIDTH * HEIGHT * DEPTH * 6 * 6 * 8];

    private final World world;
    private final Vector2i position;
    private final int[] blocks;
    private final Mesh opaqueMesh;
    private final Mesh transparentMesh;

    public Chunk(World world, Vector2i position, int[] blocks) {
        this.world = world;
        this.position = position;
        this.blocks = blocks;

        Vector3f worldOffset = new Vector3f(position.x * WIDTH, 0, position.y * DEPTH);

        opaqueMesh = new Mesh(worldOffset, null, build(false), false) {
        };

        transparentMesh = new Mesh(worldOffset, null, build(true), true) {
        };
    }

    public int[] blocks() {
        return blocks;
    }

    public void generateMesh() {
        float[] opaqueV = build(false);
        float[] transparentV = build(true);
        if (opaqueMesh.vao() == 0) {
            opaqueMesh.updateVertices(opaqueV);
            transparentMesh.updateVertices(transparentV);
            opaqueMesh.init();
            transparentMesh.init();
        } else {
            opaqueMesh.updateVertices(opaqueV);
            transparentMesh.updateVertices(transparentV);
        }
    }

    public Block blockAt(int lx, int ly, int lz) {
        if (lx < 0 || ly < 0 || lz < 0 || lx >= WIDTH || ly >= HEIGHT || lz >= DEPTH) return null;
        return WorldBlockIDs.blockFromId(blocks[lx + (ly * WIDTH) + (lz * WIDTH * HEIGHT)]);
    }

    public void setBlock(int lx, int ly, int lz, Block block) {
        if (lx < 0 || ly < 0 || lz < 0 || lx >= WIDTH || ly >= HEIGHT || lz >= DEPTH) return;

        blocks[lx + (ly * WIDTH) + (lz * WIDTH * HEIGHT)] = WorldBlockIDs.idFromBlock(block);
        generateMesh();
    }

    private float[] build(boolean buildTransparent) {
        int pointer = 0;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int z = 0; z < DEPTH; z++) {
                    Block block = blockAt(x, y, z);

                    if (block == null || block.definition().transparent() != buildTransparent) continue;
                    int wx = x + (position.x * WIDTH);
                    int wz = z + (position.y * DEPTH);
                    int[][] textureCoordinates = Arrays.stream(block.definition().texture()).map(PrimitiveShit::integerArrayToIntArray).toList().toArray(new int[0][]);
                    if (shouldShowFace(wx, y + 1, wz, block)) // Y+
                        pointer = addFace(pointer, x, y, z, 0, 1, 0, Floorcraft.textures().region(textureCoordinates[0][0], textureCoordinates[0][1]));

                    if (shouldShowFace(wx, y - 1, wz, block)) // Y-
                        pointer = addFace(pointer, x, y, z, 0, -1, 0, Floorcraft.textures().region(textureCoordinates[1][0], textureCoordinates[1][1]));

                    if (shouldShowFace(wx, y, wz + 1, block)) // Z+
                        pointer = addFace(pointer, x, y, z, 0, 0, 1, Floorcraft.textures().region(textureCoordinates[2][0], textureCoordinates[2][1]));

                    if (shouldShowFace(wx, y, wz - 1, block)) // Z-
                        pointer = addFace(pointer, x, y, z, 0, 0, -1, Floorcraft.textures().region(textureCoordinates[3][0], textureCoordinates[3][1]));

                    if (shouldShowFace(wx - 1, y, wz, block)) // X-
                        pointer = addFace(pointer, x, y, z, -1, 0, 0, Floorcraft.textures().region(textureCoordinates[4][0], textureCoordinates[4][1]));

                    if (shouldShowFace(wx + 1, y, wz, block)) // X+
                        pointer = addFace(pointer, x, y, z, 1, 0, 0, Floorcraft.textures().region(textureCoordinates[5][0], textureCoordinates[5][1]));
                }
            }
        }

        float[] result = new float[pointer];
        System.arraycopy(meshBuffer, 0, result, 0, pointer);
        return result;
    }

    private boolean shouldShowFace(int wx, int wy, int wz, Block current) {
        Block neighbor = world.blockAt(new Vector3i(wx, wy, wz));
        if (neighbor == null) return true;
        return neighbor.definition().transparent() && !current.definition().transparent();
    }

    private int addFace(int pointer, int x, int y, int z, int nx, int ny, int nz, AtlasRegion region) {
        float[][] quad = getFace(nx, ny, nz);
        for (float[] v : quad) {
            meshBuffer[pointer++] = x + v[0];
            meshBuffer[pointer++] = y + v[1];
            meshBuffer[pointer++] = z + v[2];
            meshBuffer[pointer++] = (float) nx;
            meshBuffer[pointer++] = (float) ny;
            meshBuffer[pointer++] = (float) nz;
            meshBuffer[pointer++] = v[3] == 0 ? region.u0() : region.u1();
            meshBuffer[pointer++] = v[4] == 0 ? region.v0() : region.v1();
        }
        return pointer;
    }

    public Vector2i position() {
        return new Vector2i(position);
    }

    public Mesh opaque() {
        return opaqueMesh;
    }

    public Mesh transparent() {
        return transparentMesh;
    }

    private float[][] getFace(int nx, int ny, int nz) {
        if (ny == 1)
            return new float[][]{{0, 1, 1, 0, 1}, {1, 1, 1, 1, 1}, {1, 1, 0, 1, 0}, {0, 1, 1, 0, 1}, {1, 1, 0, 1, 0}, {0, 1, 0, 0, 0}};

        if (ny == -1)
            return new float[][]{{0, 0, 0, 0, 1}, {1, 0, 0, 1, 1}, {1, 0, 1, 1, 0}, {0, 0, 0, 0, 1}, {1, 0, 1, 1, 0}, {0, 0, 1, 0, 0}};

        if (nz == 1)
            return new float[][]{{0, 0, 1, 0, 1}, {1, 0, 1, 1, 1}, {1, 1, 1, 1, 0}, {0, 0, 1, 0, 1}, {1, 1, 1, 1, 0}, {0, 1, 1, 0, 0}};

        if (nz == -1)
            return new float[][]{{1, 0, 0, 0, 1}, {0, 0, 0, 1, 1}, {0, 1, 0, 1, 0}, {1, 0, 0, 0, 1}, {0, 1, 0, 1, 0}, {1, 1, 0, 0, 0}};

        if (nx == 1)
            return new float[][]{{1, 0, 1, 0, 1}, {1, 0, 0, 1, 1}, {1, 1, 0, 1, 0}, {1, 0, 1, 0, 1}, {1, 1, 0, 1, 0}, {1, 1, 1, 0, 0}};

        return new float[][]{{0, 0, 0, 0, 1}, {0, 0, 1, 1, 1}, {0, 1, 1, 1, 0}, {0, 0, 0, 0, 1}, {0, 1, 1, 1, 0}, {0, 1, 0, 0, 0}};
    }
}
