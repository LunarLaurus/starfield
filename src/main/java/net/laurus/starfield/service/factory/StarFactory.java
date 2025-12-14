package net.laurus.starfield.service.factory;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.model.Star;

/**
 * Creates synthetic stars and applies validation rules.
 */
@Slf4j
public class StarFactory {

    public static Star SOL = createSol();

    /**
     * Creates the Sol star at the origin.
     */
    private static Star createSol() {

        Star sol = new Star();
        sol.setId(-1);
        sol.setName("Sol");
        sol.setDistanceInParsecs(0.0);
        sol.setXFromParsec(0.0);
        sol.setYFromParsec(0.0);
        sol.setZFromParsec(0.0);
        sol.setMagnitude(40D);

        Star.StarColour colour = new Star.StarColour();
        colour.setRed(1.0);
        colour.setGreen(0.7);
        colour.setBlue(0.2);
        sol.setColour(colour);

        return sol;
    }

    /**
     * Filters stars that appear to be data artifacts clustered very close to the
     * origin (Sol) within a small axis-aligned cube.
     * <p>
     * This method exists to catch malformed catalogue entries where missing or
     * partially parsed coordinates collapse stars into a tight (0–2 ly) region.
     * <p>
     * <strong>Note:</strong> This does NOT validate star data in general and should
     * not be used as a substitute for structural validation.
     *
     * @param stars list of stars to filter
     * @return list excluding stars clustered near the origin cube
     */
    public static List<Star> filterOriginClusterArtifacts(List<Star> stars) {

        List<Star> valid = new ArrayList<>();
        int excluded = 0;

        for (Star s : stars) {

            if (s == null || !s.hasValidPosition()) {
                continue;
            }

            double x = s.getX();
            double y = s.getY();
            double z = s.getZ();

            boolean clusteredNearOrigin = x > 0.0 && x < 2.0 && y > 0.0 && y < 2.0 && z > 0.0
                    && z < 2.0;

            if (clusteredNearOrigin) {
                excluded++;
                log
                        .warn(
                                "Excluded origin-cluster artifact id={} name={} x={} y={} z={}", s
                                        .getId(), s.getName(), x, y, z
                        );
            }
            else {
                valid.add(s);
            }

        }

        log.info("Excluded {} origin-cluster artifact stars", excluded);
        return valid;
    }

    /**
     * Filters stars suspiciously close to the origin (Sol) based on radial
     * distance.
     * <p>
     * Intended to remove catalogue or conversion artifacts that collapse stars
     * toward (0,0,0). Legitimate nearby stars should generally lie outside the
     * specified minimum radius.
     *
     * @param stars       list of stars to filter
     * @param minRadiusLy minimum allowed distance from the origin, in light-years
     * @return list excluding stars closer than {@code minRadiusLy} to the origin
     */
    public static List<Star> filterNearOriginArtifacts(List<Star> stars, double minRadiusLy) {

        List<Star> valid = new ArrayList<>();
        int excluded = 0;

        for (Star s : stars) {

            if (s == null || !s.hasValidPosition()) {
                continue;
            }

            double x = s.getX();
            double y = s.getY();
            double z = s.getZ();

            double r = Math.sqrt(x * x + y * y + z * z);

            if (r > 0.0 && r < minRadiusLy) {
                excluded++;
                log
                        .warn(
                                "Excluded near-origin artifact id={} name={} r={} ly", s.getId(), s
                                        .getName(), r
                        );
            }
            else {
                valid.add(s);
            }

        }

        log.info("Excluded {} near-origin artifact stars (r < {} ly)", excluded, minRadiusLy);

        return valid;
    }

    /**
     * Filters out stars that do not have a complete 3D position.
     * <p>
     * Any star with a {@code null} X, Y, or Z coordinate is considered structurally
     * invalid and excluded.
     *
     * @param stars list of stars to filter
     * @return list containing only stars with non-null X, Y, and Z coordinates
     */
    public static List<Star> filterStarsWithNullPosition(List<Star> stars) {

        List<Star> valid = new ArrayList<>();
        int excluded = 0;

        for (Star s : stars) {

            if (s == null) {
                excluded++;
                log.warn("Excluded null Star reference");
                continue;
            }

            if (!s.hasValidPosition()) {
                excluded++;
                log
                        .warn(
                                "Excluded star with null position id={} name={} x={} y={} z={}", s
                                        .getId(), s.getName(), s.getX(), s.getY(), s.getZ()
                        );
                continue;
            }

            valid.add(s);
        }

        log.info("Excluded {} stars with null XYZ coordinates", excluded);
        return valid;
    }

}
