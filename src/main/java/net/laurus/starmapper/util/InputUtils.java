package net.laurus.starmapper.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for math helpers
 */
@UtilityClass
public class InputUtils {

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double expSmooth(double value, double factor, double dt) {
        return value * Math.exp(-factor * dt);
    }

}
