package com.andreibel.server.controller;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class Serve extends AbstractServer {
    /**
     * Constructs a new server.
     *
     * @param port the port number on which to listen.
     */
    public Serve(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {

    }
}
