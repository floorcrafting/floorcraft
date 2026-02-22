#version 330 core

out vec4 FragColor;

in vec2 TexCoord;

uniform sampler2D uTexture;

void main() {
    vec4 sampled = texture(uTexture, TexCoord);

    if (sampled.a < 0.1) {
        discard;
    }

    FragColor = sampled;
}