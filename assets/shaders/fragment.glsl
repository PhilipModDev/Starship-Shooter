
//Binding of the 0 texture.
uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoords;

void main(void){
//The final output state of color.
    gl_FragColor = v_color * texture2D(u_texture,v_texCoords);
}