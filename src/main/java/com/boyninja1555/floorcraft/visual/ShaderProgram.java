package com.boyninja1555.floorcraft.visual;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int programId;

    public ShaderProgram(String vertexPath, String fragmentPath) throws Exception {
        int vertex = loadShader(vertexPath, GL_VERTEX_SHADER);
        int fragment = loadShader(fragmentPath, GL_FRAGMENT_SHADER);

        programId = glCreateProgram();
        glAttachShader(programId, vertex);
        glAttachShader(programId, fragment);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader linking failed! " + glGetProgramInfoLog(programId));

        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public int uniformLocation(String name) {
        return glGetUniformLocation(programId, name);
    }

    public void uniformInt(String name, int value) {
        glUniform1i(uniformLocation(name), value);
    }

    private int loadShader(String path, int type) throws Exception {
        String source;

        try (var is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("Shader not found! " + path);

            source = new String(is.readAllBytes());
        }

        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader compilation failed! " + glGetShaderInfoLog(shaderId));

        return shaderId;
    }
}
