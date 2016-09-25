#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform int effectIndex;
uniform sampler2D colorTexture1;
uniform sampler2D depthTexture1;
uniform sampler2D colorTexture2;
uniform sampler2D depthTexture2;

const int   EFFECT_NONE     = 0,
            EFFECT_ADD      = 1;

float linearizedDepth(sampler2D txture, vec2 coords){
    float n = 0.1; // Camera Z-Near
    float f = 1000.0; // Camera Z-Far
    float depth = texture(txture, coords).x;
    return (2.0 * n)/(f + n - depth * (f - n));
}

void main(void){
    switch(effectIndex){
        case EFFECT_ADD:
            float depth1 = linearizedDepth(depthTexture1, textureCoords);
            float depth2 = linearizedDepth(depthTexture2, textureCoords);
            if(depth1 < depth2){
                out_Color = vec4(texture(colorTexture1, textureCoords).rgb, 1.0);
            }else{
                out_Color = vec4(texture(colorTexture2, textureCoords).rgb, 1.0);
            }
            // out_Color = vec4(mix(texture(colorTexture1, textureCoords).rgb, texture(colorTexture2, textureCoords).rgb, texture(colorTexture2, textureCoords).a), );
            break;
        default:
            out_Color = vec4(texture(colorTexture1, textureCoords).rgb, 1.0);
            break;
    }
}