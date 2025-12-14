package net.laurus.starfield.app.ui.handler;

import java.util.List;

import net.laurus.starfield.app.ui.component.Camera3D;
import net.laurus.starfield.model.Star;

/**
 * Determines which star is currently under the mouse cursor.
 */
public class StarSelectionService {

    private final Camera3D camera;

    public StarSelectionService(Camera3D camera) {
        this.camera = camera;
    }

    public Star findHovered(
            double mouseX,
            double mouseY,
            List<Star> stars,
            double width,
            double height
    ) {
        return stars.stream().filter(s -> {
            double[] p = camera.project(s.getX(), s.getY(), s.getZ(), width, height);
            double dx = p[0] - mouseX;
            double dy = p[1] - mouseY;
            return Math.hypot(dx, dy) < 10;
        }).findFirst().orElse(null);
    }

}
