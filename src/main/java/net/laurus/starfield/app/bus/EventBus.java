package net.laurus.starfield.app.bus;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starfield.app.events.StarfieldEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventBus {

    private final ApplicationEventPublisher publisher;

    /**
     * Publishes a StarfieldEvent to the Spring application context.
     *
     * @param event the StarfieldEvent to publish
     * @param <T>   type of StarfieldEvent
     */
    public <T extends StarfieldEvent> void publish(T event) {

        if (event == null) {
            log.warn("Attempted to publish null event, ignoring");
            return;
        }

        log.debug("Publishing event: {}", event.getClass().getSimpleName());
        publisher.publishEvent(event);
        log.trace("Event {} published successfully", event.getClass().getSimpleName());
    }

}
