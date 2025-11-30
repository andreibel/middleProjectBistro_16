package com.andreibel.server.dbController;


// singleton connector
public class JDBCConnector {

    private static JDBCConnector instance;
    private JDBCConnector(){}
    public static JDBCConnector getInstance(){
        if(instance == null){
            instance = new JDBCConnector();
        }
        return instance;
    }
}
