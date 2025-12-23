package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SubscriberRequest implements Serializable {
    private Integer subscriberId;
    private String email;
    private String name;
    private String phoneNumber;
}
