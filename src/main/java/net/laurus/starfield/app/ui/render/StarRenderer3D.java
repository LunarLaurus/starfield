package net.laurus.starfield.app.ui.render;

import java.util.Comparator;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.model.Star;

@Slf4j
@RequiredArgsConstructor
public class StarRenderer3D {

    private final Canvas canvas;

    private final StarProjector projector;

    @Getter
    @Setter
    private double baseRadius = 2.5;

    @Getter
    @Setter
    private double minRadius = 0.5;

    @Getter
    @Setter
    private double rotationYaw = 0;

    @Getter
    @Setter
    private double rotationPitch = 0;

    @Getter
    @Setter
    private double maxBrightness = 5.0;

    @Getter
    @Setter
    private double minBrightness = 0.25;

    @Getter
    @Setter
    private Color hoverColor = Color.CYAN;

    private final double nearDistance = 25; // stars closer than this = max brightness

    private final double farDistance = 2000; // stars beyond this = min brightness

    public void render(GraphicsContext gc, List<Star> stars, Star hoveredStar) {

        if (stars == null || stars.isEmpty()) {
            return;
        }

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Precompute rotation values
        double radYaw = Math.toRadians(rotationYaw);
        double sinYaw = Math.sin(radYaw);
        double cosYaw = Math.cos(radYaw);
        double radPitch = Math.toRadians(rotationPitch);
        double sinPitch = Math.sin(radPitch);
        double cosPitch = Math.cos(radPitch);

        // Sort stars back-to-front for proper rendering
        stars.sort(Comparator.comparingDouble(projector::getDepth).reversed());

        for (Star s : stars) {

            if (s == null || !s.hasValidPosition()) {
                continue;
            }

            try {
                // Rotate star
                double[] rotated = rotateWorld(
                        s.getX(), s.getY(), s.getZ(), sinYaw, cosYaw, sinPitch, cosPitch
                );

                // Project to screen
                double[] p = projector
                        .getCamera()
                        .project(rotated[0], rotated[1], rotated[2], width, height);

                if (p == null || p.length < 3) {
                    continue;
                }

                double sx = p[0], sy = p[1], depth = p[2];

                // Cull offscreen or behind camera
                if (depth <= 0 || sx < 0 || sy < 0 || sx > width || sy > height) {
                    continue;
                }

                // Compute radius scaled by depth (logarithmic)
                double radius = Math
                        .max(
                                minRadius, baseRadius * Math.log1p(projector.getCamera().getFov())
                                        / Math.log1p(depth)
                        );

                // Compute depth-based brightness with near/far scaling
                double brightness = computeBrightnessWithDistance(depth);

                // Apply brightness to star color
                Color color = StarProjector.mapColor(s);
                color = Color
                        .color(
                                clamp(color.getRed() * brightness), clamp(
                                        color.getGreen() * brightness
                                ), clamp(color.getBlue() * brightness), 1.0
                        );

                gc.setFill(color);
                double r2 = radius * 0.5;
                gc.fillOval(sx - r2, sy - r2, radius, radius);

                // Highlight hovered star
                if (s == hoveredStar) {
                    gc.setStroke(hoverColor);
                    gc.setLineWidth(1.5);
                    gc.strokeOval(sx - radius, sy - radius, radius * 2, radius * 2);
                }

            }
            catch (Exception ex) {
                log.error("Error rendering star '{}'", s.getName(), ex);
            }

        }

    }

    private double[] rotateWorld(
            double x,
            double y,
            double z,
            double sinYaw,
            double cosYaw,
            double sinPitch,
            double cosPitch
    ) {
        // Yaw (Y-axis)
        double x1 = x * cosYaw - z * sinYaw;
        double z1 = x * sinYaw + z * cosYaw;
        // Pitch (X-axis)
        double y1 = y * cosPitch - z1 * sinPitch;
        double z2 = y * sinPitch + z1 * cosPitch;
        return new double[] {
                x1, y1, z2
        };
    }

    /**
     * Brightness scaling: max brightness within nearDistance, min at farDistance,
     * fade logarithmically
     */
    private double computeBrightnessWithDistance(double depth) {

        if (depth <= nearDistance) {
            return maxBrightness;
        }

        if (depth >= farDistance) {
            return minBrightness;
        }

        // Linear interpolation in log space
        double logDepth = Math.log1p(depth);
        double logNear = Math.log1p(nearDistance);
        double logFar = Math.log1p(farDistance);

        double t = (logDepth - logNear) / (logFar - logNear);
        double brightness = maxBrightness * (1.0 - t) + minBrightness * t;
        return clamp(brightness);
    }

    private double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

}
