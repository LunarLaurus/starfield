package net.laurus.starmapper.ui.input;

import java.awt.Point;
import java.awt.event.MouseEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starmapper.model.Star;
import net.laurus.starmapper.ui.render.StarRenderer;

/**
 * Handles hover tooltips
 */
@Slf4j
@Getter
@Setter
public class HoverHandler {

    private final StarInputHandler starInputHandler;

    private long hoverSince = 0;

    private Point lastMousePos = new Point(0, 0);

    public HoverHandler(StarInputHandler starInputHandler) {
        this.starInputHandler = starInputHandler;
    }

    public void onMouseMoved(MouseEvent e) {
        lastMousePos = e.getPoint();
        hoverSince = System.currentTimeMillis();
    }

    public void tick() {

        if (hoverSince > 0 && (System.currentTimeMillis() - hoverSince) > 100) {
            updateHover(lastMousePos.x, lastMousePos.y);
            hoverSince = 0;
        }

    }

    private void updateHover(int sx, int sy) {
        var panel = starInputHandler.getPanel();
        var renderer = starInputHandler.getRenderer();

        Star center = panel.getSelectedStar() != null ? panel.getSelectedStar()
                : panel.getStars().get(0);

        if (center == null) {
            panel.setToolTipText(null);
            return;
        }

        double probeParsecs = Math.max(5.0, renderer.getMaxDistance() * 0.1);
        var near = renderer
                .getKdtree()
                .range(center.getX(), center.getY(), center.getZ(), probeParsecs);

        if (near.isEmpty()) {
            panel.setToolTipText(null);
            return;
        }

        double bestSq = Double.POSITIVE_INFINITY;
        Star best = null;
        double[][] rotMatrix = StarRenderer
                .buildRotationMatrix(
                        starInputHandler.getRotationHandler().getRotX(), starInputHandler
                                .getRotationHandler()
                                .getRotY()
                );

        for (Star s : near) {
            int[] p = renderer
                    .projectStarToScreen(s, center, rotMatrix, panel.getWidth(), panel.getHeight());
            double dx = p[0] - sx;
            double dy = p[1] - sy;
            double dsq = dx * dx + dy * dy;

            if (dsq < bestSq) {
                bestSq = dsq;
                best = s;
            }

        }

        if (best != null && bestSq < StarRenderer.SELECTION_RADIUS * 4) {
            panel.setToolTipText(best.getName() + " (id=" + best.getId() + ")");
        }
        else {
            panel.setToolTipText(null);
        }

        log.debug("Hover update: {}", best != null ? best.getName() : "none");
    }

}
