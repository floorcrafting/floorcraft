#version 330 core

in vec2 vTexCoord;
in vec3 vNormal;
in vec3 vFragPos;

uniform sampler2D uTexture;
uniform mat4 uView;

out vec4 FragColor;

void main() {
    vec4 texColor = texture(uTexture, vTexCoord);
    vec3 cameraPos = inverse(mat3(uView)) * vec3(0.0, 0.0, 5.0);
    vec3 lightDir = normalize(cameraPos - vFragPos);
    float diff = 1.0;     // max(dot(normalize(vNormal), lightDir), 0.0);
    float ambient = 1.0;  // 0.3
    vec3 finalColor = texColor.rgb * (ambient + (1.0 - ambient) * diff);
    FragColor = vec4(finalColor, texColor.a);
}
