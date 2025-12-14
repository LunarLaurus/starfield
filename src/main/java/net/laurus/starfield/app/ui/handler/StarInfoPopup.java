package net.laurus.starfield.app.ui.handler;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.model.Star;

@Slf4j
public class StarInfoPopup {

    private final Popup popup;

    private final Label label;

    public StarInfoPopup() {
        label = new Label();
        label.setStyle("""
                -fx-background-color: rgba(0,0,0,0.8);
                -fx-text-fill: white;
                -fx-padding: 8px;
                -fx-border-radius: 4px;
                -fx-background-radius: 4px;
                -fx-font-size: 12px;
                """);

        popup = new Popup();
        popup.getContent().add(label);
        popup.setAutoHide(true);

        log.info("StarInfoPopup initialized");
    }

    /**
     * Show the popup for a given star at mouse coordinates relative to the canvas.
     */
    public void show(Star star, double mouseX, double mouseY, Canvas canvas) {

        if (star == null) {
            hide();
            return;
        }

        if (canvas == null || canvas.getScene() == null || canvas.getScene().getWindow() == null) {
            log.warn("Cannot show popup: canvas or window not ready");
            return;
        }

        String colourText = (star.getColour() != null) ? String
                .format(
                        "RGB: %.2f, %.2f, %.2f", star.getColour().getRed(), star
                                .getColour()
                                .getGreen(), star.getColour().getBlue()
                ) : "RGB: N/A";

        // Compose popup text
        String text = String
                .format(
                        "Star: %s%n" + "Coordinates: X=%d, Y=%d, Z=%d%n"
                                + "Parsec: X=%.2f, Y=%.2f, Z=%.2f%n" + "Distance: %.2f pc%n"
                                + "Magnitude: %.2f%n"
                                + "%s", safeString(star.getName()), star.getX(), star.getY(), star
                                        .getZ(), star.getXParsec(), star.getYParsec(), star
                                                .getZParsec(), star.getDistanceInParsecs(), star
                                                        .getMagnitude(), colourText
                );

        label.setText(text);

        // Show popup if not already showing
        if (!popup.isShowing()) {
            popup.show(canvas.getScene().getWindow());
        }

        // Position near mouse
        double offsetX = 10;
        double offsetY = 10;
        popup.setX(canvas.getScene().getWindow().getX() + mouseX + offsetX);
        popup.setY(canvas.getScene().getWindow().getY() + mouseY + offsetY);

        log
                .debug(
                        "Showing popup for '{}' at screen coordinates ({}, {})", star
                                .getName(), popup.getX(), popup.getY()
                );
    }

    /**
     * Hide the popup.
     */
    public void hide() {

        if (popup.isShowing()) {
            popup.hide();
            log.debug("Star info popup hidden");
        }

    }

    /** Safe string helper */
    private String safeString(String str) {
        return str != null ? str : "N/A";
    }

}
