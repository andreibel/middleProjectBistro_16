package com.andreibel.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Generic message wrapper class for client-server communication in the Bistro system.
 * <p>
 * This class serves as the main communication protocol between clients and the server,
 * encapsulating the API call type and associated data payload.
 * </p>
 *
 * @author Bistro Team
 * @version 1.0
 * @see APICallType
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    /**
     * The type of API call being made.
     * Determines how the server should process the message.
     */
    private APICallType type;

    /**
     * The data payload associated with this message.
     * The actual type depends on the {@link APICallType} specified.
     */
    private Object data;

}
