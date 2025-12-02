package com.andreibel.client;
import ocsf.client.AbstractClient;


public class BistroClient extends AbstractClient {
    ClientMainForm clientMainForm;
    public static boolean awaitResposne;

    public BistroClient(String host, int port, ClientMainForm clientMainForm) {
        //super(host, port);
        this.clientMainForm = clientMainForm;
    }

}
