package net.laurus.starfield.app.ui.handler;

import java.util.List;
import java.util.function.Consumer;

import javafx.scene.canvas.Canvas;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.app.ui.component.Camera3D;
import net.laurus.starfield.model.Star;

@Slf4j
public class StarHoverService {

    private final Camera3D camera;

    private final Canvas canvas;

    private final StarInfoPopup popup;

    /** Callback invoked when hovered star changes */
    private final Consumer<Star> hoverCallback;

    @Getter
    @Setter
    private List<Star> stars;

    @Getter
    @Setter
    private double hoverThreshold = 25; // pixels

    public StarHoverService(
            Camera3D camera,
            Canvas canvas,
            StarInfoPopup popup,
            Consumer<Star> hoverCallback
    ) {
        this.camera = camera;
        this.canvas = canvas;
        this.popup = popup;
        this.hoverCallback = hoverCallback;
        log.info("StarHoverService initialized with canvas={} and camera={}", canvas, camera);
    }

    public void updateHover(double mouseX, double mouseY) {
        log.debug("updateHover called at mouse ({}, {})", mouseX, mouseY);

        if (stars == null || stars.isEmpty()) {
            log.debug("No stars available for hover detection, hiding popup");
            popup.hide();
            hoverCallback.accept(null);
            return;
        }

        Star hovered = null;
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        for (Star s : stars) {

            try {
                double[] p = camera.project(s.getX(), s.getY(), s.getZ(), width, height);

                if (p == null || p.length < 2 || Double.isNaN(p[0]) || Double.isNaN(p[1])) {
                    log.debug("Star '{}' projected out of bounds or invalid: {}", s.getName(), p);
                    continue;
                }

                double dx = p[0] - mouseX;
                double dy = p[1] - mouseY;
                double dist = Math.sqrt(dx * dx + dy * dy);

                log
                        .debug(
                                "Star '{}': projected=({}, {}), mouse=({}, {}), dist={}", s
                                        .getName(), p[0], p[1], mouseX, mouseY, dist
                        );

                if (dist <= hoverThreshold) {
                    hovered = s;
                    log.debug("Hover detected over star '{}' at distance {}", s.getName(), dist);
                    break;
                }

            }
            catch (Exception ex) {
                log.error("Error projecting star '{}'", s.getName(), ex);
            }

        }

        // Show or hide the popup
        if (hovered != null) {
            popup.show(hovered, mouseX, mouseY, canvas);
        }
        else {
            log.debug("No star hovered, hiding popup");
            popup.hide();
        }

        // Notify controller of the hovered star
        try {
            hoverCallback.accept(hovered);
        }
        catch (Exception ex) {
            log.error("Error invoking hoverCallback", ex);
        }

    }

}
