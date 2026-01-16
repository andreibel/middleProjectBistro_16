package com.andreibel.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for table configuration responses.
 * <p>
 * Used to return restaurant table layout information from the server.
 * Tables are grouped by capacity with the count for each group.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see TableRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableResponse implements Serializable {

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
