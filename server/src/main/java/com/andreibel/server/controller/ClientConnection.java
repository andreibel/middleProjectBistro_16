package com.andreibel.server.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.net.InetAddress;

@Data
@AllArgsConstructor
public class ClientConnection implements Serializable {
    public long id;
    public InetAddress ipAddress;
    public String status;

}
