package net.laurus.starfield.service;

import org.springframework.stereotype.Service;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.app.bus.EventBus;
import net.laurus.starfield.app.events.StarfieldInputEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class InputService {

    private final EventBus eventBus;

    /**
     * Attach keyboard and mouse handlers to a JavaFX scene and canvas. Ensures the
     * canvas receives input and publishes events to the EventBus.
     */
    public void registerInput(Scene scene, Canvas canvas) {

        if (scene == null || canvas == null) {
            log.error("Cannot register input: scene or canvas is null");
            return;
        }

        // Make canvas focusable and request focus initially
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        log.info("Input registered: canvas focusable and focused");

        // Keyboard events
        scene.setOnKeyPressed(e -> {
            log.debug("Key pressed: {}", e.getCode());
            eventBus.publish(StarfieldInputEvent.keyPressed(e));
        });
        scene.setOnKeyReleased(e -> {
            log.debug("Key released: {}", e.getCode());
            eventBus.publish(StarfieldInputEvent.keyReleased(e));
        });

        // Mouse events
        canvas.setOnMouseDragged(e -> {
            log.debug("Mouse dragged at ({}, {})", e.getX(), e.getY());
            eventBus.publish(StarfieldInputEvent.mouseEvent(e));
        });
        canvas.setOnMouseMoved(e -> {
            log.debug("Mouse moved at ({}, {})", e.getX(), e.getY());
            eventBus.publish(StarfieldInputEvent.mouseEvent(e));
        });

        canvas.setOnScroll(e -> {
            log.debug("Mouse scrolled: deltaY={}", e.getDeltaY());
            eventBus.publish(StarfieldInputEvent.mouseScrolled(e));
        });

        // Optional: request focus when user clicks the canvas
        canvas.setOnMouseClicked(e -> {
            canvas.requestFocus();
            log.debug("Canvas clicked, focus requested");
        });

        log.info("Input handlers successfully registered for scene and canvas");
    }

}
