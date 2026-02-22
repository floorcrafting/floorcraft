#version 330 core

layout(lines) in;
layout(triangle_strip, max_vertices = 4) out;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uModel;
uniform float uThickness;

void main() {
    vec4 p0 = uProjection * uView * uModel * vec4(gl_in[0].gl_Position.xyz, 1.0);
    vec4 p1 = uProjection * uView * uModel * vec4(gl_in[1].gl_Position.xyz, 1.0);

    vec2 dir = normalize((p1.xy / p1.w) - (p0.xy / p0.w));
    vec2 offset = vec2(-dir.y, dir.x) * uThickness * 0.5;

    vec4 offset0 = vec4(offset, 0.0, 0.0) * p0.w;
    vec4 offset1 = vec4(offset, 0.0, 0.0) * p1.w;

    gl_Position = p0 + vec4(-offset0.xy, 0.0, 0.0); EmitVertex();
    gl_Position = p0 + vec4( offset0.xy, 0.0, 0.0); EmitVertex();
    gl_Position = p1 + vec4(-offset1.xy, 0.0, 0.0); EmitVertex();
    gl_Position = p1 + vec4( offset1.xy, 0.0, 0.0); EmitVertex();
    EndPrimitive();
}
