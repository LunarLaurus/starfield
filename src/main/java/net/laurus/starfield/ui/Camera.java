package net.laurus.starfield.ui;

import lombok.Getter;

/**
 * Simple 2D camera for panning and zooming a canvas. No internal event
 * listeners; input is handled externally.
 */
@Getter
public class Camera {

    private double offsetX = 0;

    private double offsetY = 0;

    private double scale = 1.0;

    public Camera() {
        // No listeners here
    }

    /** Pan the camera by dx/dy in world units */
    public void drag(double dx, double dy) {
        offsetX += dx / scale;
        offsetY += dy / scale;
    }

    /** Zoom camera by factor around a screen coordinate */
    public void zoom(double factor, double centerX, double centerY) {
        offsetX = (offsetX - centerX / scale) * factor + centerX / (scale * factor);
        offsetY = (offsetY - centerY / scale) * factor + centerY / (scale * factor);
        scale *= factor;
    }

    /** Convert world X to screen X */
    public double worldToScreenX(double x) {
        return (x + offsetX) * scale;
    }

    /** Convert world Y to screen Y */
    public double worldToScreenY(double y) {
        return (y + offsetY) * scale;
    }

    /** Convert radius in world units to screen units */
    public double worldToScreenRadius(double r) {
        return r * scale;
    }

}
