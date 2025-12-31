package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WaitingListResponse implements Serializable {
    private int waitingNumber;
    private Integer numberOfGuests;
    private LocalDateTime waitingDateTime;
    private boolean isCurrentlyWaiting;
    private UUID conformationCode;
    private Integer orderNumber;
    private Integer subscriberId; // optional
    private String email; // optional
    private String phoneNumber; // optional
}
