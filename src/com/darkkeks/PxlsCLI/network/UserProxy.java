package com.darkkeks.PxlsCLI.network;

public class UserProxy {

    private final String host;
    private final int port;

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

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
