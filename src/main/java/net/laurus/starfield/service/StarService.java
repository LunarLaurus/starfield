package net.laurus.starfield.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.bus.EventBus;
import net.laurus.starfield.events.DataLoadedEvent;
import net.laurus.starfield.events.LoadDataEvent;
import net.laurus.starfield.model.Star;

@Service
@RequiredArgsConstructor
@Slf4j
public class StarService {

    private final ObjectMapper objectMapper;

    private final EventBus eventBus;

    private static final String FILE_NAME = "/bubble.json";

    private static final String FILE_NAME_SMALL = "/smol-bubble.json";

    private List<Star> starsCache;

    private Star sol;

    @PostConstruct
    public void init() {
        // Optional: preload if desired
        // loadStarData();
    }

    @Async("backgroundExecutor")
    @EventListener
    public void handleLoad(LoadDataEvent event) {

        try {
            String result = loadStarData();
            eventBus.publish(new DataLoadedEvent(result));
        }
        catch (Exception e) {
            log.error("Error loading star data", e);
            eventBus.publish(new DataLoadedEvent("Failed to load galaxy map: " + e.getMessage()));
        }

    }

    /**
     * Load default star file and return summary.
     */
    public String loadStarData() {
        List<Star> stars = loadStars(); // loads and caches the file
        Star solStar = getSol();

        return "Loaded " + stars.size() + " stars. Sol: "
                + (solStar.getName() != null ? solStar.getName() : "Unknown");
    }

    /**
     * Load and cache stars from default resource.
     */
    private synchronized List<Star> loadStars() {

        if (starsCache != null) {
            log.info("Returning cached stars.");
            return starsCache;
        }

        log.info("Loading stars from resource: {}", FILE_NAME);

        try (InputStream is = getClass().getResourceAsStream(FILE_NAME)) {

            if (is == null) {
                throw new RuntimeException("Resource not found: " + FILE_NAME);
            }

            starsCache = objectMapper.readValue(is, new TypeReference<List<Star>>() {
            });
            sol = starsCache.stream().filter(s -> s.getId() == 0).findFirst().orElse(new Star());

            log.info("Loaded {} stars from {}", starsCache.size(), FILE_NAME);
            return starsCache;

        }
        catch (Exception e) {
            log.error("Failed to load JSON: {}", FILE_NAME, e);
            throw new RuntimeException("Failed to load JSON: " + FILE_NAME, e);
        }

    }

    /**
     * Get cached Sol star.
     */
    public Star getSol() {

        if (sol == null) {
            loadStars();
        }

        return sol;
    }

    /**
     * Clear cached stars and Sol.
     */
    public synchronized void clearCache() {
        starsCache = null;
        sol = null;
    }

    /**
     * Get cached stars.
     */
    public List<Star> getCachedStars() {
        return loadStars();
    }

}
