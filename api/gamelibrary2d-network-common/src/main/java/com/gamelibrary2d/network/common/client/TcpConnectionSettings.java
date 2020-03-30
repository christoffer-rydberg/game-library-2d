package com.gamelibrary2d.network.common.client;

public class TcpConnectionSettings {
    private final String host;
    private final int port;

    public TcpConnectionSettings(String host, int port) {
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
