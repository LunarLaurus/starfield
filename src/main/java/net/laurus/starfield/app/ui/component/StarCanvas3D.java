package net.laurus.starfield.app.ui.component;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.MainApp;
import net.laurus.starfield.app.events.UpdateLabelsUiEvent;
import net.laurus.starfield.app.manager.GridPlaneManager;
import net.laurus.starfield.app.ui.handler.StarCanvasInputHandler;
import net.laurus.starfield.app.ui.handler.StarFilterService;
import net.laurus.starfield.app.ui.handler.StarHoverService;
import net.laurus.starfield.app.ui.handler.StarInfoPopup;
import net.laurus.starfield.app.ui.render.GridRenderer;
import net.laurus.starfield.app.ui.render.StarProjector;
import net.laurus.starfield.app.ui.render.StarRenderer3D;
import net.laurus.starfield.model.GridPlane;
import net.laurus.starfield.model.Star;

/**
 * Main UI component responsible for hosting the star canvas and orchestrating
 * rendering and interaction services.
 *
 * This class contains NO rendering or input logic itself.
 */
@Getter
@Slf4j
public class StarCanvas3D {

    private final Canvas canvas;

    private final Camera3D camera;

    private final StarRenderer3D starRenderer;

    private final GridRenderer gridRenderer;

    private final StarFilterService filterService;

    private final StarCanvasInputHandler inputHandler;

    private List<Star> visibleStars;

    private StarHoverService hoverService;

    @Setter
    private Star hoveredStar;

    private boolean showGrid = true;

    private GridPlaneManager gridPlanes = new GridPlaneManager();

    private StarProjector starProjector;

    public StarCanvas3D(Pane container) {
        this.canvas = new Canvas();
        this.canvas.setFocusTraversable(true);

        container.getChildren().add(canvas);
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());

        this.camera = new Camera3D();

        this.starProjector = new StarProjector(canvas, camera);
        this.starRenderer = new StarRenderer3D(canvas, starProjector);
        this.gridRenderer = new GridRenderer(camera);
        this.filterService = new StarFilterService(this::renderStars);

        StarInfoPopup starPopup = new StarInfoPopup();
        hoverService = new StarHoverService(camera, canvas, starPopup, this::setHoveredStar);
        hoverService.setStars(visibleStars);
        inputHandler = new StarCanvasInputHandler(canvas, camera, this::redraw, hoverService);

        canvas.widthProperty().addListener((o, a, b) -> redraw());
        canvas.heightProperty().addListener((o, a, b) -> redraw());

        log.info("StarCanvas3DView initialized");
    }

    public void toggleGrid(GridPlane plane) {
        gridPlanes.toggle(plane);
        redraw();
    }

    /**
     * Sets the complete star dataset (unfiltered).
     */
    public void setStars(List<Star> allStars) {
        filterService.setAllStars(allStars);
        hoverService.setStars(allStars);
    }

    /**
     * Applies distance filtering.
     */
    public void filterStarsByDistance(double maxDistance, Star reference) {
        filterService.filter(maxDistance, reference);
    }

    /**
     * Renders the provided stars.
     */
    public void renderStars(List<Star> visibleStars) {
        this.visibleStars = visibleStars;
        hoverService.setStars(visibleStars);
        canvas.setUserData(visibleStars);
        redraw();
    }

    /**
     * Redraws the entire canvas.
     */
    public void redraw() {
        MainApp.INSTANCE.getEventBus().publish(new UpdateLabelsUiEvent());

        Platform.runLater(() -> {
            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            if (showGrid) {
                // double maxDistance = filterService.getCurrentMaxDistance();
                double maxDistance = 2500;

                for (GridPlane plane : GridPlane.values()) {

                    if (gridPlanes.isEnabled(plane)) {
                        gridRenderer.draw(gc, canvas, maxDistance, plane);
                    }

                }

            }

            if (visibleStars != null) {
                starRenderer.render(gc, visibleStars, hoveredStar);
            }

        });
    }

}
