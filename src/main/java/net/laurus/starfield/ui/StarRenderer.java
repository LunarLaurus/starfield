package net.laurus.starfield.ui;

import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.laurus.starfield.model.Star;

public class StarRenderer {

    private final Canvas canvas;

    private final Camera camera;

    public StarRenderer(Canvas canvas, Camera camera) {
        this.canvas = canvas;
        this.camera = camera;
    }

    public void render(List<Star> stars) {

        if (stars == null || stars.isEmpty()) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        StarScaler scaler = new StarScaler(stars, canvas.getWidth(), canvas.getHeight());

        for (Star s : stars) {
            double x = camera.worldToScreenX(scaler.toScreenX(s.getX()));
            double y = camera.worldToScreenY(scaler.toScreenY(s.getY()));
            double radius = scaler.scaleRadius(Math.max(1, 5 - s.getMagnitude() / 2.0));

            Color color = StarColorMapper.mapColor(s);
            gc.setFill(color);
            gc.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        }

    }

    public void clear() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
