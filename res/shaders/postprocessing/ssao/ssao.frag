#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform vec2 screenSize;
uniform sampler2D colorTexture;
uniform sampler2D depthTexture;
uniform sampler2D normalTexture;

uniform highp vec2 samples[16];
uniform highp mat4 invProjectionMatrix;

vec3 reconstructPosition(in highp vec2 coord, in highp float depth){
    highp vec4 vec = vec4(coord.x, coord.y, depth, 1.0);
    vec = vec * 2.0 - 1.0;
    highp vec4 r = invProjectionMatrix * vec;
    return r.xyz / r.w;
}

const highp int numSamples = 20;
const highp float kRadius = 0.008;
const highp float kDistanceThreshold = 4.0;

void main(void){

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
    vec3 colorDiffuse = texture(colorTexture, textureCoords).rgb;
    out_Color = vec4(colorDiffuse * occlusion, 1);
}