package src_;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.util.Map;

/**
 * Custom panel for rendering processes with a modern look.
 */
public class VisualizationPanel extends JPanel {
    private final SimulationModel model;
    private final int circleDiameter = 70;
    private final int margin = 30;
    private final int spacing = 20;
    private final int processesPerRow = 10; // You can adjust for layout

    public VisualizationPanel(SimulationModel model) {
        this.model = model;
        setPreferredSize(new java.awt.Dimension(800, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Use Graphics2D for better rendering.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw a gradient background.
        GradientPaint gp = new GradientPaint(0, 0, new Color(240, 240, 255), 0, getHeight(), Color.WHITE);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Map<Integer, String> states = model.getProcessStates();
        Font defaultFont = g2.getFont();
        Font boldFont = defaultFont.deriveFont(Font.BOLD, 14f);
        g2.setFont(boldFont);
        FontMetrics fm = g2.getFontMetrics();

        int index = 0;
        // Render each process as a circle with dynamic progress (if running) and state labels.
        for (Map.Entry<Integer, String> entry : states.entrySet()) {
            int row = index / processesPerRow;
            int col = index % processesPerRow;
            int x = margin + col * (circleDiameter + spacing);
            int y = margin + row * (circleDiameter + spacing + fm.getHeight() + 10);

            String status = entry.getValue();
            Color baseColor = getColorForStatus(status);
            g2.setColor(baseColor);
            // Fill circle.
            g2.fillOval(x, y, circleDiameter, circleDiameter);

            // If the process is "Running", draw a progress arc.
            if ("Running".equals(status)) {
                int progress = model.getProcessProgress(entry.getKey());
                // Draw progress arc (full circle is 360 degrees).
                int angle = (int) (360 * (progress / 100.0));
                g2.setColor(new Color(0, 0, 0, 120)); // semi-transparent black for contrast
                g2.setStroke(new BasicStroke(8));
                g2.drawArc(x + 5, y + 5, circleDiameter - 10, circleDiameter - 10, 90, -angle);
                // Reset stroke.
                g2.setStroke(new BasicStroke(1));
            }

            // Draw circle outline.
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(x, y, circleDiameter, circleDiameter);

            // Draw the process ID in white at the center.
            String idText = String.valueOf(entry.getKey());
            int textWidth = fm.stringWidth(idText);
            int textX = x + (circleDiameter - textWidth) / 2;
            int textY = y + (circleDiameter + fm.getAscent()) / 2 - 3;
            g2.setColor(Color.WHITE);
            g2.drawString(idText, textX, textY);

            // Draw the status text below the circle.
            String statusText = status;
            int statusTextWidth = fm.stringWidth(statusText);
            int statusX = x + (circleDiameter - statusTextWidth) / 2;
            int statusY = y + circleDiameter + fm.getHeight();
            g2.setColor(Color.BLACK);
            g2.drawString(statusText, statusX, statusY);

            index++;
        }

        // Draw a legend at the bottom.
        drawLegend(g2, fm);
    }

    private Color getColorForStatus(String status) {
        switch (status) {
            case "Waiting":     return new Color(255, 165, 0);  // Orange
            case "Running":     return new Color(76, 175, 80);  // Green
            case "Finished":    return new Color(158, 158, 158); // Grey
            case "Interrupted": return new Color(244, 67, 54);  // Red
            default:            return Color.DARK_GRAY;
        }
    }

    private void drawLegend(Graphics2D g2, FontMetrics fm) {
        int x = margin;
        int y = getHeight() - margin;
        int boxSize = 15;
        int gap = 5;
        int legendSpacing = 130;

        String[] statuses = {"Waiting", "Running", "Finished", "Interrupted"};
        for (String status : statuses) {
            g2.setColor(getColorForStatus(status));
            g2.fillRect(x, y - boxSize, boxSize, boxSize);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y - boxSize, boxSize, boxSize);
            g2.drawString(status, x + boxSize + gap, y - gap);
            x += legendSpacing;
        }
    }
}
