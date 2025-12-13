package net.laurus.starfield.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.MainApp;
import net.laurus.starfield.bus.EventBus;
import net.laurus.starfield.events.LoadDataEvent;
import net.laurus.starfield.events.StarfieldInputEvent;
import net.laurus.starfield.events.UpdateLabelsUiEvent;
import net.laurus.starfield.model.Star;

@Slf4j
@Component
public class SpringController {

    @Autowired
    private EventBus eventBus;

    private MainFxController fxController;

    /** Inject MainFxController and setup load button */
    public void setFxController(MainFxController fxController) {
        this.fxController = fxController;
        fxController.getLoadButton().setOnAction(e -> onLoadClicked());

        fxController
                .getDistanceSlider()
                .valueProperty()
                .addListener((obs, oldVal, newVal) -> onSliderChanged(newVal.doubleValue()));

        MainApp.INSTANCE
                .getInputService()
                .registerInput(
                        fxController.getScene(), fxController.getStarCanvasView().getCanvas()
                );
        log.info("MainFxController injected and load button configured");
    }

    /** Handle filtering slider */
    public void onSliderChanged(double newVal) {

        if (fxController == null) {
            log.warn("Filter slider changed but fxController is null");
            return;
        }

        log.debug("Filter Slider clicked.");
        fxController.updateSlider(newVal);
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

    /** Set stars in the FX canvas */
    public void setStars(List<Star> stars) {

        if (fxController == null) {
            log.warn("Attempted to set stars but fxController is null");
            return;
        }

        log.info("Setting {} stars in FX canvas", stars.size());
        fxController.setStars(stars);
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

    @EventListener
    public void updateLabels(UpdateLabelsUiEvent uiEvent) {

        if (fxController == null) {
            log
                    .warn(
                            "Received label update event but fxController is null: {}", uiEvent
                                    .getEventId()
                    );
            return;
        }

        Platform.runLater(() -> { fxController.updateLabels(); });

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
