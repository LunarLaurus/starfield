package net.laurus.starmapper.ui.panel;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.laurus.starmapper.model.Star;

@Slf4j
@Getter
public class StarControlPanel extends JPanel {

    private final JSlider zoomSlider;

    private final JCheckBox rotateCheck;

    private final JSlider rotationSpeedSlider;

    private final JTextField searchField;

    private final JSlider distanceFilterSlider;

    private final JButton zoomToFitButton;

    private final JButton centerStarButton;

    private boolean rotationEnabled = false;

    public StarControlPanel(List<Star> stars, StarMapPanel starMapPanel, Timer rotationTimer) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // ------------------------------ ZOOM ------------------------------
        zoomSlider = new JSlider(1, 500, 100);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(true);
        zoomSlider.setMajorTickSpacing(100);
        zoomSlider.setMinorTickSpacing(10);
        zoomSlider.addChangeListener(e -> {
            starMapPanel.getCamera().setZoom(zoomSlider.getValue() / 100.0);
            starMapPanel.repaint();
        });
        add(new JLabel("Zoom"));
        add(zoomSlider);

        // ------------------------------ ROTATION ------------------------------
        rotateCheck = new JCheckBox("Enable Rotation");
        add(rotateCheck);

        rotationSpeedSlider = new JSlider(1, 50, 2);
        rotationSpeedSlider.setPaintTicks(true);
        rotationSpeedSlider.setPaintLabels(true);
        rotationSpeedSlider.setMajorTickSpacing(10);
        rotationSpeedSlider.setMinorTickSpacing(1);
        add(new JLabel("Rotation Speed"));
        add(rotationSpeedSlider);

        rotateCheck.addActionListener(e -> {
            rotationEnabled = rotateCheck.isSelected();
            log.debug("Rotation enabled: {}", rotationEnabled);
        });

        // Rotation timer: rotates camera automatically if enabled
        rotationTimer.addActionListener(e -> {

            if (rotationEnabled) {
                double speed = rotationSpeedSlider.getValue() * 0.01;
                starMapPanel.getInputHandler().getRotationHandler().incrementRotation(speed, speed);
                starMapPanel.repaint();
            }

        });

        // ------------------------------ SEARCH ------------------------------
        searchField = new JTextField();
        searchField.setMinimumSize(new Dimension(150, 30));
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        add(new JLabel("Search Star:"));
        add(searchField);

        searchField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }

            private void search() {
                String text = searchField.getText().trim();
                if (text.isEmpty())
                    return;

                stars
                        .stream()
                        .filter(s -> s.getName().equalsIgnoreCase(text))
                        .findFirst()
                        .ifPresent(star ->
                        {
                            starMapPanel.setSelectedStar(star);
                            starMapPanel.getCamera().setFocusX(star.getX());
                            starMapPanel.getCamera().setFocusY(star.getY());
                            starMapPanel.getCamera().setFocusZ(star.getZ());
                        });
            }

        });

        // ------------------------------ DISTANCE FILTER ------------------------------
        distanceFilterSlider = new JSlider(10, 1000, 1000);
        distanceFilterSlider.setPaintTicks(true);
        distanceFilterSlider.setPaintLabels(true);
        distanceFilterSlider.setMajorTickSpacing(200);
        distanceFilterSlider.setMinorTickSpacing(50);
        distanceFilterSlider
                .addChangeListener(
                        e -> starMapPanel.setDistanceFilter(distanceFilterSlider.getValue())
                );
        add(new JLabel("Distance Filter"));
        add(distanceFilterSlider);

        // ------------------------------ ZOOM TO FIT ------------------------------
        zoomToFitButton = new JButton("Zoom to Fit Cluster");
        zoomToFitButton
                .addActionListener(
                        e -> starMapPanel.zoomToFitSelected(distanceFilterSlider.getValue())
                );
        add(zoomToFitButton);

        // ------------------------------ CENTER & ZOOM ------------------------------
        centerStarButton = new JButton("Center & Zoom Selected");
        centerStarButton.addActionListener(e -> {
            Star sel = starMapPanel.getSelectedStar();

            if (sel != null) {
                starMapPanel.getCamera().setFocusX(sel.getX());
                starMapPanel.getCamera().setFocusY(sel.getY());
                starMapPanel.getCamera().setFocusZ(sel.getZ());
                starMapPanel.zoomToFitSelected(distanceFilterSlider.getValue());
            }

        });
        add(centerStarButton);

        // ------------------------------ RESET PANEL ------------------------------
        JPanel resetPanel = new JPanel();
        resetPanel.setLayout(new BoxLayout(resetPanel, BoxLayout.Y_AXIS));
        resetPanel.setBorder(BorderFactory.createTitledBorder("Reset"));

        JButton resetPanBtn = new JButton("Reset Pan");
        resetPanBtn.addActionListener(e -> {
            starMapPanel.getCamera().setPanX(0);
            starMapPanel.getCamera().setPanY(0);
            starMapPanel.repaint();
        });
        resetPanel.add(resetPanBtn);

        JButton resetZoomBtn = new JButton("Reset Zoom");
        resetZoomBtn.addActionListener(e -> {
            starMapPanel.getCamera().setZoom(1.0);
            zoomSlider.setValue(100);
            starMapPanel.repaint();
        });
        resetPanel.add(resetZoomBtn);

        JButton resetRotationBtn = new JButton("Reset Rotation");
        resetRotationBtn.addActionListener(e -> { starMapPanel.resetRotation(); });
        resetPanel.add(resetRotationBtn);

        JButton resetDistanceFilterBtn = new JButton("Reset Distance Filter");
        resetDistanceFilterBtn.addActionListener(e -> {
            distanceFilterSlider.setValue(distanceFilterSlider.getMaximum());
            starMapPanel.setDistanceFilter(distanceFilterSlider.getValue());
        });
        resetPanel.add(resetDistanceFilterBtn);

        add(resetPanel);
    }

}
