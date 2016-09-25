#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform vec2 screenSize;
uniform sampler2D colorTexture;
uniform sampler2D depthTexture;

vec4 borderColor = vec4(0.5, 0.4, 0.8, 0.6);
int borderSize = 2;
float threshold = 0.0015;

float linearizedDepth(sampler2D txture, vec2 coords){
    float n = 0.1; // Camera Z-Near
    float f = 1000.0; // Camera Z-Far
    float depth = texture(txture, coords).x;
    return (2.0 * n)/(f + n - depth * (f - n));
}

void main(void){
    vec4 borderDiffuse = vec4(0);
    float avg = 0.0;
    int r = borderSize;
    for(int i=-r; i<=r; i++){
        for(int j=-r; j<=r; j++){
            avg += linearizedDepth(depthTexture, textureCoords + vec2(i, j)/(screenSize));
        }
    }
    avg /= pow(r*2 + 1, 2);
    float c = linearizedDepth(depthTexture, textureCoords);
    if(c - avg > threshold){
        borderDiffuse = borderColor;
    }

    vec3 colorDiffuse = texture(colorTexture, textureCoords).rgb;

    out_Color = vec4(mix(colorDiffuse, borderDiffuse.rgb, borderDiffuse.a), 1);
}