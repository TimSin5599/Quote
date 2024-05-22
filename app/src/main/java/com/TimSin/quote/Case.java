package com.TimSin.quote;

public class Case {
    private String owner;
    private String status;
    private String text;
    private String key;

    public Case(String owner, String status, String text) {
        this.owner = owner;
        this.status = status;
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public Case(String owner, String status, String text, String key) {
        this.owner = owner;
        this.status = status;
        this.text = text;
        this.key = key;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getOwner() { return owner; }

    public String getText() {
        return text;
    }

    public void setOwner(String owner) { this.owner = owner; }
}
