package net.laurus.starfield.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.laurus.starfield.model.StarNameData;

@Service
@RequiredArgsConstructor
@Slf4j
public class StarService {

    private final ObjectMapper objectMapper;

    private final EventBus eventBus;

    private static final String FILE_NAME_DATA = "/bsc5p_3d.json";

    private static final String FILE_NAME_NAMES = "/bsc5p_names.json";

    private List<Star> starsCache;

    private Map<Integer, List<String>> starNamesCache;

    private Star sol = createSol();

    @PostConstruct
    public void init() {
        loadStarNames();
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
     * Load and cache stars from default resource. Always injects the primary
     * (alphabetically first) name from the names cache when a match exists,
     * overriding any default name (e.g. HD-xxx).
     */
    private synchronized List<Star> loadStars() {

        if (starsCache != null) {
            log.info("Returning cached stars.");
            return starsCache;
        }

        log.info("Loading stars from resource: {}", FILE_NAME_DATA);

        try (InputStream is = getClass().getResourceAsStream(FILE_NAME_DATA)) {

            if (is == null) {
                throw new RuntimeException("Resource not found: " + FILE_NAME_DATA);
            }

            starsCache = objectMapper.readValue(is, new TypeReference<List<Star>>() {
            });
            log.info("Loaded {} stars from {}", starsCache.size(), FILE_NAME_DATA);

            // Ensure star names are loaded
            Map<Integer, List<String>> namesMap = loadStarNames();

            int injectedCount = 0;
            int missingCount = 0;

            for (Star star : starsCache) {

                if (star == null) {
                    continue;
                }

                List<String> names = namesMap.get(star.getId());

                if (names != null && !names.isEmpty()) {
                    star.setName(names.get(0)); // authoritative primary name
                    injectedCount++;
                }
                else {
                    missingCount++;
                }

            }

            log
                    .info(
                            "Injected names for {} stars; {} stars had no matching name entry", injectedCount, missingCount
                    );

            // Filter out invalid near-origin stars
            starsCache = filterInvalidNearOriginStars(starsCache);
            // Cache Sol explicitly
            starsCache.add(sol);

            return starsCache;

        }
        catch (Exception e) {
            log.error("Failed to load JSON: {}", FILE_NAME_DATA, e);
            throw new RuntimeException("Failed to load JSON: " + FILE_NAME_DATA, e);
        }

    }

    /**
     * Removes stars that fall within the invalid near-origin cube (0–2 on X/Y/Z).
     * These are considered corrupt or placeholder data.
     */
    private List<Star> filterInvalidNearOriginStars(List<Star> stars) {

        List<Star> valid = new ArrayList<>(stars.size());
        int invalidCount = 0;

        for (Star s : stars) {

            if (s == null) {
                continue;
            }

            double x = s.getX();
            double y = s.getY();
            double z = s.getZ();

            boolean invalid = x > 0 && x < 2 && y > 0 && y < 2 && z > 0 && z < 2;

            if (invalid) {
                invalidCount++;
                log
                        .warn(
                                "Invalid near-origin star excluded: id={}, name={}, x={}, y={}, z={}", s
                                        .getId(), s.getName(), x, y, z
                        );
            }
            else {
                valid.add(s);
            }

        }

        log.info("Excluded {} invalid near-origin stars", invalidCount);
        return valid;
    }

    /**
     * Loads JSON file and maps ID to sorted list of names.
     *
     * @param file JSON file to load
     * @return Map of star ID -> sorted list of names
     * @throws IOException if file cannot be read
     */

    public synchronized Map<Integer, List<String>> loadStarNames() {

        if (starNamesCache != null) {
            log.info("Returning cached star names.");
            return starNamesCache;
        }

        log.info("Loading star names from resource: {}", FILE_NAME_NAMES);

        try (InputStream is = getClass().getResourceAsStream(FILE_NAME_NAMES)) {

            if (is == null) {
                throw new RuntimeException("Resource not found: " + FILE_NAME_NAMES);
            }

            List<StarNameData> jsonList = objectMapper
                    .readValue(is, new TypeReference<List<StarNameData>>() {
                    });

            Map<Integer, List<String>> result = new HashMap<>();

            for (StarNameData entry : jsonList) {

                if (entry == null || entry.getId() <= 0 || entry.getNames() == null
                        || entry.getNames().isEmpty()) {
                    continue;
                }

                List<String> sortedNames = new ArrayList<>(entry.getNames());
                Collections.sort(sortedNames);
                result.put(entry.getId(), sortedNames);
            }

            starNamesCache = result;
            log.info("Loaded {} star name entries.", starNamesCache.size());
            return starNamesCache;

        }
        catch (IOException e) {
            log.error("Failed to load star names from JSON: {}", FILE_NAME_NAMES, e);
            throw new RuntimeException("Failed to load star names JSON: " + FILE_NAME_NAMES, e);
        }

    }

    public synchronized void clearNamesCache() {
        starNamesCache = null;
    }

    public Map<Integer, List<String>> getStarNamesCache() {
        return loadStarNames();
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

    // -------------------- Star Name Access --------------------

    /**
     * Get all known names for a star by Star reference.
     *
     * @param star Star instance (may be null)
     * @return Sorted list of names, or empty list if not found
     */
    public List<String> getStarNames(Star star) {

        if (star == null) {
            log.debug("getStarNames called with null Star");
            return List.of();
        }

        return getStarNamesById(star.getId());
    }

    /**
     * Get all known names for a star by its ID.
     *
     * @param starId Star ID
     * @return Sorted list of names, or empty list if not found
     */
    public List<String> getStarNamesById(int starId) {

        if (starId <= 0) {
            log.debug("getStarNamesById called with invalid id: {}", starId);
            return List.of();
        }

        Map<Integer, List<String>> cache = loadStarNames();
        List<String> names = cache.get(starId);

        if (names == null || names.isEmpty()) {
            log.debug("No names found for star id {}", starId);
            return List.of();
        }

        return names;
    }

    /**
     * Get the preferred (primary) name for a star by reference. Uses the first
     * alphabetical name.
     *
     * @param star Star instance
     * @return Preferred name or null if unavailable
     */
    public String getPrimaryStarName(Star star) {

        List<String> names = getStarNames(star);
        return names.isEmpty() ? null : names.get(0);
    }

    /**
     * Get the preferred (primary) name for a star by ID.
     *
     * @param starId Star ID
     * @return Preferred name or null if unavailable
     */
    public String getPrimaryStarNameById(int starId) {

        List<String> names = getStarNamesById(starId);
        return names.isEmpty() ? null : names.get(0);
    }

    public static Star createSol() {

        Star sol = new Star();

        // Identity
        sol.setId(0);
        sol.setName("Sol");

        // Distance
        sol.setDistanceInParsecs(0.0);

        // Coordinates (origin)
        sol.setXFromParsec(0.0);
        sol.setYFromParsec(0.0);
        sol.setZFromParsec(0.0);

        // Absolute visual magnitude of the Sun
        sol.setMagnitude(40);

        // Approximate solar colour (D65-ish white-yellow)
        Star.StarColour colour = new Star.StarColour();
        colour.setRed(1.0);
        colour.setGreen(0.7);
        colour.setBlue(0.2);
        sol.setColour(colour);

        return sol;
    }

}
