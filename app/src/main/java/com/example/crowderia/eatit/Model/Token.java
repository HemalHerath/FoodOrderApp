package com.example.crowderia.eatit.Model;

/**
 * Created by crowderia on 2/6/2018.
 */

public class Token {

    private String token;
    private boolean isServiceToken;

    public Token () {}

    public Token(String token, boolean isServiceToken) {
        this.token = token;
        this.isServiceToken = isServiceToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServiceToken() {
        return isServiceToken;
    }

    public void setServiceToken(boolean serviceToken) {
        isServiceToken = serviceToken;
    }
}
