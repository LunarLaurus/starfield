package net.laurus.starfield.service;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.app.bus.EventBus;
import net.laurus.starfield.app.events.DataLoadedEvent;
import net.laurus.starfield.app.events.LoadDataEvent;
import net.laurus.starfield.model.Star;
import net.laurus.starfield.service.factory.StarFactory;
import net.laurus.starfield.service.loader.StarDataLoader;
import net.laurus.starfield.service.repo.StarNameRepository;

/**
 * High-level star domain service.
 * <p>
 * Coordinates loading, caching, name resolution and event publishing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StarService {

    private final StarDataLoader loader;

    private final StarNameRepository names;

    private final EventBus eventBus;

    private List<Star> cache;

    private Star sol;

    /**
     * Async handler for galaxy loading.
     */
    @Async("backgroundExecutor")
    @EventListener
    public void handleLoad(LoadDataEvent event) {

        try {
            List<Star> stars = getStars();
            eventBus.publish(new DataLoadedEvent("Loaded " + stars.size() + " stars. Sol: Sol"));
        }
        catch (Exception e) {
            log.error("Error loading star data", e);
            eventBus.publish(new DataLoadedEvent("Failed to load galaxy map: " + e.getMessage()));
        }

    }

    /**
     * Returns all cached stars.
     */
    public synchronized List<Star> getStars() {

        if (cache != null) {
            return cache;
        }

        List<Star> stars = loader.loadStars();

        for (Star star : stars) {
            List<String> starNames = names.getNames(star.getId());

            if (!starNames.isEmpty()) {
                star.setName(starNames.get(0));
            }

        }

        stars = StarFactory.filterInvalidNearOrigin(stars);

        sol = StarFactory.SOL;
        stars.add(sol);

        cache = stars;
        return cache;
    }

    /**
     * Returns Sol.
     */
    public Star getSol() {

        if (sol == null) {
            getStars();
        }

        return sol;
    }

    /**
     * Clears all cached data.
     */
    public synchronized void clearCache() {
        cache = null;
        sol = null;
        names.clear();
    }

    /**
     * Returns all known names for a star.
     */
    public List<String> getStarNames(Star star) {
        return star == null ? List.of() : names.getNames(star.getId());
    }

    /**
     * Returns the preferred name for a star.
     */
    public String getPrimaryStarName(Star star) {
        List<String> n = getStarNames(star);
        return n.isEmpty() ? null : n.get(0);
    }

}
