package com.darkkeks;

public class UserProxy {

    private String host;
    private int port;

    public UserProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
