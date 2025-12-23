package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * WorkerResponse DTO for transferring worker data from server to client.
 * Used for sending worker information in API responses.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkerResponse implements Serializable {
    private String workerName;
    private boolean isManager;
}
