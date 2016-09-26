#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform int numTextures;
uniform sampler2D colorTextures[8];
uniform sampler2D depthTextures[8];

float linearizedDepth(sampler2D txture, vec2 coords){
    float n = 0.1; // Camera Z-Near
    float f = 1000.0; // Camera Z-Far
    float depth = texture(txture, coords).x;
    return (2.0 * n)/(f + n - depth * (f - n));
}

void main(void){
    int textureIndex = 0;
    float depthVal = 1.0;
    for(int i=0; i<numTextures; i++){
        float depth = texture(depthTextures[i], textureCoords).x; //linearizedDepth(depthTextures[i], textureCoords);
        if(depth < depthVal){
            depthVal = depth;
            textureIndex = i;
        }
    }

    out_Color = texture(colorTextures[textureIndex], textureCoords).rgba;
}