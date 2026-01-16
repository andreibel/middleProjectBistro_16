package com.andreibel.server.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Simple data model that represents a client connection entry in the server GUI.
 *
 * <p>This class is used as the row type for the JavaFX {@code TableView} in
 * {@link BistroServerGUIController}. Each instance holds:</p>
 * <ul>
 *   <li>a unique connection id (usually taken from {@code ConnectionToClient#getId()})</li>
 *   <li>the client {@link InetAddress}</li>
 *   <li>a human-readable status string (e.g., {@code "Open"}, {@code "closed"})</li>
 * </ul>
 *
 * <p>The class implements {@link Serializable} to allow future use in messaging/logging or
 * persistence if needed (for example, sending connection snapshots to another component).</p>
 *
 * <p>Lombok annotations generate boilerplate:
 * {@link Data} creates getters/setters, {@code equals/hashCode}, and {@code toString};
 * {@link AllArgsConstructor} creates a constructor with all fields.</p>
 *
 * @author Andrei Beloziyorove
 */
@Data
@AllArgsConstructor
public class ClientConnection implements Serializable {

    /** Connection identifier (unique per connected client instance). */
    public long id;

    /** Client IP address as resolved by the server connection layer. */
    public InetAddress ipAddress;

    /** Connection status displayed in the GUI (e.g., "Open", "closed"). */
    public String status;
}