package net.laurus.starmapper.ui.panel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starmapper.model.Star;
import net.laurus.starmapper.ui.component.Camera;
import net.laurus.starmapper.ui.component.KDTree;
import net.laurus.starmapper.ui.input.RotationHandler;
import net.laurus.starmapper.ui.input.StarInputHandler;
import net.laurus.starmapper.ui.render.StarRenderer;

/**
 * Main panel: delegates input, rendering, selection, and camera handling
 */
@Slf4j
public class StarMapPanel extends JPanel {

    private final List<Star> stars;

    @Getter
    private final Camera camera = new Camera();

    @Getter
    @Setter
    private Star selectedStar;

    @Getter
    private final StarInputHandler inputHandler;

    @Getter
    private final StarRenderer renderer;

    @Getter
    private final KDTree kdtree;

    public StarMapPanel(List<Star> stars) {

        if (stars == null || stars.isEmpty()) {
            throw new IllegalArgumentException("Star list must not be null or empty");
        }

        this.stars = stars;
        this.kdtree = new KDTree(stars);
        setBackground(java.awt.Color.BLACK);

        // Enable camera inertia by default off
        camera.setUseInertia(false);

        // Initialize renderer
        renderer = new StarRenderer(this.stars, this.camera, this.kdtree);

        // Initialize input handler (modular)
        inputHandler = new StarInputHandler(this, renderer);

        // Enable tooltips
        setToolTipText("");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Object aa = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Build rotation matrix from modular rotation handler
        RotationHandler rotHandler = inputHandler.getRotationHandler();
        double[][] rotMatrix = StarRenderer
                .buildRotationMatrix(rotHandler.getRotX(), rotHandler.getRotY());

        // Render stars
        renderer.render(g2d, getWidth(), getHeight(), selectedStar, rotMatrix);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);
    }

    /** Reset rotation using modular input handler */
    public void resetRotation() {
        inputHandler.resetRotation();
        repaint();
        log.debug("Rotation reset to zero");
    }

    /** Set distance filter */
    public void setDistanceFilter(double parsecs) {
        renderer.setDistanceFilter(parsecs);
        repaint();
        log.debug("Distance filter set to {}", parsecs);
    }

    /** Zoom to fit selected cluster or distance filter */
    public void zoomToFitSelected(double radiusParsecs) {
        renderer.zoomToFitSelected(selectedStar, radiusParsecs, getWidth(), getHeight());
        repaint();
        log.debug("Zoomed to fit selected star or radius {} parsecs", radiusParsecs);
    }

    public List<Star> getStars() {
        return stars;
    }

    /**
     * Update camera every frame to apply inertia and modular pan handler. Call this
     * from your animation loop or Swing timer.
     */
    public void update(double deltaSeconds) {

        if (camera.isUseInertia() && camera.isMoving()) {
            camera.update(deltaSeconds);
            repaint();
            log
                    .trace(
                            "Camera inertia applied: panX={}, panY={}", camera.getPanX(), camera
                                    .getPanY()
                    );
        }

        // Tick modular input handlers that need frame updates
        inputHandler.getPanHandler().tick();
        inputHandler.getHoverHandler().tick();

    }

}
