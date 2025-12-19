package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SubscriberResponse {
    private int subscriberId;
    private String email;
    private String name;
    private String phoneNumber;
}
