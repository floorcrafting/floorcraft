package com.boyninja1555.floorcraft.lib;

import com.boyninja1555.floorcraft.visual.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public final class BlockOutline {
    private static final float[] CUBE_LINES = {
            0,0,0, 1,0,0, 1,0,0, 1,1,0, 1,1,0, 0,1,0, 0,1,0, 0,0,0,
            0,0,1, 1,0,1, 1,0,1, 1,1,1, 1,1,1, 0,1,1, 0,1,1, 0,0,1,
            0,0,0, 0,0,1, 1,0,0, 1,0,1, 1,1,0, 1,1,1, 0,1,0, 0,1,1
    };

    private static int vao;
    private static int vbo;
    private static ShaderProgram shader;

    private static int uProjection;
    private static int uView;
    private static int uModel;
    private static int uColor;
    private static int uThickness;

    private BlockOutline() {}

    public static void init(ShaderProgram outlineShader) {
        shader = outlineShader;

        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, CUBE_LINES, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glBindVertexArray(0);

        shader.bind();
        uProjection = shader.uniformLocation("uProjection");
        uView = shader.uniformLocation("uView");
        uModel = shader.uniformLocation("uModel");
        uColor = shader.uniformLocation("uColor");
        uThickness = shader.uniformLocation("uThickness");
    }

    public static void render(Vector3i position, Matrix4f projection, Matrix4f view, float thickness, float r, float g, float b) {
        if (position == null) return;

        shader.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);

            glUniformMatrix4fv(uProjection, false, projection.get(fb));
            glUniformMatrix4fv(uView, false, view.get(fb));

            Matrix4f model = new Matrix4f().translate(position.x, position.y, position.z).scale(1.001f);
            glUniformMatrix4fv(uModel, false, model.get(fb));
        }

        glUniform3f(uColor, r, g, b);
        glUniform1f(uThickness, thickness);

        glDisable(GL_CULL_FACE);
        glBindVertexArray(vao);
        glDrawArrays(GL_LINES, 0, CUBE_LINES.length / 3);
        glBindVertexArray(0);
        glEnable(GL_CULL_FACE);
    }

    public static void cleanup() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}
