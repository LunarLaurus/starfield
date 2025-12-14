package net.laurus.starfield.app.events;

import lombok.Value;
import net.laurus.starfield.model.GridPlane;

@Value
public class ToggleGridPlaneEvent extends StarfieldEvent {

    private final GridPlane plane;

}
