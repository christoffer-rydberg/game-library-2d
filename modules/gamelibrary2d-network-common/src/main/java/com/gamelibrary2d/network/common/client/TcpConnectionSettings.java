package com.gamelibrary2d.network.common.client;

public class TcpConnectionSettings {

    private final String host;
    private final int port;
    private final boolean ssl;

    public TcpConnectionSettings(String host, int port, boolean ssl) {
        this.host = host;
        this.port = port;
        this.ssl = ssl;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isSsl() {
        return ssl;
    }
}
