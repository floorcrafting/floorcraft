#version 330 core

out vec4 FragColor;
in vec3 WorldPos;

uniform float uTime;
uniform vec3 uPlayerPosition;

const float RADIUS = 30.0; // Radius splotches are visible
const float SPEED = 0.3; // Speed splotches rise
const float SCALE = 0.15; // Size of splotches (smaller = bigger blobs)
const float INTENSITY = 0.8; // Overall brightness/opacity (0-1)
const float SOFTNESS = 0.5; // Blur/softness of splotch edges
const float THRESHOLD = 0.8; // How much of the barrier is empty space

float hash(float n) { return fract(sin(n) * 43758.5453123); }
float noise(vec3 x) {
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    float n = p.x + p.y * 57.0 + 113.0 * p.z;
    return mix(mix(mix(hash(n + 0.0), hash(n + 1.0), f.x), mix(hash(n + 57.0), hash(n + 58.0), f.x), f.y), mix(mix(hash(n + 113.0), hash(n + 114.0), f.x), mix(hash(n + 170.0), hash(n + 171.0), f.x), f.y), f.z);
}

void main() {
    float dist = distance(uPlayerPosition, WorldPos);

    // Radius check
    if (dist > RADIUS) discard;

    // Scale and speed
    vec3 noisePos = WorldPos * SCALE;
    noisePos.y -= uTime * SPEED;

    // Splotches
    float n = noise(noisePos);
    n += 0.4 * noise(noisePos * 2.5 + uTime * (SPEED * 0.3));

    // Edge softness
    float splotchMask = smoothstep(THRESHOLD - SOFTNESS, THRESHOLD + SOFTNESS, n);

    if (splotchMask < 0.01) discard;

    // Colors
    vec3 minColor = vec3(0.349, 0.059, 0.78);
    vec3 maxColor = vec3(0.475, 0.173, 0.929);

    // Mixes colors (noise-based)
    vec3 finalColor = mix(minColor, maxColor, splotchMask);

    // Edge fading
    float edgeFade = 1.0 - smoothstep(0.0, RADIUS, dist);

    // Flicker
    float flicker = sin(uTime * 30.0) * 0.03 + 0.97;

    // Final result
    FragColor = vec4(finalColor * flicker, splotchMask * edgeFade * INTENSITY);
}
