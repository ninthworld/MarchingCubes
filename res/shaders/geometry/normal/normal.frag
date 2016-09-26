#version 400 core

in vec3 fragPosition;
in vec3 fragColor;
in vec3 fragNormal;

out vec4 out_Color;

void main(void){
    vec3 normal = normalize(fragNormal);
    out_Color = vec4(normal.x * 0.5 + 0.5, normal.y * 0.5 + 0.5, normal.z * 0.5 + 0.5, 1.0);
}