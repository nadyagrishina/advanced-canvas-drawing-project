package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;

    public Window() {
        JLabel keyDescriptionLabel = new JLabel(
                "<html>" +
                        "<div style='width: 180px; padding: 5px 10px;'>" +
                        "<p>Right click to Fill <br> default mode: Polygon</p>" +
                        "<br>" +
                        "<p><strong>[Shift]</strong> In Line mode, draw a horizontal, vertical, or diagonal line.</p>" +
                        "<br>" +
                        "<p><strong>[C]</strong> Delete Canvas and all textures.</p>" +
                        "<br>" +
                        "<p><strong>[L]</strong> Line mode</p>" +
                        "<br>" +
                        "<p><strong>[P]</strong> Polygon mode.</p>" +
                        "<br>" +
                        "<p><strong>[T]</strong> Triangle mode.</p>" +
                        "<br>" +
                        "<p><strong>[R]</strong> Rectangle mode.</p>" +
                        "<br>" +
                        "<p><strong>[E]</strong> Ellipse mode.</p>" +
                        "<br>" + "<br>" + "<br>" + "<br>" +
                        "<p><strong>[F]</strong> Enable SeedFiller.</p>" +
                        "<br>" +
                        "<p><strong>[S]</strong> Enable ScanLine.</p>" +
                        "<br>" +
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
