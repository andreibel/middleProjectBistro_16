package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest implements Serializable {
    private UUID ConformationCode;
    private int numberOfGuests;
    private LocalDateTime orderDateTime;
    private Integer subscriberId; // optional
    private String email; // optional
    private String phoneNumber; // optional

}
