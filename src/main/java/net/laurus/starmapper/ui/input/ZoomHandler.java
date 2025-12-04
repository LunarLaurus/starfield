package net.laurus.starmapper.ui.input;

import java.awt.event.MouseWheelEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starmapper.ui.component.Camera;

/**
 * Handles zoom input
 */
@Slf4j
@Getter
@Setter
public class ZoomHandler {

    private final StarInputHandler starInputHandler;

    public ZoomHandler(StarInputHandler starInputHandler) {
        this.starInputHandler = starInputHandler;
    }

    public void onMouseWheelMoved(MouseWheelEvent e) {
        double delta = -e.getPreciseWheelRotation();
        Camera cam = starInputHandler.getPanel().getCamera();
        double zoom = cam.getZoom();
        zoom *= Math.pow(1.1, delta);
        zoom = Math.max(0.01, Math.min(1000, zoom));
        cam.setZoom(zoom);
        starInputHandler.getPanel().repaint();
        log.debug("Zoom changed: {}", zoom);
    }

}
