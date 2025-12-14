package net.laurus.starfield.app.ui.render;

import java.util.Comparator;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.app.ui.component.Camera3D;
import net.laurus.starfield.model.Star;

@Slf4j
public class StarRenderer {

    private final Canvas canvas;

    private final Camera3D camera;

    /** Toggle for uniform star magnitude (base radius before depth scaling) */
    @Getter
    @Setter
    private boolean uniformMagnitude = false;

    /** Base radius for uniform magnitude stars */
    @Getter
    @Setter
    private double uniformRadius = 3.0;

    /** Minimum radius for magnitude-based stars */
    @Getter
    @Setter
    private double minRadius = 0.8;

    /** Optional logarithmic depth scaling */
    @Getter
    @Setter
    private boolean logarithmicDepth = false;

    public StarRenderer(Canvas canvas, Camera3D camera) {
        this.canvas = canvas;
        this.camera = camera;
        log.info("StarRenderer initialized with canvas={} and camera={}", canvas, camera);
    }

    /**
     * Render all stars on the canvas.
     * <p>
     * Stars are projected via the camera, radius is scaled by depth, colors are
     * mapped from the StarColour, and the hovered star is highlighted.
     *
     * @param gc          the GraphicsContext to render on
     * @param stars       list of stars to render
     * @param hoveredStar optional star to highlight
     */
    public void render(GraphicsContext gc, List<Star> stars, Star hoveredStar) {

        if (stars == null || stars.isEmpty()) {
            return;
        }

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Sort stars back-to-front (largest depth first) for proper overdraw
        stars.sort(Comparator.comparingDouble(s -> -camera.getDepth(s.getX(), s.getY(), s.getZ())));

        Color lastFill = null;

        for (Star s : stars) {

            if (s == null || !s.hasValidPosition()) {
                continue;
            }

            try {
                double[] projected = camera.project(s.getX(), s.getY(), s.getZ(), width, height);

                if (projected == null || projected.length < 3) {
                    continue;
                }

                double sx = projected[0];
                double sy = projected[1];
                double depth = projected[2];

                // Skip stars behind camera or outside canvas bounds
                if (depth <= 0 || sx < 0 || sy < 0 || sx > width || sy > height) {
                    continue;
                }

                double radius = computeRadius(s, depth);

                Color color = mapColor(s);

                // Avoid unnecessary fill calls
                if (color != lastFill) {
                    gc.setFill(color);
                    lastFill = color;
                }

                double r2 = radius * 0.5;
                gc.fillOval(sx - r2, sy - r2, radius, radius);

                // Highlight hovered star
                if (s == hoveredStar) {
                    gc.setStroke(Color.CYAN);
                    gc.strokeOval(sx - radius, sy - radius, radius * 2, radius * 2);
                }

            }
            catch (Exception ex) {
                log.error("Error rendering star '{}'", s.getName(), ex);
            }

        }

    }

    /**
     * Compute the radius of a star, always scaling by depth.
     * <p>
     * If uniformMagnitude is true, all stars start from the same base size. Depth
     * scaling can be linear or logarithmic.
     *
     * @param s     the star
     * @param depth depth from camera (projected)
     * @return radius in pixels
     */
    private double computeRadius(Star s, double depth) {
        double base = uniformMagnitude ? uniformRadius
                : Math.max(minRadius, 5.0 - s.getMagnitude() * 0.6);

        double scale = logarithmicDepth ? Math.log1p(camera.getFov()) / Math.log1p(depth)
                : camera.getFov() / depth;
        return base * scale;
    }

    /**
     * Safely map a StarColour to JavaFX Color.
     * <p>
     * If colour is null, defaults to WHITE. Component values are clamped 0-1.
     *
     * @param star the star
     * @return Color for rendering
     */
    public static Color mapColor(Star star) {

        if (star.getColour() == null) {
            return Color.WHITE;
        }

        double r = safe(star.getColour().getRed());
        double g = safe(star.getColour().getGreen());
        double b = safe(star.getColour().getBlue());
        return Color.color(r, g, b);
    }

    /** Clamp null or out-of-bounds values to 0..1 */
    private static double safe(Double value) {
        return value == null ? 0.0 : Math.min(1.0, Math.max(0.0, value));
    }

}
