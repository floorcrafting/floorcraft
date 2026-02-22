#version 330 core

in vec3 vNormal;
in vec2 vTexCoord;

uniform sampler2D uTexture;

out vec4 FragColor;

void main() {
    vec4 texColor = texture(uTexture, vTexCoord);
    float brightness = 1.0;
    if (vNormal.y > 0.5)       brightness = 1.0; // Top
    else if (vNormal.y < -0.5) brightness = 0.5; // Bottom
    else if (vNormal.z > 0.5)  brightness = 0.8; // Front
    else if (vNormal.z < -0.5) brightness = 0.8; // Back
    else if (vNormal.x > 0.5)  brightness = 0.6; // Right
    else if (vNormal.x < -0.5) brightness = 0.6; // Left
    FragColor = vec4(texColor.rgb * brightness, texColor.a);
}
