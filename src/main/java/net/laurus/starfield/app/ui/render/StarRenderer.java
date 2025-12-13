package net.laurus.starfield.app.ui.render;

import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.laurus.starfield.app.ui.component.Camera3D;
import net.laurus.starfield.model.Star;

public class StarRenderer {

    private final Canvas canvas;

    private final Camera3D camera;

    public StarRenderer(Canvas canvas, Camera3D cam) {
        this.canvas = canvas;
        this.camera = cam;
    }

    public void render(GraphicsContext gc, List<Star> stars, Star hoveredStar) {

        if (stars == null || stars.isEmpty()) {
            return;
        }

        final double width = canvas.getWidth();
        final double height = canvas.getHeight();

        Color lastFill = null;

        for (Star s : stars) {

            double[] p = camera.project(s.getX(), s.getY(), s.getZ(), width, height);

            double sx = p[0];
            double sy = p[1];
            double depth = p[2];

            // Behind camera or too far
            if (depth <= 0) {
                continue;
            }

            // Screen culling
            if (sx < 0 || sy < 0 || sx > width || sy > height) {
                continue;
            }

            double radius = computeRadius(s, depth);

            Color color = mapColor(s);

            if (color != lastFill) {
                gc.setFill(color);
                lastFill = color;
            }

            double r2 = radius * 0.5;
            gc.fillOval(sx - r2, sy - r2, radius, radius);

            if (s == hoveredStar) {
                gc.setStroke(Color.CYAN);
                gc.strokeOval(sx - radius, sy - radius, radius * 2, radius * 2);
            }

        }

    }

    private double computeRadius(Star s, double depth) {
        double base = Math.max(0.8, 5.0 - s.getMagnitude() * 0.6);
        return base * camera.getFov() / depth;
    }

    public static Color mapColor(Star star) {

        if (star.getColour() == null) {
            return Color.WHITE;
        }

        return Color
                .color(
                        clamp(star.getColour().getRed()), clamp(star.getColour().getGreen()), clamp(
                                star.getColour().getBlue()
                        )
                );
    }

    private static double clamp(double value) {
        return Math.min(1.0, Math.max(0.0, value));
    }

}
