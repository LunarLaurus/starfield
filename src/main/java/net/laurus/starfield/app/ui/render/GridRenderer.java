package net.laurus.starfield.app.ui.render;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.laurus.starfield.app.ui.component.Camera3D;

/**
 * Draws a 3D spatial grid with a radial color gradient from the origin. Grid
 * lines smoothly transition: LightGreen → DarkGreen → LightBlue → DarkBlue →
 * DarkGray. Color depends on distance from origin (0,0,0).
 */
public class GridRenderer {

    private static final double STEP = 50; // Grid spacing

    private final Camera3D camera;

    public GridRenderer(Camera3D camera) {
        this.camera = camera;
    }

    /**
     * Draw the grid on the canvas with a radial gradient.
     *
     * @param gc          Graphics context
     * @param canvas      Canvas to draw on
     * @param maxDistance Maximum distance from origin
     */
    public void draw(GraphicsContext gc, Canvas canvas, double maxDistance) {
        gc.setLineWidth(0.3);

        for (double i = -maxDistance; i <= maxDistance; i += STEP) {

            for (double j = -maxDistance; j <= maxDistance; j += STEP) {

                // Compute radial distance from origin for coloring
                double distance = Math.sqrt(i * i + j * j);
                double t = Math.min(distance / maxDistance, 1.0); // normalized 0..1
                gc.setStroke(getColorForNormalizedDistance(t));

                // Vertical line along Z-axis
                double[] v1 = camera
                        .project(i, 0, -maxDistance, canvas.getWidth(), canvas.getHeight());
                double[] v2 = camera
                        .project(i, 0, maxDistance, canvas.getWidth(), canvas.getHeight());
                gc.strokeLine(v1[0], v1[1], v2[0], v2[1]);

                // Horizontal line along X-axis
                double[] h1 = camera
                        .project(-maxDistance, 0, j, canvas.getWidth(), canvas.getHeight());
                double[] h2 = camera
                        .project(maxDistance, 0, j, canvas.getWidth(), canvas.getHeight());
                gc.strokeLine(h1[0], h1[1], h2[0], h2[1]);
            }

        }

    }

    /**
     * Maps a normalized distance [0..1] to a color gradient: LightGreen → DarkGreen
     * → LightBlue → DarkBlue → DarkGray
     */
    private Color getColorForNormalizedDistance(double t) {
        double[] stops = {
                0.0, 0.25, 0.5, 0.75, 1.0
        };
        Color[] colors = {
                Color.LIGHTGREEN, Color.DARKGREEN, Color.LIGHTBLUE, Color.DARKBLUE, Color.DARKGRAY
        };

        // Find segment
        int i = 0;

        while (i < stops.length - 1 && t > stops[i + 1]) {
            i++;
        }

        double localT = (t - stops[i]) / (stops[i + 1] - stops[i]);
        return interpolateColor(colors[i], colors[i + 1], localT);
    }

    /**
     * Linear interpolation between two colors.
     */
    private Color interpolateColor(Color c1, Color c2, double t) {
        double r = c1.getRed() + (c2.getRed() - c1.getRed()) * t;
        double g = c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t;
        double b = c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t;
        double a = c1.getOpacity() + (c2.getOpacity() - c1.getOpacity()) * t;
        return new Color(r, g, b, a);
    }

}
