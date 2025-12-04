package net.laurus.starmapper.ui.component;

import lombok.Getter;
import lombok.Setter;

/**
 * Camera with optional inertial pan/zoom support. StarMapPanel drives updates
 * by calling update(deltaSeconds).
 */
@Getter
@Setter
public class Camera {

    private double focusX = 0;

    private double focusY = 0;

    private double focusZ = 0;

    private double zoom = 1.0;

    private double panX = 0;

    private double panY = 0;

    // inertial velocity (pixels per second)
    private double velPanX = 0;

    private double velPanY = 0;

    // friction per second (0..∞), e.g., 3.0 = fairly quick damping
    private double friction = 3.5;

    // enable/disable inertia
    private boolean useInertia = false;

    /** Update camera state by delta seconds. Applies inertia if enabled. */
    public void update(double dtSeconds) {
        if (!useInertia)
            return;

        if (Math.abs(velPanX) > 1e-3 || Math.abs(velPanY) > 1e-3) {
            panX += velPanX * dtSeconds;
            panY += velPanY * dtSeconds;

            // exponential damping
            double damp = Math.exp(-friction * dtSeconds);
            velPanX *= damp;
            velPanY *= damp;

            // clamp tiny values
            if (Math.abs(velPanX) < 1e-3)
                velPanX = 0;
            if (Math.abs(velPanY) < 1e-3)
                velPanY = 0;
        }

    }

    /** Returns true if camera is still moving via inertia. */
    public boolean isMoving() {
        return useInertia && (velPanX != 0 || velPanY != 0);
    }

    /** Apply inertia velocity (pixels/sec). Only applied if useInertia is true. */
    public void addInertia(double vx, double vy) {
        if (!useInertia)
            return;
        this.velPanX = vx;
        this.velPanY = vy;
    }

}
