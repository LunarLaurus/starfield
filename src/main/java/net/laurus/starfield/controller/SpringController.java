package net.laurus.starfield.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.MainApp;
import net.laurus.starfield.app.bus.EventBus;
import net.laurus.starfield.app.events.LoadDataEvent;
import net.laurus.starfield.app.events.SpringPostInitEvent;
import net.laurus.starfield.app.events.StarfieldInputEvent;
import net.laurus.starfield.app.events.ToggleGridPlaneEvent;
import net.laurus.starfield.app.events.UpdateLabelsUiEvent;
import net.laurus.starfield.model.GridPlane;
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
        setupListenersForJavaFXController();
        log.info("MainFxController injected and load button configured");
    }

    private final void setupListenersForJavaFXController() {
        // Button action
        fxController.getLoadButton().setOnAction(e -> onLoadClicked());

        // Slider listener
        fxController
                .getDistanceSlider()
                .valueProperty()
                .addListener((obs, oldVal, newVal) -> onSliderChanged(newVal.doubleValue()));

        // GridPlane checkboxes
        setupGridPlaneListener(fxController.getXyPlaneCheckBox(), GridPlane.XY);
        setupGridPlaneListener(fxController.getXzPlaneCheckBox(), GridPlane.XZ);
        setupGridPlaneListener(fxController.getYzPlaneCheckBox(), GridPlane.YZ);

        fxController
                .getPaletteComboBox()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) ->
                {

                    if (newVal != null) {
                        fxController
                                .getStarCanvasView()
                                .getGridRenderer()
                                .getColourManager()
                                .setPalette(newVal);
                        fxController.getStarCanvasView().redraw();
                    }

                });
    }

    /** Helper to attach checkbox listener for a grid plane */
    private void setupGridPlaneListener(CheckBox checkBox, GridPlane plane) {
        checkBox
                .selectedProperty()
                .addListener((obs, oldVal, newVal) -> fxController.toggleGrid(plane));
    }

    @EventListener
    public void postInit(SpringPostInitEvent event) {
        MainApp.INSTANCE
                .getInputService()
                .registerInput(
                        fxController.getScene(), fxController.getStarCanvasView().getCanvas()
                );
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
    public void toggleGrid(ToggleGridPlaneEvent event) {
        log.info("Caught ToggleGridPlaneEvent");
        toggleGrid(event.getPlane());
    }

    public void toggleGrid(GridPlane plane) {

        if (fxController == null) {
            log.warn("Attempted to render stars but fxController is null");
            return;
        }

        log.info("Updating Grid Plane {} in FX canvas", plane);
        fxController.toggleGrid(plane);
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
                log.debug("Handling KEY_PRESSED: {}", event.getKeyEvent().getCode());
                fxController.handleKeyPressed(event.getKeyEvent());
            }
            case KEY_RELEASED -> {
                log.debug("Handling KEY_RELEASED: {}", event.getKeyEvent().getCode());
                // Optional: implement if needed
            }
            case MOUSE_SCROLL -> {
                log.debug("Handling MOUSE_SCROLL: deltaY={}", event.getScrollEvent().getDeltaY());
                fxController.handleMouseScroll(event.getScrollEvent());
            }
            case MOUSE_MOVED -> {
                log
                        .debug(
                                "Handling MOUSE_MOVED: ({}, {})", event
                                        .getMouseEvent()
                                        .getX(), event.getMouseEvent().getY()
                        );

                Platform.runLater(() -> { fxController.handleMouseMoved(event.getMouseEvent()); });
            }
            case MOUSE_DRAG -> {
                log
                        .info(
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
