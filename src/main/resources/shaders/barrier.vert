#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

out vec2 TexCoord;
out vec3 WorldPos;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uModel;

void main() {
    WorldPos = vec3(uModel * vec4(aPos, 1.0));
    TexCoord = aTexCoord;
    gl_Position = uProjection * uView * vec4(WorldPos, 1.0);
}
