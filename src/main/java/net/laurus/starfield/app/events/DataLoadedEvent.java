package net.laurus.starfield.app.events;

public final class DataLoadedEvent extends UiResultEvent {

    private final String message;

    public DataLoadedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
