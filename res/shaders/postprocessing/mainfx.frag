#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

// General Uniforms
uniform sampler2D textures[16];

// SSAO Uniforms
uniform vec2 samples[16];
uniform mat4 invProjectionMatrix;

const int numSamples = 15;
const float kRadius = 0.006;
const float kDistanceThreshold = 4.0;

// Border Uniforms
uniform vec2 screenSize;

vec3 reconstructPosition(vec2 coord, float depth){
    highp vec4 vec = vec4(coord.x, coord.y, depth, 1.0);
    vec = vec * 2.0 - 1.0;
    highp vec4 r = invProjectionMatrix * vec;
    return r.xyz / r.w;
}

vec4 applySSAO(vec4 color, sampler2D depthTexture, sampler2D normalTexture){
    highp ivec2 texsize = textureSize(depthTexture, 0);
    highp vec3 normal = texture(normalTexture, textureCoords).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    highp float depth = texture(depthTexture, textureCoords).r;
    highp vec3 position = reconstructPosition(textureCoords, depth);

    highp float occlusion = 0.0;
    for(int i=0; i<numSamples; ++i){
        highp vec2 sampleTexture = textureCoords + (samples[i] * kRadius);
        highp float sampleDepth = texture(depthTexture, sampleTexture).r;
        highp vec3 samplePos = reconstructPosition(sampleTexture, sampleDepth);
        highp vec3 diffVec = samplePos - position;
        highp float dist = length(diffVec);
        highp vec3 sampleDir = diffVec * 1.0/dist;
        highp float cosine = max(dot(normal, sampleDir), 0.0);

        highp float a = 1.0 - smoothstep(kDistanceThreshold, kDistanceThreshold * 2.0, dist);
        highp float b = cosine;
        occlusion += (b*a);
    }

    occlusion = 1.0 - occlusion / float(numSamples);
    return vec4(color.rgb * occlusion, color.a);
}

// Depth
float linearizedDepth(sampler2D txture, vec2 coords){
    float n = 0.1; // Camera Z-Near
    float f = 1000.0; // Camera Z-Far
    float depth = texture(txture, coords).x;
    return (2.0 * n)/(f + n - depth * (f - n));
}


// Border
vec4 applyBorderColor(vec4 color, sampler2D depthTexture, vec4 borderColor, int borderSize, float borderThreshold){
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
    if(c - avg > borderThreshold){
        borderDiffuse = borderColor;
    }

    return mix(color, borderDiffuse.rgba, borderDiffuse.a);
}

float applyBorderDepth(float depth, sampler2D depthTexture, int borderSize, float borderThreshold){
    float avg = 0.0;
    int r = borderSize;
    float minDepth = 1.0;
    for(int i=-r; i<=r; i++){
        for(int j=-r; j<=r; j++){
            float d = linearizedDepth(depthTexture, textureCoords + vec2(i, j)/(screenSize));
            avg += d;
            minDepth = min(minDepth, d);
        }
    }
    avg /= pow(r*2 + 1, 2);
    float c = linearizedDepth(depthTexture, textureCoords);
    if(c - avg > borderThreshold){
        depth = minDepth;
    }
    return depth;
}

void main(void){
    // Post-processing
    vec4 skyboxColor = texture(textures[0], textureCoords).rgba;
    float skyboxDepth = linearizedDepth(textures[1], textureCoords);

    vec4 gridColor = texture(textures[2], textureCoords).rgba;
    float gridDepth = linearizedDepth(textures[3], textureCoords);

    vec4 cuboidColor = texture(textures[4], textureCoords).rgba;
    float cuboidDepth = linearizedDepth(textures[5], textureCoords);

    vec4 asteroidColor = applySSAO(texture(textures[6], textureCoords).rgba, textures[7], textures[8]); //texture(textures[6], textureCoords).rgba;
    float asteroidDepth = linearizedDepth(textures[7], textureCoords);

    vec4 spaceShipColor = texture(textures[9], textureCoords).rgba;
    float spaceShipDepth = linearizedDepth(textures[10], textureCoords);

    gridColor = applyBorderColor(gridColor, textures[3], vec4(0.1, 0.6, 0.8, 0.6), 1, 0.0015);
    gridDepth = applyBorderDepth(gridDepth, textures[3], 2, 0.0015);

    cuboidColor = applyBorderColor(cuboidColor, textures[5], vec4(0.1, 0.6, 0.2, 0.6), 1, 0.0015);
    cuboidDepth = applyBorderDepth(cuboidDepth, textures[5], 2, 0.0015);

    asteroidColor = applyBorderColor(asteroidColor, textures[7], vec4(0.8, 0.4, 0.1, 0.75), 2, 0.1);
    asteroidDepth = applyBorderDepth(asteroidDepth, textures[7], 2, 0.0015);

    // Output
    int outputCount = 5;
    vec4 outputColor[] = vec4[](skyboxColor, gridColor, cuboidColor, asteroidColor, spaceShipColor);
    float outputDepth[] = float[](skyboxDepth, gridDepth, cuboidDepth, asteroidDepth, spaceShipDepth);

    // Sort output based on depth
    int indexOrder[] = int[](0, 1, 2, 3, 4);
    for(int i=(outputCount-1); i>=0; i--){
        for(int j=1; j<=i; j++){
            if(outputDepth[indexOrder[j-1]] < outputDepth[indexOrder[j]]){
                int temp = indexOrder[j-1];
                indexOrder[j-1] = indexOrder[j];
                indexOrder[j] = temp;
            }
        }
    }

    // Draw layers in order
    out_Color = vec4(0, 0, 0, 1);
    for(int i=0; i<outputCount; i++){
        out_Color = mix(out_Color, outputColor[indexOrder[i]], outputColor[indexOrder[i]].a);
    }
}