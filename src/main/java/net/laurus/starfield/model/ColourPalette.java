package net.laurus.starfield.model;

import javafx.scene.paint.Color;

/**
 * Predefined colour palettes for visualising distances, values, or other
 * normalised metrics.
 * <p>
 * Each palette defines a smooth gradient from the first to the last colour.
 * </p>
 */
public enum ColourPalette {

    /**
     * Life → Cold heatmap: symbolises distance humans have travelled from Earth.
     * <p>
     * - First colour: vibrant, full of life (green, yellow) - Mid colours:
     * transition through warmth, energy, fading - Last colour: cold, empty space
     * (dark blue/grey)
     * </p>
     */
    HUMAN_EXPLORATION_LIFE_TO_EMPTY(
            new Color[] {
                    Color.web("#00FF00"), // bright green: life, vitality
                    Color.web("#7FFF00"), // chartreuse: growth, energy
                    Color.web("#FFFF00"), // yellow: warmth, activity
                    Color.web("#FFD700"), // gold: human achievement
                    Color.web("#FFA500"), // orange: fading reach
                    Color.web("#FF4500"), // orange-red: distant frontier
                    Color.web("#708090"), // slate grey: cold, remote
                    Color.web("#0B0B3B")// dark blue: empty space, void
            }
    ),

    /**
     * Smooth transition from light green → dark green → light blue → dark blue →
     * dark grey.
     * <p>
     * Good for general-purpose distance or height visualisation.
     * </p>
     */
    GREEN_TO_BLUE(
            new Color[] {
                    Color.LIGHTGREEN, Color.GREEN, Color.DARKGREEN, Color.LIGHTBLUE, Color.BLUE,
                    Color.DARKBLUE, Color.DARKGRAY
            }
    ),

    /**
     * Warm fire colours from yellow → orange → red → dark red → maroon.
     * <p>
     * Suitable for heat maps or intensity visualization.
     * </p>
     */
    FIRE(
            new Color[] {
                    Color.YELLOW, Color.GOLD, Color.ORANGE, Color.RED, Color.DARKRED, Color.MAROON
            }
    ),

    /**
     * Sunset gradient: light pink → hot pink → orange-red → red → dark red.
     * <p>
     * Ideal for aesthetic visuals with warm tones.
     * </p>
     */
    SUNSET(
            new Color[] {
                    Color.LIGHTPINK, Color.HOTPINK, Color.ORANGERED, Color.RED, Color.DARKRED
            }
    ),

    /**
     * Ocean tones: light cyan → cyan → turquoise → dark cyan → navy.
     * <p>
     * Works well for water or depth visualisation.
     * </p>
     */
    OCEAN(
            new Color[] {
                    Color.LIGHTCYAN, Color.CYAN, Color.TURQUOISE, Color.DARKCYAN, Color.NAVY
            }
    ),

    /**
     * Earth tones: beige → tan → sienna → brown → firebrick → dark brown.
     * <p>
     * Useful for terrain or natural visualisations.
     * </p>
     */
    EARTH(
            new Color[] {
                    Color.BEIGE, Color.TAN, Color.SIENNA, Color.BROWN, Color.FIREBRICK,
                    Color.DARKRED
            }
    ),

    /**
     * Rainbow gradient: red → orange → yellow → green → cyan → blue → violet.
     * <p>
     * Classic rainbow spectrum for continuous value visualisation.
     * </p>
     */
    RAINBOW(
            new Color[] {
                    Color.RED, Color.ORANGE, Color.YELLOW, Color.LIME, Color.CYAN, Color.BLUE,
                    Color.VIOLET
            }
    ),

    /**
     * Ice / cold tones: pale blue → light blue → cyan → teal → dark blue.
     * <p>
     * Suitable for cold, icy, or water-themed visualisations.
     * </p>
     */
    ICE(
            new Color[] {
                    Color.ALICEBLUE, Color.LIGHTBLUE, Color.CYAN, Color.TEAL, Color.DARKBLUE
            }
    ),

    /**
     * Desert gradient: pale yellow → gold → tan → sienna → brown → dark brown.
     * <p>
     * Works well for arid or sandy environments.
     * </p>
     */
    DESERT(
            new Color[] {
                    Color.LIGHTYELLOW, Color.GOLD, Color.TAN, Color.SIENNA, Color.BROWN,
                    Color.DARKKHAKI
            }
    ),

    /** Green → yellow → orange → brown */
    GREEN_TO_BROWN(
            new Color[] {
                    Color.web("#006400"), // dark green
                    Color.web("#228B22"), Color.web("#32CD32"), Color.web("#7FFF00"), // chartreuse
                    Color.web("#ADFF2F"), Color.web("#FFD700"), // gold
                    Color.web("#FFA500"), // orange
                    Color.web("#8B4513")// brown
            }
    ),

    /** Blue → cyan → light blue → navy */
    BLUE_TO_NAVY(
            new Color[] {
                    Color.web("#0000FF"), // blue
                    Color.web("#0033FF"), Color.web("#0066FF"), Color.web("#3399FF"),
                    Color.web("#66CCFF"), Color.web("#99CCFF"), Color.web("#336699"),
                    Color.web("#000080")// navy
            }
    ),

    /** Red → orange → yellow → light yellow */
    RED_TO_YELLOW(
            new Color[] {
                    Color.web("#FF0000"), // red
                    Color.web("#FF3300"), Color.web("#FF6600"), Color.web("#FF9900"),
                    Color.web("#FFCC00"), Color.web("#FFFF00"), // yellow
                    Color.web("#FFFF66"), Color.web("#FFFFCC")// light yellow
            }
    ),

    /** Blue → purple → magenta → pink */
    BLUE_TO_PINK(
            new Color[] {
                    Color.web("#0000FF"), // blue
                    Color.web("#3333FF"), Color.web("#6600FF"), Color.web("#9900FF"),
                    Color.web("#CC33FF"), Color.web("#FF33CC"), Color.web("#FF66CC"),
                    Color.web("#FF99CC")// pink
            }
    ),

    /** Yellow → green → teal → cyan */
    YELLOW_TO_CYAN(
            new Color[] {
                    Color.web("#FFFF00"), // yellow
                    Color.web("#CCFF00"), Color.web("#99FF33"), Color.web("#66FF66"),
                    Color.web("#33FF99"), Color.web("#00FFCC"), Color.web("#00FFFF"), // cyan
                    Color.web("#00CCCC")
            }
    ),

    /** Magenta → red → orange → gold */
    MAGENTA_TO_GOLD(
            new Color[] {
                    Color.web("#FF00FF"), // magenta
                    Color.web("#FF33CC"), Color.web("#FF3399"), Color.web("#FF6666"),
                    Color.web("#FF9933"), Color.web("#FFCC00"), Color.web("#FFD700"), // gold
                    Color.web("#FFC700")
            }
    );

    /** Array of colours in this palette */
    private final Color[] colours;

    ColourPalette(Color[] colours) {
        this.colours = colours;
    }

    /**
     * Returns the colours in this palette.
     *
     * @return array of colours, in order from start to end of gradient
     */
    public Color[] getColours() {
        return colours;
    }

}
