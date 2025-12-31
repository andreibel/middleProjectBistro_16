package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubscriberReportResponse implements Serializable {
    private Map<LocalDate, Integer> SubscriberOrdersCount;
    private Map<LocalDate, Integer> SubscriberWaitingListCount;
}
