varying vec4 fragColor;

void main() {
    // Centered coordinates (0.0-1.0 range)
    vec2 coord = gl_PointCoord - vec2(0.5);
    float dist = length(coord);

    // Smooth exponential falloff for glow
    float alpha = exp(-dist * 5.0);

    // Optional inner bright rim
    alpha += 0.3 * exp(-dist * 15.0);

    // Clamp final alpha to [0,1]
    alpha = clamp(alpha, 0.0, 1.0);

    gl_FragColor = vec4(fragColor.rgb, fragColor.a * alpha);
}
