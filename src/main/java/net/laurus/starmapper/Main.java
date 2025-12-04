package net.laurus.starmapper;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import lombok.extern.slf4j.Slf4j;
import net.laurus.starmapper.model.Star;
import net.laurus.starmapper.ui.frame.StarMapperFrame;
import net.laurus.starmapper.ui.panel.StarMapPanel;
import net.laurus.starmapper.util.StarLoader;

@Slf4j
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            log.info("Starting Star Mapper UI");

            // Load stars
            List<Star> stars = StarLoader.loadStars();
            log.info("Loaded {} stars", stars.size());

            // Create frame
            StarMapperFrame frame = new StarMapperFrame(stars);
            frame.setVisible(true);

            // Access the main StarMapPanel
            StarMapPanel mapPanel = frame.getStarMapPanel();

            // Create update loop (~60 FPS)
            Timer updateTimer = new Timer(16, e -> {
                // deltaSeconds fixed at 1/60
                mapPanel.update(0.016);
            });
            updateTimer.start();

            log.info("Star Mapper UI ready with update loop running");
        });
    }

}
