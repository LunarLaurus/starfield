package net.laurus.starmapper.ui.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ToolTipManager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starmapper.ui.panel.StarMapPanel;
import net.laurus.starmapper.ui.render.StarRenderer;

/**
 * Main input handler: coordinates rotation, pan, zoom, selection, hover.
 */
@Slf4j
@Getter
public class StarInputHandler {

    private final StarMapPanel panel;

    private final StarRenderer renderer;

    private final RotationHandler rotationHandler;

    private final PanHandler panHandler;

    private final ZoomHandler zoomHandler;

    private final SelectionHandler selectionHandler;

    private final HoverHandler hoverHandler;

    public StarInputHandler(StarMapPanel panel, StarRenderer renderer) {
        if (panel == null || renderer == null)
            throw new IllegalArgumentException("Panel and renderer must not be null");

        this.panel = panel;
        this.renderer = renderer;

        ToolTipManager.sharedInstance().registerComponent(panel);

        rotationHandler = new RotationHandler(this);
        panHandler = new PanHandler(this);
        zoomHandler = new ZoomHandler(this);
        selectionHandler = new SelectionHandler(this);
        hoverHandler = new HoverHandler(this);

        setupInput();
    }

    private void setupInput() {
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                panHandler.onMousePressed(e);
                rotationHandler.onMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                panHandler.onMouseReleased(e);
                rotationHandler.onMouseReleased(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectionHandler.onMouseClicked(e);
            }

        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                rotationHandler.onMouseDragged(e);
                panHandler.onMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                hoverHandler.onMouseMoved(e);
            }

        });

        panel.addMouseWheelListener(zoomHandler::onMouseWheelMoved);
    }

    public void resetRotation() {
        rotationHandler.resetRotation();
    }

}
