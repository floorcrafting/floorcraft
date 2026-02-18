package com.boyninja1555.floorcraft.mesh;

import com.boyninja1555.floorcraft.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public abstract class Mesh {
    public final Texture texture;
    public final boolean transparent;
    private final Vector3f position;
    private final Matrix4f model = new Matrix4f().identity();
    private int vao;
    private int vbo;
    private int vertexCount;

    public Mesh(Vector3f position, Texture texture, boolean transparent) {
        this.position = position;
        this.texture = texture;
        this.transparent = transparent;
    }

    public Mesh(Vector3i position, Texture texture, boolean transparent) {
        this.position = new Vector3f(position.x, position.y, position.z);
        this.texture = texture;
        this.transparent = transparent;
    }

    public void init() {
        float[] vertices = vertices();
        model.translate(position);
        vertexCount = vertices.length / 8;
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        // Positions
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Normals
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // UVs
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        glBindVertexArray(0);
    }

    public Vector3f position() {
        return position;
    }

    public void translate(float x, float y, float z) {
        model.translate(new Vector3f(x, y, z));
    }

    public void rotate(float angleRadians, float x, float y, float z) {
        model.rotate(angleRadians, new Vector3f(x, y, z));
    }

    public void scale(float x, float y, float z) {
        model.scale(x, y, z);
    }

    /**
     * Gets the model matrix to send to shader
     **/
    public Matrix4f modelMatrix() {
        return model;
    }

    /**
     * Resets model matrix to identity
     **/
    public void resetTransform() {
        model.identity();
        model.translate(position);
    }

    public abstract float[] vertices();
}
