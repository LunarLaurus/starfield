package net.laurus.starfield.app.ui.component;

import java.util.Locale;

import lombok.Getter;

@Getter
public class Camera3D {

    private double x = 0;

    private double y = 0;

    private double z = -2000; // Start behind origin

    private double pitch = 0; // Up/down

    private double yaw = 0; // Left/right

    private double fov = 500; // Field of view

    private final double minFov = 100;

    private final double maxFov = 2000;

    /** Move camera relative to its local axes (forward, right, up) */
    public void move(double forward, double right, double up) {
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);

        // Forward/backward in camera direction (including pitch)
        x += Math.sin(radYaw) * forward;
        y += Math.sin(radPitch) * forward;
        z += Math.cos(radYaw) * forward;

        // Strafing left/right (ignores pitch)
        x += Math.cos(radYaw) * right;
        z -= Math.sin(radYaw) * right;

        // Up/down directly
        y += up;
    }

    /** Rotate camera freely */
    public void rotate(double dYaw, double dPitch) {
        yaw += dYaw;
        pitch = Math.max(-89, Math.min(89, pitch + dPitch));
    }

    /** Adjust FOV for zooming */
    public void zoom(double factor) {
        fov = Math.max(minFov, Math.min(maxFov, fov * factor));
    }

    /**
     * Project 3D world coordinates to 2D canvas coordinates Returns {screenX,
     * screenY, depth}
     */
    public double[]
            project(double px, double py, double pz, double screenWidth, double screenHeight) {
        // Translate to camera space
        double dx = px - x;
        double dy = py - y;
        double dz = pz - z;

        // Precompute rotation radians
        double radYaw = Math.toRadians(-yaw);
        double radPitch = Math.toRadians(-pitch);

        // Rotate around Y (yaw)
        double x1 = dx * Math.cos(radYaw) - dz * Math.sin(radYaw);
        double z1 = dx * Math.sin(radYaw) + dz * Math.cos(radYaw);

        // Rotate around X (pitch)
        double y1 = dy * Math.cos(radPitch) - z1 * Math.sin(radPitch);
        double z2 = dy * Math.sin(radPitch) + z1 * Math.cos(radPitch);

        // Avoid division by zero / behind camera
        if (z2 < 0.1) {
            z2 = 0.1;
        }

        // Perspective projection
        double sx = (x1 / z2) * fov + screenWidth / 2;
        double sy = (y1 / z2) * fov + screenHeight / 2;

        return new double[] {
                sx, sy, z2
        };
    }

    public String toUiString(boolean includePosition) {

        // Ensure dot decimal regardless of locale
        Locale locale = Locale.US;

        if (includePosition) {
            return String
                    .format(
                            locale, "Camera  |  Pos [X: %7.1f  Y: %7.1f  Z: %7.1f]  |  Yaw: %6.1f°  Pitch: %6.1f°  |  FOV: %5.0f", x, y, z, yaw, pitch, fov
                    );
        }

        return String
                .format(
                        locale, "Camera  |  Yaw: %6.1f°  Pitch: %6.1f°  |  FOV: %5.0f", yaw, pitch, fov
                );
    }

}
