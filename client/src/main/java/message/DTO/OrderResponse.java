package message.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse  implements Serializable {
    private int orderNumber;
    private int numberOfGuests;
    private int conformationCode;
    private int subscriberId; // optional
    private LocalDateTime orderDateTime;
    private LocalDateTime placedOrderDateTime;

}
