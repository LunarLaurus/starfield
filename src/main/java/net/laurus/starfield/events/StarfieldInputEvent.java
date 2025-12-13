package net.laurus.starfield.events;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class StarfieldInputEvent extends StarfieldEvent {

    public enum InputType {
        KEY_PRESSED,
        KEY_RELEASED,
        MOUSE_DRAG,
        MOUSE_SCROLL
    }

    private final InputType type;

    private final KeyEvent keyEvent;

    private final MouseEvent mouseEvent;

    private final ScrollEvent scrollEvent;

    private StarfieldInputEvent(
            InputType type,
            KeyEvent keyEvent,
            MouseEvent mouseEvent,
            ScrollEvent scrollEvent
    ) {
        this.type = type;
        this.keyEvent = keyEvent;
        this.mouseEvent = mouseEvent;
        this.scrollEvent = scrollEvent;
        log.debug("StarfieldInputEvent created: type={}", type);
    }

    public static StarfieldInputEvent keyPressed(KeyEvent e) {
        log.info("Creating KEY_PRESSED event: key={}", e.getCode());
        return new StarfieldInputEvent(InputType.KEY_PRESSED, e, null, null);
    }

    public static StarfieldInputEvent keyReleased(KeyEvent e) {
        log.info("Creating KEY_RELEASED event: key={}", e.getCode());
        return new StarfieldInputEvent(InputType.KEY_RELEASED, e, null, null);
    }

    public static StarfieldInputEvent mouseDragged(MouseEvent e) {
        log.debug("Creating MOUSE_DRAG event at ({}, {})", e.getX(), e.getY());
        return new StarfieldInputEvent(InputType.MOUSE_DRAG, null, e, null);
    }

    public static StarfieldInputEvent mouseScrolled(ScrollEvent e) {
        log.debug("Creating MOUSE_SCROLL event: deltaY={}", e.getDeltaY());
        return new StarfieldInputEvent(InputType.MOUSE_SCROLL, null, null, e);
    }

}
