package net.laurus.starmapper.util;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.laurus.starmapper.model.Star;

@Slf4j
public class StarLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    // Cache of loaded star lists by resource name
    private static final Map<String, List<Star>> cache = new HashMap<>();

    // Cached Sol
    private static Star SOL;

    /**
     * Load default star file: bubble.json
     */
    public static List<Star> loadStars() {
        return loadStars("bubble.json");
    }

    /**
     * Load stars from a JSON file inside src/main/resources. Uses internal cache to
     * avoid reloading the same file multiple times.
     */
    public static List<Star> loadStars(String resourceName) {

        // Return cached if available
        if (cache.containsKey(resourceName)) {
            return cache.get(resourceName);
        }

        // Ensure resource path starts with /
        if (!resourceName.startsWith("/")) {
            resourceName = "/" + resourceName;
        }

        log.info("Loading stars from resource: {}", resourceName);

        try (InputStream is = StarLoader.class.getResourceAsStream(resourceName)) {

            if (is == null) {
                log.error("Resource '{}' not found in classpath!", resourceName);
                throw new RuntimeException(
                        "Resource not found: " + resourceName
                                + "\nEnsure it exists in src/main/resources/"
                );
            }

            List<Star> stars = mapper.readValue(is, new TypeReference<List<Star>>() {
            });
            log.info("Loaded {} stars from {}", stars.size(), resourceName);

            // Cache loaded stars
            cache.put(resourceName, stars);

            // Cache Sol
            SOL = stars.stream().filter(s -> s.getId() == 0).findFirst().orElse(new Star());

            return stars;

        }
        catch (Exception e) {
            log.error("Failed to load JSON: {}", resourceName, e);
            throw new RuntimeException("Failed to load JSON: " + resourceName, e);
        }

    }

    /**
     * Get cached Sol star. If not already loaded, will attempt to load default
     * stars first.
     */
    public static Star getSol() {
        if (SOL != null)
            return SOL;

        List<Star> stars = loadStars();
        SOL = stars.stream().filter(s -> s.getId() == 0).findFirst().orElse(new Star());
        return SOL;
    }

    /**
     * Clear all caches. Useful for testing or reloading.
     */
    public static void clearCache() {
        cache.clear();
        SOL = null;
    }

    /**
     * Get cached stars for a resource if already loaded.
     */
    public static List<Star> getCachedStars(String resourceName) {
        return cache.getOrDefault(resourceName, Collections.emptyList());
    }

}
