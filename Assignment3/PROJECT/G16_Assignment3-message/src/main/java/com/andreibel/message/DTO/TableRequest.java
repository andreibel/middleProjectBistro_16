package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for table configuration requests.
 * <p>
 * Used to configure the restaurant's table layout and capacity.
 * Tables are grouped by capacity with a specified quantity for each group.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see TableResponse
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TableRequest implements Serializable {

    /**
     * Seating capacity of this table type.
     * Represents the maximum number of guests the table can accommodate.
     */
    private Integer capacity;

    /**
     * Number of tables with this capacity.
     */
    private Integer quantity;
}
