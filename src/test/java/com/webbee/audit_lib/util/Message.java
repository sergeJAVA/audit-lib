package com.webbee.audit_lib.util;

public class Message {
    private String message;

    public Message(String id) {
        this.message = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
