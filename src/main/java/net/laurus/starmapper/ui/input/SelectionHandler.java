package net.laurus.starmapper.ui.input;

import java.awt.event.MouseEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starmapper.model.Star;
import net.laurus.starmapper.ui.render.StarRenderer;

/**
 * Handles selection input
 */
@Slf4j
@Getter
@Setter
public class SelectionHandler {

    private final StarInputHandler starInputHandler;

    public SelectionHandler(StarInputHandler starInputHandler) {
        this.starInputHandler = starInputHandler;
    }

    public void onMouseClicked(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 1)
            return;

        var panel = starInputHandler.getPanel();
        var renderer = starInputHandler.getRenderer();

        Star center = panel.getSelectedStar() != null ? panel.getSelectedStar()
                : panel.getStars().get(0);
        if (center == null)
            return;

        double radiusParsecs = Math.max(5.0, Math.min(1000.0, renderer.getMaxDistance()));
        var candidates = renderer
                .getKdtree()
                .range(center.getX(), center.getY(), center.getZ(), radiusParsecs);

        double bestSq = StarRenderer.SELECTION_RADIUS * StarRenderer.SELECTION_RADIUS;
        Star best = null;
        double[][] rotMatrix = StarRenderer
                .buildRotationMatrix(
                        starInputHandler.getRotationHandler().getRotX(), starInputHandler
                                .getRotationHandler()
                                .getRotY()
                );

        for (Star s : candidates) {
            int[] p = renderer
                    .projectStarToScreen(s, center, rotMatrix, panel.getWidth(), panel.getHeight());
            double dx = p[0] - e.getX();
            double dy = p[1] - e.getY();
            double dsq = dx * dx + dy * dy;

            if (dsq < bestSq) {
                bestSq = dsq;
                best = s;
            }

        }

        panel.setSelectedStar(best);
        if (best != null)
            panel.repaint();
        log.debug("Selected star: {}", best != null ? best.getName() : "none");
    }

}
