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

    /** Target FPS for update loop */
    private static final int TARGET_FPS = 60;

    private static final double FRAME_TIME_SEC = 1.0 / TARGET_FPS;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            log.info("Starting Star Mapper UI");

            // Load stars -----------------------------------------------------
            List<Star> stars = StarLoader.loadStars();
            log.info("Loaded {} stars", stars.size());

            // Create UI frame ------------------------------------------------
            StarMapperFrame frame = new StarMapperFrame(stars);
            frame.setVisible(true);

            // Obtain main rendering panel
            StarMapPanel mapPanel = frame.getStarMapPanel();

            // ----------------------------------------------------------------
            // Create animation/update loop using Swing's Timer (runs on EDT)
            // ----------------------------------------------------------------
            int delayMs = (int) (FRAME_TIME_SEC * 1000);

            Timer updateLoop = new Timer(delayMs, event -> {
                mapPanel.update(FRAME_TIME_SEC);
                // repaint is triggered inside update() when necessary
            });

            updateLoop.setRepeats(true);
            updateLoop.start();

            log.info("Star Mapper UI ready — update loop running at ~{} FPS", TARGET_FPS);
        });
    }

}
