package net.laurus.starfield.controller;

import static net.laurus.starfield.model.ColourPalette.HUMAN_EXPLORATION_LIFE_TO_EMPTY;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.app.ui.component.StarCanvas3D;
import net.laurus.starfield.model.ColourPalette;
import net.laurus.starfield.model.GridPlane;
import net.laurus.starfield.model.Star;
import net.laurus.starfield.service.factory.StarFactory;

@Getter
@Slf4j
public class MainFxController {

    @Getter
    private List<Star> loadedStars;

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

    @FXML
    private Label gridPanelLabel;

    @FXML
    private CheckBox xyPlaneCheckBox;

    @FXML
    private CheckBox xzPlaneCheckBox;

    @FXML
    private CheckBox yzPlaneCheckBox;

    @FXML
    private ComboBox<ColourPalette> paletteComboBox;

    private StarCanvas3D starCanvasView;

    @FXML
    public void initialize() {
        statusLabel.setText("No data loaded.");
        xyPlaneCheckBox.setSelected(false);
        xzPlaneCheckBox.setSelected(false);
        yzPlaneCheckBox.setSelected(false);
        starCanvasView = new StarCanvas3D(starPanel);

        paletteComboBox.getItems().setAll(ColourPalette.values());
        paletteComboBox.getSelectionModel().select(HUMAN_EXPLORATION_LIFE_TO_EMPTY);

        // Request focus so key events go to the canvas
        configureFocusTraversal();

        log.info("MainFxController initialized and StarCanvas3DView created");
    }

    /**
     * Configures focus traversal so that key events are directed to the canvas,
     * while other UI elements do not take initial focus.
     */
    private void configureFocusTraversal() {
        // Prevent buttons and controls from grabbing focus
        loadButton.setFocusTraversable(false);
        distanceSlider.setFocusTraversable(false);
        paletteComboBox.setFocusTraversable(false);
        xyPlaneCheckBox.setFocusTraversable(false);
        xzPlaneCheckBox.setFocusTraversable(false);
        yzPlaneCheckBox.setFocusTraversable(false);

        // Ensure canvas receives initial focus for key input
        starCanvasView.getCanvas().setFocusTraversable(true);
        starCanvasView.getCanvas().requestFocus();
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
        loadedStars = stars;
        starCanvasView.setStars(loadedStars);

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

    public void toggleGrid(GridPlane plane) {
        log.info("Grid plane update {}", plane);
        starCanvasView.toggleGrid(plane);
    }

    public void updateSlider(double val) {
        log.info("Filter Slider updated: {}", val);

        if (starCanvasView != null) {
            log.info("Slider action");
            starCanvasView.filterStarsByDistance(val, StarFactory.SOL);
        }

    }

    public void handleKeyPressed(KeyEvent e) {

        if (starCanvasView != null) {
            starCanvasView.getInputHandler().handleKeyPressed(e);
        }

    }

    public void handleMouseScroll(ScrollEvent e) {

        if (starCanvasView != null) {
            starCanvasView.getInputHandler().handleMouseScroll(e);
        }

    }

    public void handleMouseMoved(MouseEvent e) {

        if (starCanvasView != null) {

            starCanvasView.getInputHandler().handleMouseMoved(e);
        }

    }

}
