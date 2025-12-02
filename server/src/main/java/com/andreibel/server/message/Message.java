package com.andreibel.server.message;

import java.io.Serializable;

public class Message implements Serializable {
    private APICallType type;
    private Object data;

    public Message(APICallType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public APICallType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}