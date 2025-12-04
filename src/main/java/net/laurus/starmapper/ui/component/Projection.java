package net.laurus.starmapper.ui.component;

public class Projection {

    private static final double CAMERA_DISTANCE = 800;

    /**
     * Project a 3D point (already rotated and relative to camera focus) into 2D
     * screen coordinates.
     *
     * @param pos    double[3] {x, y, z}
     * @param width  panel width
     * @param height panel height
     * @param cam    camera (zoom & pan)
     * @return screen coordinates {x, y}
     */
    public static int[] project(double[] pos, int width, int height, Camera cam) {

        double x = pos[0] - cam.getFocusX();
        double y = pos[1] - cam.getFocusY();
        double z = pos[2] - cam.getFocusZ();

        // Simple perspective projection
        double dz = z + CAMERA_DISTANCE;
        if (dz < 1)
            dz = 1;

        double scale = (CAMERA_DISTANCE / dz) * cam.getZoom();

        int screenX = (int) (x * scale + width / 2.0 + cam.getPanX());
        int screenY = (int) (y * scale + height / 2.0 + cam.getPanY());

        return new int[] {
                screenX, screenY
        };
    }

    /**
     * Convenience overload for separate x,y,z coordinates.
     */
    public static int[] project(double x, double y, double z, int width, int height, Camera cam) {
        return project(new double[] {
                x, y, z
        }, width, height, cam);
    }

}
