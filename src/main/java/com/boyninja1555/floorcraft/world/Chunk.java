package com.boyninja1555.floorcraft.world;

import com.boyninja1555.floorcraft.blocks.lib.Block;
import com.boyninja1555.floorcraft.mesh.Mesh;
import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    public static final int WIDTH = 32;
    public static final int HEIGHT = 64;
    public static final int DEPTH = 32;

    private final World world;
    private final Vector2i position;
    private final Block[] blocks;
    private final Mesh opaqueMesh;
    private final Mesh transparentMesh;

    public Chunk(World world, Vector2i position, Block[] blocks) {
        this.world = world;
        this.position = position;
        this.blocks = blocks;

        Vector3f worldOffset = new Vector3f(position.x * WIDTH, -1f - HEIGHT, position.y * DEPTH);

        opaqueMesh = new Mesh(worldOffset, null, build(false), false) {
        };

        transparentMesh = new Mesh(worldOffset, null, build(true), true) {
        };
    }

    public void generateMesh() {
        opaqueMesh.updateVertices(build(false));
        transparentMesh.updateVertices(build(true));
        opaqueMesh.init();
        transparentMesh.init();
    }

    public Block blockAt(int lx, int ly, int lz) {
        if (lx < 0 || ly < 0 || lz < 0 || lx >= WIDTH || ly >= HEIGHT || lz >= DEPTH) return null;
        return blocks[lx + (ly * WIDTH) + (lz * WIDTH * HEIGHT)];
    }

    private float[] build(boolean buildTransparent) {
        List<Float> vertices = new ArrayList<>();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int z = 0; z < DEPTH; z++) {
                    Block block = blockAt(x, y, z);

                    if (block == null || block.transparent() != buildTransparent) continue;
                    int wx = x + (position.x * WIDTH);
                    int wz = z + (position.y * DEPTH);

                    if (shouldShowFace(wx, y + 1, wz, block))
                        addFace(vertices, x, y, z, 0, 1, 0, block.texture().sides().getFirst());

                    if (shouldShowFace(wx, y - 1, wz, block))
                        addFace(vertices, x, y, z, 0, -1, 0, block.texture().sides().get(1));

                    if (shouldShowFace(wx, y, wz + 1, block))
                        addFace(vertices, x, y, z, 0, 0, 1, block.texture().sides().get(2));

                    if (shouldShowFace(wx, y, wz - 1, block))
                        addFace(vertices, x, y, z, 0, 0, -1, block.texture().sides().get(3));

                    if (shouldShowFace(wx + 1, y, wz, block))
                        addFace(vertices, x, y, z, 1, 0, 0, block.texture().sides().get(5));

                    if (shouldShowFace(wx - 1, y, wz, block))
                        addFace(vertices, x, y, z, -1, 0, 0, block.texture().sides().get(4));
                }
            }
        }

        return toArray(vertices);
    }

    private boolean shouldShowFace(int wx, int wy, int wz, Block current) {
        Block neighbor = world.blockAt(wx, wy, wz);
        if (neighbor == null) return true;
        return neighbor.transparent() && !current.transparent();
    }

    private void addFace(List<Float> verts, int x, int y, int z, int nx, int ny, int nz, AtlasRegion region) {
        float[][] quad = getFace(nx, ny, nz);
        for (float[] v : quad) {
            verts.add(x + v[0]);
            verts.add(y + v[1]);
            verts.add(z + v[2]);
            verts.add((float) nx);
            verts.add((float) ny);
            verts.add((float) nz);
            verts.add(v[3] == 0 ? region.u0() : region.u1());
            verts.add(v[4] == 0 ? region.v0() : region.v1());
        }
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

    private float[] toArray(List<Float> list) {
        float[] arr = new float[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
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
