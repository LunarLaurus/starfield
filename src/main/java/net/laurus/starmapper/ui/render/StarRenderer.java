package net.laurus.starmapper.ui.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.laurus.starmapper.model.Star;
import net.laurus.starmapper.model.StarColour;
import net.laurus.starmapper.ui.component.Camera;
import net.laurus.starmapper.ui.component.KDTree;
import net.laurus.starmapper.ui.component.Projection;

/**
 * Responsible for star rendering, distance filtering, and projection
 */
public class StarRenderer {

    public static final int SELECTION_RADIUS = 8;

    private static final double COORD_SCALE = 10.0;

    private final List<Star> stars;

    private final Camera camera;

    private final KDTree kdtree;

    @Getter
    @Setter
    private double maxDistance = Double.MAX_VALUE;

    @Getter
    private double maxDistanceSq = Double.MAX_VALUE;

    public StarRenderer(List<Star> stars, Camera camera, KDTree kdtree) {
        this.stars = stars;
        this.camera = camera;
        this.kdtree = kdtree;
    }

    public void setDistanceFilter(double parsecs) {
        this.maxDistance = parsecs;
        this.maxDistanceSq = parsecs * parsecs;
    }

    public static double[][] buildRotationMatrix(double rotX, double rotY) {
        double cx = Math.cos(rotX), sx = Math.sin(rotX);
        double cy = Math.cos(rotY), sy = Math.sin(rotY);
        return new double[][] {
                {
                        cy, 0, sy
                }, {
                        sx * sy, cx, -sx * cy
                }, {
                        -cx * sy, sx, cx * cy
                }
        };
    }

    public void
            render(Graphics2D g, int width, int height, Star selectedStar, double[][] rotMatrix) {
        // Use camera focus instead of selected star for proper zoom
        double centerX = camera.getFocusX();
        double centerY = camera.getFocusY();
        double centerZ = camera.getFocusZ();

        for (Star s : stars) {
            double dx = (s.getX() - centerX) * COORD_SCALE;
            double dy = (s.getY() - centerY) * COORD_SCALE;
            double dz = (s.getZ() - centerZ) * COORD_SCALE;

            double dsq = dx * dx + dy * dy + dz * dz;
            if (dsq > maxDistanceSq)
                continue;

            double[] pos = applyMatrix(new double[] {
                    dx, dy, dz
            }, rotMatrix);
            int[] p = Projection.project(pos, width, height, camera);

            drawStar(g, s, p[0], p[1], selectedStar == s);
        }

        // Draw filter circle
        if (selectedStar != null && maxDistance != Double.MAX_VALUE) {
            g.setColor(new Color(255, 255, 255, 40));
            int radius = (int) (maxDistance * COORD_SCALE * camera.getZoom());
            g
                    .drawOval(
                            width / 2 + (int) camera.getPanX() - radius, height / 2
                                    + (int) camera.getPanY() - radius, radius * 2, radius * 2
                    );
        }

    }

    private void drawStar(Graphics2D g, Star s, int x, int y, boolean highlight) {
        StarColour col = s.getColour();
        if (col != null)
            g.setColor(new Color(clamp(col.getRed()), clamp(col.getGreen()), clamp(col.getBlue())));
        else
            g.setColor(Color.WHITE);

        int size = 3;
        g.fillOval(x, y, size, size);

        if (highlight) {
            g.setColor(Color.YELLOW);
            g.drawOval(x - 4, y - 4, size + 8, size + 8);
            g.setColor(Color.WHITE);
            g.drawString(s.getName(), x + 6, y - 6);
        }

    }

    private float clamp(double d) {
        return (float) Math.max(0.0, Math.min(1.0, d));
    }

    public int[]
            projectStarToScreen(Star s, Star center, double[][] rotMatrix, int width, int height) {
        double dx = (s.getX() - center.getX()) * COORD_SCALE;
        double dy = (s.getY() - center.getY()) * COORD_SCALE;
        double dz = (s.getZ() - center.getZ()) * COORD_SCALE;
        double[] pos = applyMatrix(new double[] {
                dx, dy, dz
        }, rotMatrix);
        return Projection.project(pos, width, height, camera);
    }

    private double[] applyMatrix(double[] v, double[][] m) {
        return new double[] {
                v[0] * m[0][0] + v[1] * m[1][0] + v[2] * m[2][0],
                v[0] * m[0][1] + v[1] * m[1][1] + v[2] * m[2][1],
                v[0] * m[0][2] + v[1] * m[1][2] + v[2] * m[2][2]
        };
    }

    public void zoomToFitSelected(
            Star selectedStar,
            double radiusParsecs,
            int panelWidth,
            int panelHeight
    ) {
        Star center = (selectedStar != null) ? selectedStar : stars.get(0);
        if (center == null)
            return;

        double r = radiusParsecs > 0 ? radiusParsecs : maxDistance;
        List<Star> cluster = kdtree.range(center.getX(), center.getY(), center.getZ(), r);
        if (cluster.isEmpty())
            return;

        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;

        for (Star s : cluster) {
            minX = Math.min(minX, s.getX());
            maxX = Math.max(maxX, s.getX());
            minY = Math.min(minY, s.getY());
            maxY = Math.max(maxY, s.getY());
        }

        double worldWidth = (maxX - minX) * COORD_SCALE;
        double worldHeight = (maxY - minY) * COORD_SCALE;

        double pad = 40;
        double availW = Math.max(100, panelWidth - pad * 2);
        double availH = Math.max(100, panelHeight - pad * 2);
        double targetZoom = Math
                .min(availW / Math.max(1, worldWidth), availH / Math.max(1, worldHeight)) * 0.9;

        camera.setZoom(targetZoom);
        camera.setFocusX((minX + maxX) / 2.0);
        camera.setFocusY((minY + maxY) / 2.0);
        camera.setPanX(0);
        camera.setPanY(0);
    }

    public KDTree getKdtree() {
        return kdtree;
    }

}
