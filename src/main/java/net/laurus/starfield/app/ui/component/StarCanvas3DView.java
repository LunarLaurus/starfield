package net.laurus.starfield.app.ui.component;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.MainApp;
import net.laurus.starfield.app.events.UpdateLabelsUiEvent;
import net.laurus.starfield.app.ui.render.StarRenderer;
import net.laurus.starfield.model.Star;

@Getter
@Slf4j
public class StarCanvas3DView {

    private static final double GRID_EXTENT = 1500;

    private static final double GRID_STEP = 100;

    private final Canvas canvas;

    private final Camera3D camera;

    private final StarRenderer starRender;

    private double currentMaxDistance = 2000; // default: show all

    /** Full list of stars (never filtered) */
    private List<Star> allStars;

    /** Currently displayed stars */
    private List<Star> stars;

    private Star hoveredStar;

    private double mousePrevX, mousePrevY;

    private boolean dragging;

    private boolean showGrid = true;

    /**
     * Constructs the 3D star canvas inside the given container pane.
     *
     * @param container The Pane in which to place the Canvas.
     */
    public StarCanvas3DView(Pane container) {
        this.canvas = new Canvas();
        this.canvas.setFocusTraversable(true);
        container.getChildren().add(canvas);

        this.canvas.widthProperty().bind(container.widthProperty());
        this.canvas.heightProperty().bind(container.heightProperty());

        this.camera = new Camera3D();
        this.starRender = new StarRenderer(canvas, camera);
        setupListeners();
        log.info("StarCanvas3DView initialized");
    }

    /**
     * Sets up mouse, scroll, and resize listeners.
     */
    private void setupListeners() {
        canvas.widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> redraw());

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(e -> dragging = false);
        canvas.setOnMouseMoved(this::handleMouseMoved);
        canvas.setOnScroll(this::handleMouseScroll);
    }

    /**
     * Sets the full star list. Keeps a copy of all stars to allow filtering without
     * losing the original dataset.
     *
     * @param stars The list of all stars to display.
     */
    public void setStars(List<Star> stars) {
        this.allStars = new ArrayList<>(stars);
        log.info("Star list set: {} stars loaded", stars.size());
        filterStarsByDistance(currentMaxDistance); // Apply current slider
    }

    /**
     * Renders the given list of stars on the canvas.
     *
     * @param stars The stars to render.
     */
    public void renderStars(List<Star> stars) {
        this.stars = stars;
        log.info("Rendering {} stars", stars.size());
        redraw();
    }

    /**
     * Redraws the canvas including the grid and all currently displayed stars.
     */
    public void redraw() {
        MainApp.INSTANCE.getEventBus().publish(new UpdateLabelsUiEvent());
        Platform.runLater(() -> {

            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Clear
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Grid
            if (showGrid) {
                drawGrid(gc);
            }

            // Stars
            if (stars != null) {
                starRender.render(gc, stars, hoveredStar);
            }

        });
    }

    // -------------------- Input Handlers --------------------

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
        log.debug("Camera zoom: fov={}", camera.getFov());
        redraw();
    }

    /**
     * Handles WASDQE camera movement.
     *
     * @param e Key event
     */
    public void handleKeyPressed(KeyEvent e) {
        double moveSpeed = 10;

        switch (e.getCode()) {
            case W -> camera.move(moveSpeed, 0, 0);
            case S -> camera.move(-moveSpeed, 0, 0);
            case A -> camera.move(0, -moveSpeed, 0);
            case D -> camera.move(0, moveSpeed, 0);
            case Q -> camera.move(0, 0, -moveSpeed);
            case E -> camera.move(0, 0, moveSpeed);

            case UP -> camera.rotate(0, -10);
            case DOWN -> camera.rotate(0, 10);
            case LEFT -> camera.rotate(-10, 0);
            case RIGHT -> camera.rotate(10, 0);
            default -> {
                log.debug("Unhandled key pressed: {}", e.getCode());
                return;
            }
        }

        log
                .debug(
                        "Camera moved [key={}]: x={}, y={}, z={}, pitch={}, yaw={}", e
                                .getCode(), camera.getX(), camera
                                        .getY(), camera.getZ(), camera.getPitch(), camera.getYaw()
                );

        redraw();
    }

    // -------------------- Grid --------------------

    private void drawGrid(GraphicsContext gc) {
        gc.setLineWidth(0.3);

        for (double i = -GRID_EXTENT; i <= GRID_EXTENT; i += GRID_STEP) {

            // Distance band from origin (0,0)
            int band = (int) (Math.abs(i) / GRID_STEP);

            gc.setStroke(getGridColour(band));

            // Vertical lines (X = i)
            double[] v1 = camera.project(i, 0, -GRID_EXTENT, canvas.getWidth(), canvas.getHeight());
            double[] v2 = camera.project(i, 0, GRID_EXTENT, canvas.getWidth(), canvas.getHeight());
            gc.strokeLine(v1[0], v1[1], v2[0], v2[1]);

            // Horizontal lines (Z = i)
            double[] h1 = camera.project(-GRID_EXTENT, 0, i, canvas.getWidth(), canvas.getHeight());
            double[] h2 = camera.project(GRID_EXTENT, 0, i, canvas.getWidth(), canvas.getHeight());
            gc.strokeLine(h1[0], h1[1], h2[0], h2[1]);
        }

    }

    private Color getGridColour(int band) {

        if (band == 0) {
            return Color.LIGHTGREEN; // origin axes
        }

        switch (band % 5) {
            case 1:
                return Color.GREEN;
            case 2:
                return Color.DARKGREEN;
            case 3:
                return Color.LIGHTBLUE;
            case 4:
                return Color.BLUE;
            default:
                return Color.DARKBLUE;
        }

    }

    // -------------------- Filtering --------------------

    /**
     * Filter stars within maxDistance of a reference star (default Sol at 0,0,0).
     * Stores the current maxDistance so future updates (e.g., slider changes) work.
     *
     * @param maxDistance Maximum distance in light-years
     */
    public void filterStarsByDistance(double maxDistance) {
        filterStarsByDistance(maxDistance, null);
    }

    /**
     * Filter stars within maxDistance of a reference star. If reference is null,
     * Sol at (0,0,0) is used. Stores the current maxDistance so future updates
     * (e.g., slider changes) work.
     *
     * @param maxDistance Maximum distance in light-years
     * @param reference   Reference star, or null for Sol
     */
    public void filterStarsByDistance(double maxDistance, Star reference) {

        if (allStars == null) {
            log.warn("filterStarsByDistance called but allStars is null; skipping filter");
            return;
        }

        // Store current slider value for consistency
        this.currentMaxDistance = maxDistance;

        log
                .info(
                        "Filtering stars with maxDistance={} ly relative to {}", maxDistance, reference != null
                                ? reference.getName()
                                : "Sol"
                );

        List<Star> filtered = new ArrayList<>();

        for (int i = 0; i < allStars.size(); i++) {
            Star s = allStars.get(i);

            if (i % 250 == 0) {
                // log.info("Processing star {} of {}", i, allStars.size());
            }

            double distance = s.distanceTo(reference);

            if (distance <= maxDistance) {
                filtered.add(s);
                log.debug("Star {} passes filter (distance = {} ly)", s.getName(), distance);
            }
            else if (distance < maxDistance * 1.1) { // near threshold
                log.debug("Star {} near threshold (distance = {} ly)", s.getName(), distance);
            }

            if (distance <= maxDistance) {
                filtered.add(s);
                log.debug("Star {} passed filter (distance={} ly)", s.getName(), distance);
            }

        }

        log.info("Filtered stars: {} of {} total stars", filtered.size(), allStars.size());

        renderStars(filtered);
    }

}
