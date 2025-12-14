package net.laurus.starfield.app.manager;

import java.util.EnumMap;
import java.util.Map;

import net.laurus.starfield.model.GridPlane;

/**
 * Tracks which GridPlanes are enabled. Each plane can be individually enabled
 * or disabled.
 */
public class GridPlaneManager {

    private final Map<GridPlane, Boolean> planeStatus;

    /** Initializes all planes as disabled by default */
    public GridPlaneManager() {
        planeStatus = new EnumMap<>(GridPlane.class);

        for (GridPlane plane : GridPlane.values()) {
            planeStatus.put(plane, false);
        }

    }

    /** Enable a specific plane */
    public void enable(GridPlane plane) {
        planeStatus.put(plane, true);
    }

    /** Disable a specific plane */
    public void disable(GridPlane plane) {
        planeStatus.put(plane, false);
    }

    /** Toggle a specific plane */
    public void toggle(GridPlane plane) {
        planeStatus.put(plane, !planeStatus.get(plane));
    }

    /** Check if a specific plane is enabled */
    public boolean isEnabled(GridPlane plane) {
        return planeStatus.getOrDefault(plane, false);
    }

    /** Enable all planes */
    public void enableAll() {

        for (GridPlane plane : GridPlane.values()) {
            planeStatus.put(plane, true);
        }

    }

    /** Disable all planes */
    public void disableAll() {

        for (GridPlane plane : GridPlane.values()) {
            planeStatus.put(plane, false);
        }

    }

    /** Get a copy of the current status map */
    public Map<GridPlane, Boolean> getStatusMap() {
        return Map.copyOf(planeStatus);
    }

}
