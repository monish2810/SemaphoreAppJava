package src_;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

/**
 * Main application entry point.
 */
public class SemaphoreSimulationApp {
    JFrame frame;
    VisualizationPanel visPanel;
    SimulationModel model;
    
    // Default simulation settings.
    private int initialResourceCount = 3;
    private int taskDuration = 2000; // in milliseconds
    private int defaultProcessCount = 10;

    public SemaphoreSimulationApp() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Enhanced Semaphore Synchronization Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 750);
        frame.setLayout(new BorderLayout());

        // Initialize simulation model and visualization panel.
        model = new SimulationModel(taskDuration);
        visPanel = new VisualizationPanel(model);
        model.setUpdateListener(() -> visPanel.repaint());

        // Create a top control panel.
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel resourceLabel = new JLabel("Resource Count:");
        JTextField resourceField = new JTextField(String.valueOf(initialResourceCount), 3);
        
        JLabel processLabel = new JLabel("Number of Processes:");
        JTextField processCountField = new JTextField(String.valueOf(defaultProcessCount), 3);

        JButton startSimButton = new JButton("Start Simulation");

        controlPanel.add(resourceLabel);
        controlPanel.add(resourceField);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(processLabel);
        controlPanel.add(processCountField);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(startSimButton);
        
        // Create an explanation panel.
        JTextArea explanationArea = new JTextArea(5, 80);
        explanationArea.setEditable(false);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setText(
            "Explanation:\n" +
            "- Each circle represents a process. The number inside is its ID.\n" +
            "- Progress arc in a running process shows task completion percentage.\n" +
            "- 'Waiting': The process is queued for a resource.\n" +
            "- 'Running': The process has acquired a resource and is in progress.\n" +
            "- 'Finished': The process has completed its task and released the resource.\n" +
            "- 'Interrupted': The process was interrupted during execution.\n" +
            "A semaphore controls concurrent access, limiting the number of processes running concurrently."
        );
        JScrollPane explanationScroll = new JScrollPane(explanationArea);
        explanationScroll.setBorder(BorderFactory.createTitledBorder("Simulation Explanation"));

        // Lower panel holds the control panel and the explanation.
        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.add(controlPanel, BorderLayout.NORTH);
        lowerPanel.add(explanationScroll, BorderLayout.CENTER);

        // Add the visualization panel and lower panel to the frame.
        frame.add(visPanel, BorderLayout.CENTER);
        frame.add(lowerPanel, BorderLayout.SOUTH);

        // Action handler for the Start Simulation button.
        startSimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int resourceCount, processCount;
                try {
                    resourceCount = Integer.parseInt(resourceField.getText());
                    processCount = Integer.parseInt(processCountField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numeric values.");
                    return;
                }

                // Reset model and visualization.
                model = new SimulationModel(taskDuration);
                visPanel = new VisualizationPanel(model);
                frame.getContentPane().removeAll();
                frame.add(visPanel, BorderLayout.CENTER);
                frame.add(lowerPanel, BorderLayout.SOUTH);
                frame.revalidate();
                frame.repaint();
                model.setUpdateListener(() -> visPanel.repaint());

                // Create a fair semaphore.
                Semaphore semaphore = new Semaphore(resourceCount, true);

                // Launch user-defined number of processes.
                for (int i = 0; i < processCount; i++) {
                    Thread t = new Thread(new ProcessTask(i, semaphore, model));
                    t.start();
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SemaphoreSimulationApp app = new SemaphoreSimulationApp();
            app.frame.setVisible(true);
        });
    }
}
