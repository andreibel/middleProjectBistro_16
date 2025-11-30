package com.andreibel.server.dbController;


// singleton connector
public class JDBCConnector {

    private static JDBCConnector instance;
    private String url;
    private String username;
    private String password;
    private JDBCConnector(){
        url = "jdbc:mysql://localhost/placeholder";
        username = "placeholder";
        password = "placeholder";
    }
    public static JDBCConnector getInstance(){
        if(instance == null){
            instance = new JDBCConnector();
        }
        return instance;
    }
}
