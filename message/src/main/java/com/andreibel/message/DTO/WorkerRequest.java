package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WorkerRequest DTO for transferring worker data from client to server.
 * Used for creating and updating worker information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkerRequest {
    private String workerName;
    private String workerPassword;
    private String workerEmail;
    private boolean isManager;
}
