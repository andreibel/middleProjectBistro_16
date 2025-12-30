package com.andreibel.client.util;

import lombok.Getter;
import lombok.Setter;

/**
 * Singleton class that manages the global worker state for the client application.
 *
 * <p>This class provides a central location to store and share worker-related
 * information across different GUI controllers (FXML forms) without passing
 * data explicitly between screens.</p>
 *
 * <p>The main responsibilities of this class include:</p>
 * <ul>
 *     <li>Storing the worker's name</li>
 *     <li>Tracking whether the worker is a manager</li>
 *     <li>Providing a single shared instance accessible via {@link #getInstance()}</li>
 * </ul>
 *
 * <p>This class does not perform any server communication; it purely stores
 * client-side state.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * WorkerStateManager state = WorkerStateManager.getInstance();
 * state.setWorkerName("John Doe");
 * if (state.isManager()) {
 *     // Show manager-only controls
 * }
 * </pre>
 */
@Getter
@Setter
public class WorkerStateManager {

    /**
     * The single instance of {@link WorkerStateManager}.
     */
    private static WorkerStateManager instance;

    /**
     * The name of the currently logged-in worker.
     */
    private String workerName;

    /**
     * Whether the currently logged-in worker is a manager.
     */
    private boolean isManager;

    /**
     * Returns the singleton instance of {@link WorkerStateManager}.
     *
     * <p>If the instance does not exist yet, it is created.</p>
     *
     * @return the shared WorkerStateManager instance
     */
    public static WorkerStateManager getInstance() {
        if (instance == null) {
            instance = new WorkerStateManager();
        }
        return instance;
    }

    /**
     * Checks if a worker has logged in.
     *
     * @return {@code true} if a worker has logged in, {@code false} otherwise
     */
    public static boolean hasWorkerLoggedIn() {
        return instance != null;
    }
}
