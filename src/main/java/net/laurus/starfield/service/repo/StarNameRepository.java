package net.laurus.starfield.service.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.model.StarNameData;
import net.laurus.starfield.service.loader.StarDataLoader;

/**
 * Caches and provides access to star name mappings.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StarNameRepository {

    private final StarDataLoader loader;

    private Map<Integer, List<String>> cache;

    @PostConstruct
    void init() {
        load();
    }

    /**
     * Loads and caches star names.
     */
    public synchronized Map<Integer, List<String>> load() {

        if (cache != null) {
            return cache;
        }

        Map<Integer, List<String>> result = new HashMap<>();

        for (StarNameData entry : loader.loadStarNames()) {

            if (entry == null || entry.getId() <= 0 || entry.getNames() == null) {
                continue;
            }

            List<String> names = new ArrayList<>(entry.getNames());
            names.sort(String::compareTo);
            result.put(entry.getId(), names);
        }

        cache = result;
        log.info("Loaded {} star name entries", cache.size());
        return cache;
    }

    /**
     * Returns names for a star ID.
     */
    public List<String> getNames(int starId) {
        List<String> names = load().get(starId);
        return names == null ? List.of() : names;
    }

    /**
     * Clears cached names.
     */
    public synchronized void clear() {
        cache = null;
    }

}
