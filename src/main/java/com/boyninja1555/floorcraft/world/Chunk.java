package com.boyninja1555.floorcraft.world;

import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.mesh.Mesh;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32 * 2;
    public static final int DEPTH = 32;

    private final Vector2i position;
    private final Block[] blocks;
    private final Mesh opaqueMesh;
    private final Mesh transparentMesh;

    public Chunk(Vector2i position, Block[] blocks) {
        this.position = position;
        this.blocks = blocks;

        Vector3f worldOffset = new Vector3f(position.x * WIDTH, -1f - HEIGHT, position.y * DEPTH);

        float[] opaqueVertices = build(false);
        float[] transparentVertices = build(true);

        opaqueMesh = new Mesh(worldOffset, null, false) {

            @Override
            public float[] vertices() {
                return opaqueVertices;
            }
        };

        transparentMesh = new Mesh(worldOffset, null, true) {

            @Override
            public float[] vertices() {
                return transparentVertices;
            }
        };

        opaqueMesh.init();
        transparentMesh.init();
    }

    public Vector2i position() {
        return position;
    }

    public Vector2f positionF() {
        return new Vector2f(position.x, position.y);
    }

    public Mesh opaque() {
        return opaqueMesh;
    }

    public Mesh transparent() {
        return transparentMesh;
    }

    // Indexing
    private int indexOf(int x, int y, int z) {
        return x + (y * WIDTH) + (z * WIDTH * HEIGHT);
    }

    private Block blockAt(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= WIDTH || y >= HEIGHT || z >= DEPTH) return null;
        return blocks[indexOf(x, y, z)];
    }

    // Meshing
    private float[] build(boolean buildTransparent) {
        List<Float> vertices = new ArrayList<>();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int z = 0; z < DEPTH; z++) {
                    Block block = blockAt(x, y, z);
                    if (block == null || block.transparent() != buildTransparent) continue;

                    // Top (0, 1, 0)
                    if (shouldShowFace(x, y + 1, z, block))
                        addFace(vertices, x, y, z, 0, 1, 0, block.texture().sides().getFirst());
                    // Bottom (0, -1, 0)
                    if (shouldShowFace(x, y - 1, z, block))
                        addFace(vertices, x, y, z, 0, -1, 0, block.texture().sides().get(1));
                    // Front (0, 0, 1)
                    if (shouldShowFace(x, y, z + 1, block))
                        addFace(vertices, x, y, z, 0, 0, 1, block.texture().sides().get(2));
                    // Back (0, 0, -1)
                    if (shouldShowFace(x, y, z - 1, block))
                        addFace(vertices, x, y, z, 0, 0, -1, block.texture().sides().get(3));
                    // Left (-1, 0, 0)
                    if (shouldShowFace(x + 1, y, z, block))
                        addFace(vertices, x, y, z, 1, 0, 0, block.texture().sides().get(5));
                    if (shouldShowFace(x - 1, y, z, block))
                        addFace(vertices, x, y, z, -1, 0, 0, block.texture().sides().get(4));
                }
            }
        }

        return toArray(vertices);
    }

    private boolean shouldShowFace(int nx, int ny, int nz, Block current) {
        Block neighbor = blockAt(nx, ny, nz);
        if (neighbor == null) return true;
        return neighbor.transparent() && !current.transparent();
    }

    private void addFace(List<Float> verts, int x, int y, int z, int nx, int ny, int nz, AtlasRegion region) {
        float[][] quad = getFace(nx, ny, nz);
        for (float[] v : quad) {
            verts.add(x + v[0]);                              // Position X
            verts.add(y + v[1]);                              // Position Y
            verts.add(z + v[2]);                              // Position Z
            verts.add((float) nx);                            // Normal X
            verts.add((float) ny);                            // Normal Y
            verts.add((float) nz);                            // Normal Z
            verts.add(v[3] == 0 ? region.u0() : region.u1()); // UV U
            verts.add(v[4] == 0 ? region.v0() : region.v1()); // UV V
        }
    }

    private float[][] getFace(int nx, int ny, int nz) {
        if (ny == 1)
            return new float[][]{{0, 1, 1, 0, 1}, {1, 1, 1, 1, 1}, {1, 1, 0, 1, 0}, {0, 1, 1, 0, 1}, {1, 1, 0, 1, 0}, {0, 1, 0, 0, 0}}; // Top

        if (ny == -1)
            return new float[][]{{0, 0, 0, 0, 1}, {1, 0, 0, 1, 1}, {1, 0, 1, 1, 0}, {0, 0, 0, 0, 1}, {1, 0, 1, 1, 0}, {0, 0, 1, 0, 0}}; // Bottom

        if (nz == 1)
            return new float[][]{{0, 0, 1, 0, 1}, {1, 0, 1, 1, 1}, {1, 1, 1, 1, 0}, {0, 0, 1, 0, 1}, {1, 1, 1, 1, 0}, {0, 1, 1, 0, 0}}; // Front

        if (nz == -1)
            return new float[][]{{1, 0, 0, 0, 1}, {0, 0, 0, 1, 1}, {0, 1, 0, 1, 0}, {1, 0, 0, 0, 1}, {0, 1, 0, 1, 0}, {1, 1, 0, 0, 0}}; // Back

        if (nx == 1)
            return new float[][]{{1, 0, 1, 0, 1}, {1, 0, 0, 1, 1}, {1, 1, 0, 1, 0}, {1, 0, 1, 0, 1}, {1, 1, 0, 1, 0}, {1, 1, 1, 0, 0}}; // Right

        return new float[][]{{0, 0, 0, 0, 1}, {0, 0, 1, 1, 1}, {0, 1, 1, 1, 0}, {0, 0, 0, 0, 1}, {0, 1, 1, 1, 0}, {0, 1, 0, 0, 0}}; // Left
    }

    private float[] toArray(List<Float> list) {
        float[] arr = new float[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }
}
