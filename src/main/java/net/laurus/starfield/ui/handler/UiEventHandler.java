package net.laurus.starfield.ui.handler;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import net.laurus.starfield.MainApp;
import net.laurus.starfield.controller.MainFxController;
import net.laurus.starfield.events.DataLoadedEvent;
import net.laurus.starfield.model.Star;
import net.laurus.starfield.service.StarService;

@Component
@RequiredArgsConstructor
public class UiEventHandler {

    private final StarService starService;

    @EventListener
    public void onDataLoaded(DataLoadedEvent event) {
        Platform.runLater(() -> {
            getController().updateStatus(event.getMessage());

            // Render stars
            List<Star> stars = starService.getCachedStars();

            if (stars != null && !stars.isEmpty()) {
                getController().setStars(stars);
                getController().renderStars(stars);
            }

        });
    }

    private static final MainFxController getController() {
        return MainApp.INSTANCE.getControllerFx();
    }

}
