#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colorTexture;

void main(void){
    out_Color = vec4(texture(colorTexture, textureCoords).rgb, 1.0);
}