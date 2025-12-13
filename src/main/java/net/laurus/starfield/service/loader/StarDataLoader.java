package net.laurus.starfield.service.loader;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.model.Star;
import net.laurus.starfield.model.StarNameData;

/**
 * Loads raw star and star-name data from JSON resources.
 * <p>
 * This class performs I/O and JSON parsing only.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StarDataLoader {

    private static final String STAR_DATA_FILE = "/bsc5p_3d.json";

    private static final String STAR_NAMES_FILE = "/bsc5p_names.json";

    private final ObjectMapper objectMapper;

    /**
     * Loads all stars from the default JSON resource.
     *
     * @return list of stars
     */
    public List<Star> loadStars() {

        try (InputStream is = getClass().getResourceAsStream(STAR_DATA_FILE)) {

            if (is == null) {
                throw new IllegalStateException("Resource not found: " + STAR_DATA_FILE);
            }

            return objectMapper.readValue(is, new TypeReference<>() {
            });
        }
        catch (Exception e) {
            log.error("Failed to load star data", e);
            throw new RuntimeException("Failed to load star data", e);
        }

    }

    /**
     * Loads star name mappings from JSON.
     *
     * @return list of raw name entries
     */
    public List<StarNameData> loadStarNames() {

        try (InputStream is = getClass().getResourceAsStream(STAR_NAMES_FILE)) {

            if (is == null) {
                throw new IllegalStateException("Resource not found: " + STAR_NAMES_FILE);
            }

            return objectMapper.readValue(is, new TypeReference<>() {
            });
        }
        catch (Exception e) {
            log.error("Failed to load star names", e);
            throw new RuntimeException("Failed to load star names", e);
        }

    }

}
