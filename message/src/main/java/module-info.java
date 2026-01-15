/**
 * Module definition for the Bistro message library.
 * <p>
 * This module provides the communication protocol DTOs and message types
 * used for client-server communication in the Bistro system.
 * </p>
 */
module com.andreibel.message {
    // Required modules
    requires static lombok;

    // Export packages for use by other modules
    exports com.andreibel.message;
    exports com.andreibel.message.DTO;
}