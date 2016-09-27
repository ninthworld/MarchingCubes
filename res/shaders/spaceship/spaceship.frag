#version 400 core

in vec3 fragPosition;
in float fragMaterial;
in vec3 fragNormal;

out vec4 out_Color;

uniform vec3 lightAmbient;
uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform mat4 projectionMatrix;

uniform sampler2D texture0;
uniform sampler2D texture1;

uniform sampler2D normal0;
uniform sampler2D normal1;

void main(void){
    vec3 lightPos = normalize(lightPosition);

    // Texture XYZ Blending
    vec3 blending = abs(fragNormal);
    blending = normalize(max(blending, 0.00001));
    float b = (blending.x + blending.y + blending.z);
    blending /= vec3(b, b, b);

    // Multi-texture Coords
    float matIndex = fragMaterial - 1.0;
    float scale = 0.2; //0.08;
    vec2 xAxis = fragPosition.yz * scale;
    vec2 yAxis = fragPosition.xz * scale;
    vec2 zAxis = fragPosition.xy * scale;

    // Multi-texture Color
    vec4 color0 = texture(texture0, xAxis) * blending.x + texture(texture0, yAxis) * blending.y + texture(texture0, zAxis) * blending.z;
    vec4 color1 = texture(texture1, xAxis) * blending.x + texture(texture1, yAxis) * blending.y + texture(texture1, zAxis) * blending.z;
    vec4 blendedColor = color1*matIndex + color0*(1.0 - matIndex);

    // Multi-texture Normal
    vec4 norm0 = texture(normal0, xAxis) * blending.x + texture(normal0, yAxis) * blending.y + texture(normal0, zAxis) * blending.z;
    vec4 norm1 = texture(normal1, xAxis) * blending.x + texture(normal1, yAxis) * blending.y + texture(normal1, zAxis) * blending.z;
    vec4 blendedNormal = norm0*matIndex + norm1*(1.0 - matIndex);

    vec3 normalTanSpace = normalize(blendedNormal.rgb * 2.0 - 1.0) * mat3(1, 0, 0, 0, 0, 1, 0, -1, 0);

    // Lighting
    float cosTheta = dot(normalize((normalTanSpace - vec3(0, 1, 0)) + fragNormal), lightPos);
    //float cosTheta = dot(fragNormal, lightPos);
    float brightness = clamp(cosTheta, 0, 1);

    vec3 diffuseColor = blendedColor.rgb; //vec3(0.4, 0.3, 0.3); //
    vec3 ambientColor = lightAmbient * diffuseColor;

    out_Color = vec4(ambientColor + diffuseColor * lightColor * brightness, 1.0);
}