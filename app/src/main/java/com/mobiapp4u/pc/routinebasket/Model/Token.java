package com.mobiapp4u.pc.routinebasket.Model;

public class Token {
    private String token;
    private String serverToken;

    public Token() {
    }

    public Token(String token, String serverToken) {
        this.token = token;
        this.serverToken = serverToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getServerToken() {
        return serverToken;
    }

    public void setServerToken(String servertoken) {
        serverToken = servertoken;
    }
}

