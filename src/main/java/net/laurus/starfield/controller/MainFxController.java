package net.laurus.starfield.controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.model.Star;
import net.laurus.starfield.ui.StarCanvas3DView;

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
    private Button loadButton;

    private StarCanvas3DView starCanvasView;

    @FXML
    public void initialize() {
        statusLabel.setText("No data loaded.");
        starCanvasView = new StarCanvas3DView(starPanel);

        // Request focus so key events go to the canvas
        loadButton.setFocusTraversable(false);
        starCanvasView.getCanvas().setFocusTraversable(true);
        starCanvasView.getCanvas().requestFocus();
        log.info("MainFxController initialized and StarCanvas3DView created");
    }

    public void renderStars(List<Star> stars) {
        log.info("Rendering {} stars in StarCanvas3DView", stars.size());
        starCanvasView.renderStars(stars);
    }

    public void updateStatus(String text) {
        log.info("Status updated: {}", text);
        statusLabel.setText(text);
    }

    @FXML
    public void handleKeyPressed(KeyEvent e) {
        log.info("Key pressed: {}", e.getCode());
        starCanvasView.handleKeyPressed(e);
    }

}
