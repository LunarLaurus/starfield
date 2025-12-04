package net.laurus.starmapper.ui.frame;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import lombok.Getter;
import net.laurus.starmapper.model.Star;
import net.laurus.starmapper.ui.panel.StarControlPanel;
import net.laurus.starmapper.ui.panel.StarMapPanel;

@Getter
public class StarMapperFrame extends JFrame {

    private final StarMapPanel starMapPanel;

    private final Timer rotationTimer = new Timer(33, null);

    public StarMapperFrame(List<Star> stars) {
        super("Star Mapper");

        starMapPanel = new StarMapPanel(stars);

        // Default focus = Sol
        stars.stream().filter(s -> s.getId() == 0).findFirst().ifPresent(sol -> {
            starMapPanel.setSelectedStar(sol);
            starMapPanel.getCamera().setFocusX(sol.getX());
            starMapPanel.getCamera().setFocusY(sol.getY());
            starMapPanel.getCamera().setFocusZ(sol.getZ());
        });

        setLayout(new BorderLayout());
        add(starMapPanel, BorderLayout.CENTER);

        StarControlPanel controlPanel = new StarControlPanel(stars, starMapPanel, rotationTimer);
        add(controlPanel, BorderLayout.EAST);

        rotationTimer.start();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
    }

}
