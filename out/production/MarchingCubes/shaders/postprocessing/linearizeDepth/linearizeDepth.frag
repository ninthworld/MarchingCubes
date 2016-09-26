#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D depthTexture;

float linearizedDepth(sampler2D txture, vec2 coords){
    float n = 0.1; // Camera Z-Near
    float f = 1000.0; // Camera Z-Far
    float depth = texture(txture, coords).x;
    return (2.0 * n)/(f + n - depth * (f - n));
}

void main(void){
    out_Color = vec4(linearizedDepth(depthTexture, textureCoords));
}