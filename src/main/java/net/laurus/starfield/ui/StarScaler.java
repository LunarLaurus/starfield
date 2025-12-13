package net.laurus.starfield.ui;

import java.util.List;

import net.laurus.starfield.model.Star;

public class StarScaler {

    private double minX, maxX, minY, maxY;

    private double scaleX, scaleY;

    public StarScaler(List<Star> stars, double canvasWidth, double canvasHeight) {

        minX = stars.stream().mapToDouble(Star::getX).min().orElse(0);
        maxX = stars.stream().mapToDouble(Star::getX).max().orElse(1);
        minY = stars.stream().mapToDouble(Star::getY).min().orElse(0);
        maxY = stars.stream().mapToDouble(Star::getY).max().orElse(1);

        // Use constructor arguments directly
        scaleX = canvasWidth / (maxX - minX);
        scaleY = canvasHeight / (maxY - minY);
    }

    public double toScreenX(double worldX) {
        return (worldX - minX) * scaleX;
    }

    public double toScreenY(double worldY) {
        return (worldY - minY) * scaleY;
    }

    public double scaleRadius(double radius) {
        // Average scale for radius
        return radius * (scaleX + scaleY) / 2.0;
    }

}
