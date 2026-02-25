#version 330 core

out vec4 FragColor;
uniform vec3 uSkyColor;

void main() {
    FragColor = vec4(uSkyColor, 1.0);
}
