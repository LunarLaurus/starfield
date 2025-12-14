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
        sol.setId(0);
        sol.setName("Sol");
        sol.setDistanceInParsecs(0.0);
        sol.setXFromParsec(0.0);
        sol.setYFromParsec(0.0);
        sol.setZFromParsec(0.0);
        sol.setMagnitude(40);

        Star.StarColour colour = new Star.StarColour();
        colour.setRed(1.0);
        colour.setGreen(0.7);
        colour.setBlue(0.2);
        sol.setColour(colour);

        return sol;
    }

    /**
     * Filters invalid near-origin stars (0–2 cube).
     */
    public static List<Star> filterInvalidNearOrigin(List<Star> stars) {

        List<Star> valid = new ArrayList<>();
        int excluded = 0;

        for (Star s : stars) {

            if (s == null) {
                continue;
            }

            boolean invalid = s.getX() > 0 && s.getX() < 2 && s.getY() > 0 && s.getY() < 2
                    && s.getZ() > 0 && s.getZ() < 2;

            if (invalid) {
                excluded++;
                log.warn("Excluded invalid star id={} name={}", s.getId(), s.getName());
            }
            else {
                valid.add(s);
            }

        }

        log.info("Excluded {} invalid near-origin stars", excluded);
        return valid;
    }

}
