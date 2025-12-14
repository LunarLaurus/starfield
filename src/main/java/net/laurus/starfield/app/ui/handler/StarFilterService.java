package net.laurus.starfield.app.ui.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.Getter;
import net.laurus.starfield.model.Star;

/**
 * Handles distance-based star filtering.
 */
public class StarFilterService {

    private List<Star> allStars;

    @Getter
    private double currentMaxDistance = 2000;

    private final Consumer<List<Star>> onFiltered;

    public StarFilterService(Consumer<List<Star>> onFiltered) {
        this.onFiltered = onFiltered;
    }

    public void setAllStars(List<Star> stars) {
        this.allStars = new ArrayList<>(stars);
        filter(currentMaxDistance, null);
    }

    public void filter(double maxDistance, Star reference) {

        if (allStars == null) {
            return;
        }

        this.currentMaxDistance = maxDistance;

        List<Star> filtered = new ArrayList<>();

        for (Star s : allStars) {

            if (s.distanceTo(reference) <= maxDistance) {
                filtered.add(s);
            }

        }

        onFiltered.accept(filtered);
    }

}
