package com.andreibel.client;
import OCSF.src.ocsf.client.*;


public class BistroClient {
    ClientMainForm clientMainForm;
    public static boolean awaitResposne;

    public BistroClient(String host, int port, ClientMainForm clientMainForm) {
        //super(host, port);
        this.clientMainForm = clientMainForm;
    }


}
