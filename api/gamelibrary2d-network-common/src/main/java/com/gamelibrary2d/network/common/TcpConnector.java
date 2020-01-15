package com.gamelibrary2d.network.common;

import com.gamelibrary2d.network.common.client.TcpConnectionSettings;

public interface TcpConnector {

    TcpConnectionSettings getTcpConnectionSettings();

    void setTcpConnectionSettings(TcpConnectionSettings tcpSettings);

}
