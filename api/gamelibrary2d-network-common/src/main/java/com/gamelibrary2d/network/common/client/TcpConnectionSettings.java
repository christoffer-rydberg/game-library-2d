package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.network.common.NetworkService;

public class TcpConnectionSettings {
    private final String host;
    private final int port;
    private final NetworkService networkService;
    private final boolean owningNetworkService;

    private TcpConnectionSettings(String host, int port, NetworkService networkService, boolean owningNetworkService) {
        this.host = host;
        this.port = port;
        this.networkService = networkService;
        this.owningNetworkService = owningNetworkService;
    }

    public TcpConnectionSettings(String host, int port) {
        this(host, port, new NetworkService(), true);
    }

    public TcpConnectionSettings(String host, int port, NetworkService networkService) {
        this(host, port, networkService, false);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    public boolean isOwningNetworkService() {
        return owningNetworkService;
    }
}
