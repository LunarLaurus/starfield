package net.laurus.starfield.controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.model.Star;
import net.laurus.starfield.ui.component.StarCanvas3DView;

@Getter
@Slf4j
public class MainFxController {

    @Setter
    private Scene scene;

    @FXML
    private Pane starPanel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label coordsLabel;

    @FXML
    private Label cameraDetailsLabel;

    @FXML
    private Button loadButton;

    @FXML
    private Slider distanceSlider;

    private StarCanvas3DView starCanvasView;

    @FXML
    public void initialize() {
        statusLabel.setText("No data loaded.");
        starCanvasView = new StarCanvas3DView(starPanel);

        // Request focus so key events go to the canvas
        loadButton.setFocusTraversable(false);
        distanceSlider.setFocusTraversable(false);
        starCanvasView.getCanvas().setFocusTraversable(true);
        starCanvasView.getCanvas().requestFocus();

        log.info("MainFxController initialized and StarCanvas3DView created");
    }

    public void updateLabels() {

        if (starCanvasView == null) {
            log.warn("updateLabels skipped: starCanvasView is null");
            return;
        }

        var camera = starCanvasView.getCamera();

        if (camera == null) {
            log.warn("updateLabels skipped: camera is null");
            return;
        }

        if (coordsLabel == null || cameraDetailsLabel == null) {
            log
                    .warn(
                            "updateLabels skipped: labels not initialised (coordsLabel={}, cameraDetailsLabel={})", coordsLabel != null, cameraDetailsLabel != null
                    );
            return;
        }

        double x = camera.getX();
        double y = camera.getY();
        double z = camera.getZ();

        coordsLabel.setText(String.format("Coordinates  X: %.1f,  Y: %.1f,  Z: %.1f", x, y, z));

        cameraDetailsLabel.setText(camera.toUiString(false));

        log
                .debug(
                        "UI labels updated | pos=({},{},{}) | yaw={} pitch={} fov={}", String
                                .format("%.1f", x), String.format("%.1f", y), String
                                        .format("%.1f", z), String
                                                .format("%.1f", camera.getYaw()), String
                                                        .format("%.1f", camera.getPitch()), String
                                                                .format("%.0f", camera.getFov())
                );
    }

    public void setStars(List<Star> stars) {
        log.info("Settings {} stars in StarCanvas3DView", stars.size());
        starCanvasView.setStars(stars);
    }

    public void renderStars(List<Star> stars) {
        log.info("Rendering {} stars in StarCanvas3DView", stars.size());
        starCanvasView.renderStars(stars);
        updateLabels();
    }

    public void updateStatus(String text) {
        log.info("Status updated: {}", text);
        statusLabel.setText(text);
    }

    public void updateSlider(double val) {
        log.info("Filter Slider updated: {}", val);

        if (starCanvasView != null) {
            log.info("Slider action");
            starCanvasView.filterStarsByDistance(val);
        }

    }

    @FXML
    public void handleKeyPressed(KeyEvent e) {
        log.info("Key pressed: {}", e.getCode());
        starCanvasView.handleKeyPressed(e);
        updateLabels();
    }

}
