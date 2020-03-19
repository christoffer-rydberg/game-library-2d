package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.network.common.CommunicationServer;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;

public final class DefaultClientSideCommunicator extends AbstractClientSideCommunicator {

    private ParameterizedAction<CommunicationInitializer> configureAuthentication;

    public DefaultClientSideCommunicator(TcpConnectionSettings tcpSettings) {
        super(tcpSettings);
    }

    public DefaultClientSideCommunicator(TcpConnectionSettings tcpSettings, CommunicationServer communicationServer) {
        super(tcpSettings, communicationServer);
    }

    public void onConfigureAuthentication(ParameterizedAction<CommunicationInitializer> action) {
        this.configureAuthentication = action;
    }

    @Override
    public void configureAuthentication(CommunicationInitializer initializer) {
        if (configureAuthentication != null) {
            configureAuthentication.invoke(initializer);
        }
    }
}
