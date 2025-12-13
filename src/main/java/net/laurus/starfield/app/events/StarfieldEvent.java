package net.laurus.starfield.app.events;

import java.time.Instant;
import java.util.UUID;

public abstract class StarfieldEvent {

    private final UUID eventId = UUID.randomUUID();

    private final Instant timestamp = Instant.now();

    public UUID getEventId() {
        return eventId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

}
