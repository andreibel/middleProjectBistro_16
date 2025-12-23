package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkerNewRequest {
    private String name;
    private String password;
    private String email;
    private boolean isManager;
}
