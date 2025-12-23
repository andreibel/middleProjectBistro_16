package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubscriberResponse implements Serializable {
    private int subscriberId;
    private String email;
    private String name;
    private String phoneNumber;
    private List<OrderResponse> orders;
}
