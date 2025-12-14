package net.laurus.starfield.app.ui.handler;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.app.ui.component.Camera3D;

@Slf4j
public class StarCanvasInputHandler {

    private final Camera3D camera;

    private final StarHoverService hoverService;

    private final Runnable redraw;

    private double prevX, prevY;

    private boolean dragging;

    public StarCanvasInputHandler(
            Canvas canvas,
            Camera3D camera,
            Runnable redraw,
            StarHoverService hoverService
    ) {
        this.camera = camera;
        this.redraw = redraw;
        this.hoverService = hoverService;

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnMouseMoved(this::handleMouseMoved);
        canvas.setOnScroll(this::handleMouseScroll);
        canvas.setOnKeyPressed(this::handleKeyPressed);
    }

    public void handleMousePressed(MouseEvent e) {
        dragging = true;
        prevX = e.getX();
        prevY = e.getY();
        log.debug("Mouse pressed at ({}, {})", e.getX(), e.getY());
    }

    public void handleMouseDragged(MouseEvent e) {

        if (!dragging) {
            return;
        }

        double dx = e.getX() - prevX;
        double dy = e.getY() - prevY;
        camera.rotate(dx * 0.2, -dy * 0.2);
        prevX = e.getX();
        prevY = e.getY();
        log.debug("Mouse dragged dx={}, dy={}", dx, dy);
        redraw.run();
    }

    public void handleMouseReleased(MouseEvent e) {
        dragging = false;
        log.debug("Mouse released at ({}, {})", e.getX(), e.getY());
    }

    public void handleMouseMoved(MouseEvent e) {
        hoverService.updateHover(e.getX(), e.getY());
        log.debug("Mouse moved at ({}, {})", e.getX(), e.getY());
        redraw.run();
    }

    public void handleMouseScroll(ScrollEvent e) {
        camera.zoom(e.getDeltaY() > 0 ? 0.9 : 1.1);
        log.debug("Mouse scrolled deltaY={}", e.getDeltaY());
        redraw.run();
    }

    public void handleKeyPressed(KeyEvent e) {
        double moveSpeed = 10;
        boolean handled = true;

        switch (e.getCode()) {
            case W -> camera.move(moveSpeed, 0, 0);
            case S -> camera.move(-moveSpeed, 0, 0);
            case A -> camera.move(0, -moveSpeed, 0);
            case D -> camera.move(0, moveSpeed, 0);
            case Q -> camera.move(0, 0, -moveSpeed);
            case E -> camera.move(0, 0, moveSpeed);
            case UP -> camera.rotate(0, -10);
            case DOWN -> camera.rotate(0, 10);
            case LEFT -> camera.rotate(-10, 0);
            case RIGHT -> camera.rotate(10, 0);
            default -> handled = false;
        }

        if (handled) {
            log.debug("Key pressed: {}, camera updated", e.getCode());
            redraw.run();
        }

    }

}
