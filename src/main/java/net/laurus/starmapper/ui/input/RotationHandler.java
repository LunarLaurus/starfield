package net.laurus.starmapper.ui.input;

import java.awt.event.MouseEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Getter
public class RotationHandler {

    private final StarInputHandler starInputHandler;

    private double rotX = 0;

    private double rotY = 0;

    private int lastX, lastY;

    private int pressedButton = MouseEvent.NOBUTTON;

    public void onMousePressed(MouseEvent e) {
        pressedButton = e.getButton();
        lastX = e.getX();
        lastY = e.getY();
        log.debug("Rotation mouse pressed at ({}, {}) with button {}", lastX, lastY, pressedButton);
    }

    public void onMouseReleased(MouseEvent e) {
        pressedButton = MouseEvent.NOBUTTON;
        log.debug("Rotation mouse released");
    }

    public void onMouseDragged(MouseEvent e) {
        int dx = e.getX() - lastX;
        int dy = e.getY() - lastY;

        if (pressedButton == MouseEvent.BUTTON2) { // middle
            rotY += dx * 0.01;
            rotX += dy * 0.01;
            starInputHandler.getPanel().repaint();
            log.debug("Rotation updated: rotX={}, rotY={}", rotX, rotY);
        }

        lastX = e.getX();
        lastY = e.getY();
    }

    public void resetRotation() {
        rotX = 0;
        rotY = 0;
        log.debug("Rotation reset");
    }

    public void incrementRotation(double dx, double dy) {
        this.rotX += dx;
        this.rotY += dy;
        starInputHandler.getPanel().repaint(); // repaint immediately after programmatic rotation
        log.debug("Programmatic rotation incremented: rotX={}, rotY={}", rotX, rotY);
    }

}
