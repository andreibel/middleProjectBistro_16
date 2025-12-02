package message.DTO;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse implements Serializable {
    private int orderNumber;
    private int numberOfGuests;
    private int conformationCode;
    private int subscriberId; // optional
    private LocalDateTime orderDateTime;
    private LocalDateTime placedOrderDateTime;
}
