package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;

    public Window() {
        JLabel keyDescriptionLabel = new JLabel(
                "<html>" +
                        "<style>" +
                        "strong { color: #004C99;}" +
                        "p { color: #202020; font-size: 11px; margin: 4px 0;}" +
                        "</style>" +
                        "<div style='width: 180px; padding: 5px 10px;'>" +
                        "<p>Right click to SeedFill <br> default mode: Polygon</p>" +
                        "<p><strong>[Shift]</strong> " +
                        "<p><b>In Line mode:</b> draws a horizontal, vertical, or diagonal line.</p>" +
                        "<p><b>In Rectangle mode:</b> draws a square.</p>" +
                        "<p><b>In Ellipse mode:</b> draws a circle.</p>" +
                        "<p><strong>[Control]</strong> " +
                        "<p><b>In Cut Mode:</b> Changes a Cut Shape.</p>" +
                        "<p><strong>[C]</strong> Delete Canvas and all textures.</p>" +
                        "<p><strong>[L]</strong> Line mode</p>" +
                        "<p><strong>[P]</strong> Polygon mode.</p>" +
                        "<p><strong>[T]</strong> Triangle mode.</p>" +
                        "<p><strong>[R]</strong> Rectangle mode.</p>" +
                        "<p><strong>[E]</strong> Ellipse mode.</p>" +
                        "<p><strong>[W]</strong> Cut mode</p>" +
                        "<p style = 'margin: 10px 0 5px;'><strong style = 'color: #990000;'>[F]</strong> Enable SeedFiller.</p>" +
                        "<p><strong style = 'color: #990000;'>[S]</strong> Enable ScanLine.</p>" +
                        "</div>"
        );
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.add(keyDescriptionLabel);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("UHK FIM PGRF : " + this.getClass().getName());


        panel = new Panel();
        panel.add(descriptionPanel);
        add(descriptionPanel, BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
        pack();

        setLocationRelativeTo(null);

        // lepší až na konci, aby to neukradla nějaká komponenta v případně složitějším UI
        panel.setFocusable(true);
        panel.grabFocus(); // důležité pro pozdější ovládání z klávesnice
    }

    public Panel getPanel() {
        return panel;
    }

}
