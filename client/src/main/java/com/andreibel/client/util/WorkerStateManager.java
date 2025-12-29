package com.andreibel.client.util;

import lombok.Getter;
import lombok.Setter;

/**
 * Manages the application-wide state of the currently logged-in worker.
 *
 * <p>This class follows the <b>Singleton</b> design pattern and provides a
 * centralized place to store and access worker-related session information,
 * such as the worker's name and managerial privileges.</p>
 *
 * <p>The state stored in this manager represents the worker who is currently
 * authenticated in the client application and should be accessed via
 * {@link #getInstance()}.</p>
 */
@Getter
@Setter
public class WorkerStateManager {

    /**
     * The full name of the currently logged-in worker.
     */
    private String workerName;

    /**
     * Indicates whether the current worker has manager privileges.
     * {@code true} if the worker is a manager; {@code false} otherwise.
     */
    private boolean isManager;

    /**
     * The singleton instance of {@code WorkerStateManager}.
     */
    private static WorkerStateManager instance;

    /**
     * Returns the singleton instance of {@code WorkerStateManager}.
     * <p>
     * If the instance does not yet exist, it is created lazily.
     * </p>
     *
     * @return the singleton {@code WorkerStateManager} instance
     */
    public static WorkerStateManager getInstance() {
        if (instance == null) {
            instance = new WorkerStateManager();
        }
        return instance;
    }
}
