package net.laurus.starmapper.ui.input;

import java.awt.event.MouseEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starmapper.ui.component.Camera;

/**
 * Handles pan input and inertia
 */
@Slf4j
@Getter
@Setter
public class PanHandler {

    private final StarInputHandler starInputHandler;

    private double velX = 0, velY = 0;

    private int lastX, lastY;

    private int pressedButton = MouseEvent.NOBUTTON;

    private long lastDragTime = 0;

    public PanHandler(StarInputHandler starInputHandler) {
        this.starInputHandler = starInputHandler;
    }

    public void onMousePressed(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON3) { // right = pan
            pressedButton = e.getButton();
            lastX = e.getX();
            lastY = e.getY();
            lastDragTime = System.nanoTime();
            velX = velY = 0;
        }

    }

    public void onMouseReleased(MouseEvent e) {
        pressedButton = MouseEvent.NOBUTTON;
    }

    public void onMouseDragged(MouseEvent e) {
        if (pressedButton != MouseEvent.BUTTON3)
            return;

        int dx = e.getX() - lastX;
        int dy = e.getY() - lastY;
        long now = System.nanoTime();
        double dt = Math.max(1e-6, (now - lastDragTime) / 1e9);
        velX = dx / dt;
        velY = dy / dt;
        lastDragTime = now;

        Camera cam = starInputHandler.getPanel().getCamera();
        cam.setPanX(cam.getPanX() + dx);
        cam.setPanY(cam.getPanY() + dy);

        lastX = e.getX();
        lastY = e.getY();
        starInputHandler.getPanel().repaint();
        log.debug("Panned: panX={}, panY={}", cam.getPanX(), cam.getPanY());
    }

    public void tick() {
        Camera cam = starInputHandler.getPanel().getCamera();
        if (!cam.isUseInertia())
            return;

        if (Math.abs(cam.getVelPanX()) > 1e-3 || Math.abs(cam.getVelPanY()) > 1e-3) {
            cam.update(0.016);
            starInputHandler.getPanel().repaint();
        }

    }

}
