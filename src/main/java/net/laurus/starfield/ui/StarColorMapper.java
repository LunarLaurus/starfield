package net.laurus.starfield.ui;

import javafx.scene.paint.Color;
import net.laurus.starfield.model.Star;

public class StarColorMapper {

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
