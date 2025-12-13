package net.laurus.starfield.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.bus.EventBus;
import net.laurus.starfield.events.LoadDataEvent;
import net.laurus.starfield.events.StarfieldInputEvent;
import net.laurus.starfield.model.Star;
import net.laurus.starfield.service.InputService;

@Slf4j
@Component
public class SpringController {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private InputService inputService;

    private MainFxController fxController;

    /** Inject MainFxController and setup load button */
    public void setFxController(MainFxController fxController) {
        this.fxController = fxController;
        fxController.getLoadButton().setOnAction(e -> onLoadClicked());
        log.info("MainFxController injected and load button configured");
    }

    /** Handle load button click */
    public void onLoadClicked() {

        if (fxController == null) {
            log.warn("Load clicked but fxController is null");
            return;
        }

        log.info("Load button clicked, publishing LoadDataEvent");
        fxController.updateStatus("Loading stars...");
        eventBus.publish(new LoadDataEvent());
    }

    /** Render stars in the FX canvas */
    public void renderStars(List<Star> stars) {

        if (fxController == null) {
            log.warn("Attempted to render stars but fxController is null");
            return;
        }

        log.info("Rendering {} stars in FX canvas", stars.size());
        fxController.renderStars(stars);
    }

    /** Handle input events asynchronously */
    @Async("backgroundExecutor")
    @org.springframework.context.event.EventListener
    public void handleInput(StarfieldInputEvent event) {

        if (fxController == null) {
            log.warn("Received input event but fxController is null: {}", event.getType());
            return;
        }

        switch (event.getType()) {
            case KEY_PRESSED -> {
                log.info("Handling KEY_PRESSED: {}", event.getKeyEvent().getCode());
                fxController.getStarCanvasView().handleKeyPressed(event.getKeyEvent());
            }
            case KEY_RELEASED -> {
                log.info("Handling KEY_RELEASED: {}", event.getKeyEvent().getCode());
                // Optional: implement if needed
            }
            case MOUSE_SCROLL -> {
                log.debug("Handling MOUSE_SCROLL: deltaY={}", event.getScrollEvent().getDeltaY());
                fxController.getStarCanvasView().handleMouseScroll(event.getScrollEvent());
            }
            case MOUSE_DRAG -> {
                log
                        .debug(
                                "Handling MOUSE_DRAG at ({}, {})", event
                                        .getMouseEvent()
                                        .getX(), event.getMouseEvent().getY()
                        );
                // Optional: implement drag handling if needed
            }
            default -> log.warn("Unhandled input event type: {}", event.getType());
        }

    }

}
