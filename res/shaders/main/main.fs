#version 400 core

flat in vec3 fragColor;
flat in vec3 surfaceNormal;
in vec3 toLightVector;

out vec4 out_Color;

uniform vec3 lightColor;
uniform mat4 projectionMatrix;

void main(void){

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float nDotl = dot(unitNormal, unitLightVector);

    //float brightness = max(nDotl * lightFactor, 0.4);
    //vec3 diffuse = brightness * lightColor;
    //out_Color = vec4(fragColor, 1.0) * vec4(diffuse, 1.0);

    float minLight = 0.4;
    float maxLight = 1.0;
    float brightness = min(maxLight, max(nDotl, minLight));

    float tintLevel = 0.6;
    vec3 diffuse = mix(vec3(1 - tintLevel, 0.5, tintLevel), vec3(tintLevel, 0.5, 1 - tintLevel), (brightness - minLight)/(maxLight - minLight));
    out_Color = vec4(fragColor * diffuse, 1.0);

    out_Color = vec4(1);
}