package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WaitingListRequest implements Serializable {
    private UUID ConformationCode;
    private Integer numberOfGuests;
    private Integer orderNumber;
    private Integer subscriberId; // optional
    private String email; // optional
    private String phoneNumber; // optional
}
