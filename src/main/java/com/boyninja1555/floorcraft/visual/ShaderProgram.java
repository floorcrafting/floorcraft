package com.boyninja1555.floorcraft.visual;

import java.nio.file.Path;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class ShaderProgram {
    private final int programId;

    public ShaderProgram(String vertexPath, String fragmentPath, String geometryPath) throws Exception {
        int vertex = loadShader(Path.of("shaders", vertexPath).toString(), GL_VERTEX_SHADER);
        int fragment = loadShader(Path.of("shaders", fragmentPath).toString(), GL_FRAGMENT_SHADER);
        int geometry = 0;
        if (geometryPath != null)
            geometry = loadShader(Path.of("shaders", geometryPath).toString(), GL_GEOMETRY_SHADER);

        programId = glCreateProgram();
        glAttachShader(programId, vertex);
        glAttachShader(programId, fragment);

        if (geometryPath != null) glAttachShader(programId, geometry);

        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Shader linking failed! " + glGetProgramInfoLog(programId));

        glDeleteShader(vertex);
        glDeleteShader(fragment);

        if (geometryPath != null) glDeleteShader(geometry);
    }

    public ShaderProgram(String vertexPath, String fragmentPath) throws Exception {
        this(vertexPath, fragmentPath, null);
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
