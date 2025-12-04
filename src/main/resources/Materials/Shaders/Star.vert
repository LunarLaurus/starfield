uniform mat4 g_WorldViewProjectionMatrix;
uniform float PointSize;

attribute vec3 Position;
attribute vec4 ColorParam;
attribute float SizeParam; // optional, can be set per star

varying vec4 fragColor;

void main() {
    gl_Position = g_WorldViewProjectionMatrix * vec4(Position, 1.0);
    
    // Use per-star size if available, otherwise default PointSize
    gl_PointSize = (SizeParam > 0.0) ? SizeParam : PointSize;

    fragColor = ColorParam;
}
