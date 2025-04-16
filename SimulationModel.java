package src_;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds simulation data: process states and progress.
 */
public class SimulationModel {
    // Stores process state (e.g., "Waiting", "Running", "Finished", "Interrupted")
    private final Map<Integer, String> processStates = new HashMap<>();
    // Stores process progress (0 to 100)
    private final Map<Integer, Integer> processProgress = new HashMap<>();
    private final int taskDuration; // in milliseconds
    private Runnable updateListener;

    public SimulationModel(int taskDuration) {
        this.taskDuration = taskDuration;
    }

    public synchronized void updateProcessStatus(int id, String status) {
        processStates.put(id, status);
        // When starting, reset the progress.
        if ("Running".equals(status)) {
            processProgress.put(id, 0);
        }
    }

    public synchronized Map<Integer, String> getProcessStates() {
        return new HashMap<>(processStates);
    }
    
    public int getTaskDuration() {
        return taskDuration;
    }

    public void setUpdateListener(Runnable listener) {
        this.updateListener = listener;
    }

    public void notifyUpdate() {
        if (updateListener != null) {
            updateListener.run();
        }
    }

    public synchronized void updateProcessProgress(int id, int progress) {
        processProgress.put(id, progress);
    }

    public synchronized int getProcessProgress(int id) {
        return processProgress.getOrDefault(id, 0);
    }
}
