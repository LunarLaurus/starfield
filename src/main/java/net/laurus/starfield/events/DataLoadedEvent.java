package net.laurus.starfield.events;

public final class DataLoadedEvent extends UiResultEvent {

    private final String message;

    public DataLoadedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
