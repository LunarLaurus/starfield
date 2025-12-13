package net.laurus.starfield.ui.handler;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.MainApp;
import net.laurus.starfield.controller.MainFxController;
import net.laurus.starfield.events.DataLoadedEvent;
import net.laurus.starfield.model.Star;
import net.laurus.starfield.service.StarService;

/**
 * Handles UI-side reactions to application-wide events.
 * <p>
 * This class bridges the Spring event system and the JavaFX UI thread.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UiEventHandler {

    private final StarService starService;

    /**
     * Handles completion of star data loading.
     * <p>
     * Updates UI status text and triggers star rendering on the JavaFX thread.
     *
     * @param event Data loaded event
     */
    @EventListener
    public void onDataLoaded(DataLoadedEvent event) {

        Platform.runLater(() -> {

            MainFxController controller = getController();

            if (controller == null) {
                log.warn("FX controller not available; skipping UI update");
                return;
            }

            controller.updateStatus(event.getMessage());

            List<Star> stars = starService.getStars();

            if (stars == null || stars.isEmpty()) {
                log.warn("No stars available for rendering");
                return;
            }

            controller.setStars(stars);
            controller.renderStars(stars);
        });
    }

    /**
     * Returns the active JavaFX controller instance.
     *
     * @return Main FX controller or {@code null} if not yet initialised
     */
    private static MainFxController getController() {
        return MainApp.INSTANCE != null ? MainApp.INSTANCE.getControllerFx() : null;
    }

}
