package net.laurus.starfield.app.ui.handler;

import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.MainApp;
import net.laurus.starfield.model.Star;

@Slf4j
public class StarInfoPopup {

    private final Popup popup;

    private final Label label;

    private static final double FONT_SIZE = 10.0; // smaller font

    private static final int COLUMN_WIDTH = 25; // approx chars per column

    private static final int MAX_COLUMNS = 3;

    public StarInfoPopup() {
        label = new Label();
        label.setStyle("""
                -fx-background-color: rgba(0,0,0,0.8);
                -fx-text-fill: white;
                -fx-padding: 6px;
                -fx-border-radius: 4px;
                -fx-background-radius: 4px;
                -fx-font-size: 10px;
                """);
        label.setFont(Font.font(FONT_SIZE));

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

        // Compose star names in columns
        List<String> starNames = MainApp.INSTANCE.getStarNames().getNames(star.getId());
        String namesText = formatColumns(starNames, MAX_COLUMNS);

        String colourText = (star.getColour() != null) ? String
                .format(
                        "RGB: %.2f, %.2f, %.2f", safeDouble(star.getColour().getRed()), safeDouble(
                                star.getColour().getGreen()
                        ), safeDouble(star.getColour().getBlue())
                ) : "RGB: N/A";

        String text = String
                .format(
                        "Star: %s%n%s%nCoordinates: X=%s, Y=%s, Z=%s%nParsec: X=%s, Y=%s, Z=%s%nDistance: %s pc%nMagnitude: %s%n%s", safeString(
                                star.getName()
                        ), namesText, safeDoubleString(star.getX()), safeDoubleString(
                                star.getY()
                        ), safeDoubleString(star.getZ()), safeDoubleString(
                                star.getXParsec()
                        ), safeDoubleString(star.getYParsec()), safeDoubleString(
                                star.getZParsec()
                        ), safeDoubleString(
                                star.getDistanceInParsecs()
                        ), safeDoubleString(star.getMagnitude()), colourText
                );

        label.setText(text);

        if (!popup.isShowing()) {
            popup.show(canvas.getScene().getWindow());
        }

        double offsetX = 10;
        double offsetY = 10;
        popup.setX(canvas.getScene().getWindow().getX() + mouseX + offsetX);
        popup.setY(canvas.getScene().getWindow().getY() + mouseY + offsetY);

        log.debug("Showing popup for '{}' at ({}, {})", star.getName(), popup.getX(), popup.getY());
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

    /** Safe numeric formatting helper */
    private String safeDoubleString(Double d) {
        return d != null ? String.format("%.2f", d) : "N/A";
    }

    /** Safe double for RGB */
    private double safeDouble(Double d) {
        return d != null ? d : 0.0;
    }

    /**
     * Format a list of strings into multiple columns for display
     */
    private String formatColumns(List<String> items, int maxColumns) {

        if (items == null || items.isEmpty()) {
            return "";
        }

        int total = items.size();
        int rows = (int) Math.ceil((double) total / maxColumns);
        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < rows; r++) {

            for (int c = 0; c < maxColumns; c++) {
                int idx = c * rows + r;

                if (idx < total) {
                    String name = items.get(idx);
                    sb.append(String.format("%-" + COLUMN_WIDTH + "s", name));
                }

            }

            sb.append("\n");
        }

        return sb.toString();
    }

}
