package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for creating new worker accounts.
 * <p>
 * Used to register new workers (employees) in the Bistro system.
 * Contains authentication credentials and role information.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see WorkerAuth
 * @see WorkerResponse
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkerNewRequest implements Serializable {

    /**
     * Username/name of the new worker.
     * Used for identification and login purposes.
     */
    private String name;

    /**
     * Password for the new worker account.
     * Should be stored securely on the server side.
     */
    private String password;

    /**
     * Indicates whether the worker has manager privileges.
     * {@code true} for managers with elevated access rights,
     * {@code false} for regular staff members.
     */
    private boolean isManager;
}
