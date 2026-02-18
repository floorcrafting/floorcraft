package com.boyninja1555.floorcraft.mesh;

import com.boyninja1555.floorcraft.texture.atlas.AtlasRegion;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class UIMesh {
    private int vao, vbo;

    public UIMesh(AtlasRegion region) {
        useAtlasRegion(region);
    }

    public void useAtlasRegion(AtlasRegion region) {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        float u0 = region.u0();
        float v0 = region.v0();
        float u1 = region.u1();
        float v1 = region.v1();
        float[] vertices = {
                0f, 0f, u0, v0, // Top Left
                0f, 1f, u0, v1, // Bottom Left
                1f, 1f, u1, v1, // Bottom Right

                0f, 0f, u0, v0, // Top Left
                1f, 1f, u1, v1, // Bottom Right
                1f, 0f, u1, v0  // Top Right
        };

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        // Position
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Texture
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    public void render() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}
