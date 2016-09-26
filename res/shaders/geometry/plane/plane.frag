#version 400 core

in vec3 fragPosition;
in vec3 fragColor;
in vec3 fragNormal;

out vec4 out_Color;

uniform vec3 lightAmbient;
uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform mat4 projectionMatrix;

void main(void){
    vec3 lightPos = normalize(lightPosition);

    // Lighting
    float cosTheta = dot(fragNormal, lightPos);
    float brightness = clamp(cosTheta, 0, 1);

    vec3 diffuseColor = fragColor;
    vec3 ambientColor = lightAmbient * diffuseColor;

    out_Color = vec4(0.1, 0.6, 0.8, 0.4); //vec4(ambientColor + diffuseColor * lightColor * brightness, 1.0);
}