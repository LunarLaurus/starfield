package net.laurus.starfield.app.manager;

import static net.laurus.starfield.model.ColourPalette.HUMAN_EXPLORATION_LIFE_TO_EMPTY;

import javafx.scene.paint.Color;
import net.laurus.starfield.model.ColourPalette;

/**
 * Manages colour palettes for mapping normalized values (0.0–1.0) to colours.
 * Supports multiple predefined palettes and smooth interpolation between
 * colours.
 */
public class ColourPalleteManager {

    private ColourPalette currentPalette = HUMAN_EXPLORATION_LIFE_TO_EMPTY;

    private double[] stops;

    /**
     * Constructs a ColourPalleteManager with the default palette.
     */
    public ColourPalleteManager() {
        setPalette(currentPalette);
    }

    /**
     * Sets the active palette and recalculates uniform interpolation stops.
     *
     * @param palette the palette to activate
     */
    public void setPalette(ColourPalette palette) {
        this.currentPalette = palette;
        Color[] colours = palette.getColours();
        int n = colours.length;
        stops = new double[n];

        for (int i = 0; i < n; i++) {
            stops[i] = (double) i / (n - 1); // evenly spaced 0.0 → 1.0
        }

    }

    /**
     * Returns the currently active palette.
     *
     * @return the active palette
     */
    public ColourPalette getCurrentPalette() {
        return currentPalette;
    }

    /**
     * Gets an interpolated colour for a normalised value t in [0, 1].
     *
     * @param t normalised value (0.0 = start of palette, 1.0 = end of palette)
     * @return interpolated colour
     */
    public Color getColourForNormalisedValue(double t) {
        Color[] colours = currentPalette.getColours();

        if (t <= 0) {
            return colours[0];
        }

        if (t >= 1) {
            return colours[colours.length - 1];
        }

        int i = 0;

        while (i < stops.length - 1 && t > stops[i + 1]) {
            i++;
        }

        double localT = (t - stops[i]) / (stops[i + 1] - stops[i]);
        return interpolateColour(colours[i], colours[i + 1], localT);
    }

    /**
     * Linearly interpolates between two colours.
     *
     * @param c1 first colour
     * @param c2 second colour
     * @param t  interpolation factor [0, 1]
     * @return interpolated colour
     */
    private Color interpolateColour(Color c1, Color c2, double t) {
        double r = c1.getRed() + t * (c2.getRed() - c1.getRed());
        double g = c1.getGreen() + t * (c2.getGreen() - c1.getGreen());
        double b = c1.getBlue() + t * (c2.getBlue() - c1.getBlue());
        double a = c1.getOpacity() + t * (c2.getOpacity() - c1.getOpacity());
        return new Color(r, g, b, a);
    }

}
