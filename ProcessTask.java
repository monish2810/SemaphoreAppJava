
package src_;

import java.util.concurrent.Semaphore;
import javax.swing.SwingUtilities;

/**
 * Represents a simulated process that updates its state and progress.
 */
public class ProcessTask implements Runnable {
    private final int id;
    private final Semaphore semaphore;
    private final SimulationModel model;

    public ProcessTask(int id, Semaphore semaphore, SimulationModel model) {
        this.id = id;
        this.semaphore = semaphore;
        this.model = model;
    }

    @Override
    public void run() {
        try {
            // Process starts in "Waiting" state.
            model.updateProcessStatus(id, "Waiting");
            SwingUtilities.invokeLater(model::notifyUpdate);

            // Try to acquire a resource.
            semaphore.acquire();

            // Resource acquired: change state to "Running".
            model.updateProcessStatus(id, "Running");
            SwingUtilities.invokeLater(model::notifyUpdate);

            // Simulate work with smooth progress updates.
            int increments = 100; // Divide the task into 100 steps.
            int sleepTime = model.getTaskDuration() / increments;
            for (int i = 0; i < increments; i++) {
                Thread.sleep(sleepTime);
                // Update progress (0 to 100 percent).
                model.updateProcessProgress(id, i + 1);
                SwingUtilities.invokeLater(model::notifyUpdate);
            }

            // After work is done, release resource.
            semaphore.release();
            // Mark as Finished.
            model.updateProcessStatus(id, "Finished");
            SwingUtilities.invokeLater(model::notifyUpdate);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            model.updateProcessStatus(id, "Interrupted");
            SwingUtilities.invokeLater(model::notifyUpdate);
        }
    }
}
