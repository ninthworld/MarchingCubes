#version 400 core

in vec3 fragColor;
in vec3 surfaceNormal;
//in vec3 toLightVector;

out vec4 out_Color;

uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform mat4 projectionMatrix;

void main(void){

    float cosTheta = dot(surfaceNormal, lightPosition);
    float brightness = clamp(cosTheta, 0, 1);

    vec3 diffuseColor = fragColor;
    vec3 ambientColor = vec3(0.1, 0.1, 0.1) * diffuseColor;

    out_Color = vec4(ambientColor + diffuseColor * lightColor * brightness, 1.0);
}