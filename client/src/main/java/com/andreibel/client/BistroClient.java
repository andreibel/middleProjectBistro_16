package com.andreibel.client;



public class BistroClient {
    ClientMainForm clientMainForm;
    public static boolean awaitResposne;

    public BistroClient(String host, int port, ClientMainForm clientMainForm) {
        //super(host, port);
        this.clientMainForm = clientMainForm;
    }


}
