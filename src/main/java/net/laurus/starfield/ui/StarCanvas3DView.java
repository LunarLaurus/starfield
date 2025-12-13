package net.laurus.starfield.ui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import net.laurus.starfield.model.Star;

@Getter
public class StarCanvas3DView {

    private static final Logger log = LoggerFactory.getLogger(StarCanvas3DView.class);

    private final Canvas canvas;

    private final Camera3D camera;

    private List<Star> stars;

    private Star hoveredStar;

    private double mousePrevX, mousePrevY;

    private boolean dragging;

    private boolean showGrid = true;

    public StarCanvas3DView(Pane container) {
        this.canvas = new Canvas();
        this.canvas.setFocusTraversable(true);
        container.getChildren().add(canvas);

        this.canvas.widthProperty().bind(container.widthProperty());
        this.canvas.heightProperty().bind(container.heightProperty());

        this.camera = new Camera3D();

        setupListeners();
        log.info("StarCanvas3DView initialized");
    }

    private void setupListeners() {
        canvas.widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> redraw());

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(e -> dragging = false);
        canvas.setOnMouseMoved(this::handleMouseMoved);
        canvas.setOnScroll(this::handleMouseScroll);
    }

    public void renderStars(List<Star> stars) {
        this.stars = stars;
        log.info("Rendering {} stars", stars.size());
        redraw();
    }

    public void redraw() {
        Platform.runLater(() -> {
            var gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            if (showGrid) {
                drawGrid(gc);
            }

            if (stars != null) {

                for (Star s : stars) {
                    double[] p = camera
                            .project(
                                    s.getX(), s.getY(), s.getZ(), canvas.getWidth(), canvas
                                            .getHeight()
                            );
                    double sx = p[0];
                    double sy = p[1];
                    double depth = p[2];

                    double radius = Math.max(1, 1 * camera.getFov() / depth);
                    gc.setFill(Color.WHITE);
                    gc.fillOval(sx - radius / 2, sy - radius / 2, radius, radius);

                    if (s == hoveredStar) {
                        gc.setStroke(Color.CYAN);
                        gc.strokeOval(sx - radius, sy - radius, radius * 2, radius * 2);
                    }

                }

            }

        });
    }

    private void handleMousePressed(MouseEvent e) {
        dragging = true;
        mousePrevX = e.getX();
        mousePrevY = e.getY();
        log.info("Mouse pressed at ({}, {})", e.getX(), e.getY());
    }

    private void handleMouseDragged(MouseEvent e) {

        if (!dragging) {
            return;
        }

        double dx = e.getX() - mousePrevX;
        double dy = e.getY() - mousePrevY;

        camera.rotate(dx * 0.2, -dy * 0.2);
        log.info("Camera rotated: yaw={}, pitch={}", camera.getYaw(), camera.getPitch());

        mousePrevX = e.getX();
        mousePrevY = e.getY();

        redraw();
    }

    private void handleMouseMoved(MouseEvent e) {

        if (stars == null) {
            return;
        }

        double mx = e.getX();
        double my = e.getY();
        hoveredStar = stars.stream().filter(s -> {
            double[] p = camera
                    .project(s.getX(), s.getY(), s.getZ(), canvas.getWidth(), canvas.getHeight());
            double dx = p[0] - mx;
            double dy = p[1] - my;
            return Math.sqrt(dx * dx + dy * dy) < 10;
        }).findFirst().orElse(null);

        if (hoveredStar != null) {
            log.info("Hovering over star: {}", hoveredStar.getName());
        }

        redraw();
    }

    public void handleMouseScroll(ScrollEvent e) {
        double factor = e.getDeltaY() > 0 ? 0.9 : 1.1;
        camera.zoom(factor);
        log.info("Camera zoom: fov={}", camera.getFov());
        redraw();
    }

    public void handleKeyPressed(KeyEvent e) {
        double moveSpeed = 100;

        switch (e.getCode()) {
            case W -> camera.move(moveSpeed, 0, 0);
            case S -> camera.move(-moveSpeed, 0, 0);
            case A -> camera.move(0, -moveSpeed, 0);
            case D -> camera.move(0, moveSpeed, 0);
            case Q -> camera.move(0, 0, -moveSpeed);
            case E -> camera.move(0, 0, moveSpeed);
            default -> {
                log.debug("Unhandled key pressed: {}", e.getCode());
                return;
            }
        }

        log
                .info(
                        "Camera moved [key={}]: x={}, y={}, z={}, pitch={}, yaw={}", e
                                .getCode(), camera.getX(), camera
                                        .getY(), camera.getZ(), camera.getPitch(), camera.getYaw()
                );

        redraw();
    }

    private void drawGrid(javafx.scene.canvas.GraphicsContext gc) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.3);
        double step = 200;

        for (double i = -1000; i <= 1000; i += step) {
            double[] p1 = camera.project(i, 0, -1000, canvas.getWidth(), canvas.getHeight());
            double[] p2 = camera.project(i, 0, 1000, canvas.getWidth(), canvas.getHeight());
            gc.strokeLine(p1[0], p1[1], p2[0], p2[1]);

            double[] p3 = camera.project(-1000, 0, i, canvas.getWidth(), canvas.getHeight());
            double[] p4 = camera.project(1000, 0, i, canvas.getWidth(), canvas.getHeight());
            gc.strokeLine(p3[0], p3[1], p4[0], p4[1]);
        }

    }

}
