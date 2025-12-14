package net.laurus.starfield.app.ui.render;

import javafx.scene.canvas.Canvas;
import lombok.Getter;
import net.laurus.starfield.app.ui.component.Camera3D;
import net.laurus.starfield.model.Star;

@Getter
public class StarProjector {

    private final Camera3D camera;

    private final Canvas canvas;

    public StarProjector(Canvas canvas, Camera3D camera) {
        this.canvas = canvas;
        this.camera = camera;
    }

    /**
     * Project a star to screen coordinates.
     *
     * @param s the star
     * @return {screenX, screenY, depth} or null if invalid
     */
    public double[] project(Star s) {

        if (s == null || !s.hasValidPosition()) {
            return null;
        }

        return camera.project(s.getX(), s.getY(), s.getZ(), canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Get depth of star relative to camera (for sorting)
     */
    public double getDepth(Star s) {

        if (s == null || !s.hasValidPosition()) {
            return Double.NaN;
        }

        return camera.getDepth(s.getX(), s.getY(), s.getZ());
    }

    /**
     * Convert magnitude to base radius
     */
    public double computeRadius(
            Star s,
            boolean uniform,
            double uniformRadius,
            double minRadius,
            boolean logarithmic
    ) {
        double base = uniform ? uniformRadius : Math.max(minRadius, 5.0 - s.getMagnitude() * 0.6);
        double depth = getDepth(s);

        if (Double.isNaN(depth)) {
            return base;
        }

        double scale = logarithmic ? Math.log1p(camera.getFov()) / Math.log1p(depth)
                : camera.getFov() / depth;
        return base * scale;
    }

    /**
     * Map StarColour to JavaFX Color safely
     */
    public static javafx.scene.paint.Color mapColor(Star s) {

        if (s.getColour() == null) {
            return javafx.scene.paint.Color.WHITE;
        }

        double r = s.getColour().getRed() == null ? 0.0 : clamp(s.getColour().getRed());
        double g = s.getColour().getGreen() == null ? 0.0 : clamp(s.getColour().getGreen());
        double b = s.getColour().getBlue() == null ? 0.0 : clamp(s.getColour().getBlue());
        return javafx.scene.paint.Color.color(r, g, b);
    }

    private static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

}
