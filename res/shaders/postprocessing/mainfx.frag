#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

// General Uniforms
uniform sampler2D textures[16];

// SSAO Uniforms
uniform vec2 screenSize;

uniform vec2 samples[16];
uniform mat4 invProjectionMatrix;

const int numSamples = 15;
const float kRadius = 0.006;
const float kDistanceThreshold = 4.0;

vec3 reconstructPosition(vec2 coord, float depth){
    highp vec4 vec = vec4(coord.x, coord.y, depth, 1.0);
    vec = vec * 2.0 - 1.0;
    highp vec4 r = invProjectionMatrix * vec;
    return r.xyz / r.w;
}

vec4 applySSAO(sampler2D colorTexture, sampler2D depthTexture, sampler2D normalTexture){
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
    return texture(colorTexture, textureCoords).rgba * occlusion;
}

// Border

// Depth
float linearizedDepth(sampler2D txture, vec2 coords){
    float n = 0.1; // Camera Z-Near
    float f = 1000.0; // Camera Z-Far
    float depth = texture(txture, coords).x;
    return (2.0 * n)/(f + n - depth * (f - n));
}

void main(void){
    // Textures
    sampler2D skyboxColorTexture = textures[0];
    sampler2D skyboxDepthTexture = textures[1];
    sampler2D gridColorTexture = textures[2];
    sampler2D gridDepthTexture = textures[3];
    sampler2D asteroidColorTexture = textures[4];
    sampler2D asteroidDepthTexture = textures[5];
    sampler2D asteroidNormalTexture = textures[6];

    // Post-processing
    vec4 skyboxColor = texture(skyboxColorTexture, textureCoords).rgba;
    float skyboxDepth = linearizedDepth(skyboxDepthTexture, textureCoords);

    vec4 gridColor = texture(gridColorTexture, textureCoords).rgba;
    float gridDepth = linearizedDepth(gridDepthTexture, textureCoords);

    vec4 asteroidColor = applySSAO(asteroidColorTexture, asteroidDepthTexture, asteroidNormalTexture);
    float asteroidDepth = linearizedDepth(asteroidDepthTexture, textureCoords);

    // Output
    int outputCount = 3;
    vec4 outputColor[outputCount];
    float outputDepth[outputCount];

    outputColor[0] = skyboxColor;
    outputDepth[0] = skyboxDepth;

    outputColor[1] = gridColor;
    outputDepth[1] = gridDepth;

    outputColor[2] = asteroidColor;
    outputDepth[2] = asteroidDepth;


}