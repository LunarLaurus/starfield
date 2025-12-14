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

    private final Canvas canvas;

    private final StarInfoPopup popup;

    private final Consumer<Star> hoverCallback;

    private final StarSelectionService selectionService;

    @Getter
    @Setter
    private List<Star> stars;

    @Getter
    @Setter
    private double hoverThreshold = 10; // pixels

    public StarHoverService(
            Camera3D camera,
            Canvas canvas,
            StarInfoPopup popup,
            Consumer<Star> hoverCallback
    ) {
        this.canvas = canvas;
        this.popup = popup;
        this.hoverCallback = hoverCallback;
        this.selectionService = new StarSelectionService(camera);
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

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Delegate hover calculation to StarSelectionService with hoverThreshold
        Star hovered = selectionService
                .findHovered(mouseX, mouseY, stars, width, height, hoverThreshold);

        if (hovered != null) {
            log.debug("Hover detected over star '{}'", hovered.getName());
            popup.show(hovered, mouseX, mouseY, canvas);
        }
        else {
            log.debug("No star hovered, hiding popup");
            popup.hide();
        }

        try {
            hoverCallback.accept(hovered);
        }
        catch (Exception ex) {
            log.error("Error invoking hoverCallback", ex);
        }

    }

}
