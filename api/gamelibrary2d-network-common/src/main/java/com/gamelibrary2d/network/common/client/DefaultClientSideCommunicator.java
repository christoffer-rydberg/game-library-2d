package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.network.common.CommunicationServer;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

public final class DefaultClientSideCommunicator extends AbstractClientSideCommunicator {

    private ParameterizedAction<CommunicationSteps> configureAuthentication;

    public DefaultClientSideCommunicator(TcpConnectionSettings tcpSettings) {
        super(tcpSettings);
    }

    public DefaultClientSideCommunicator(TcpConnectionSettings tcpSettings, CommunicationServer communicationServer) {
        super(tcpSettings, communicationServer);
    }

    public void onConfigureAuthentication(ParameterizedAction<CommunicationSteps> action) {
        this.configureAuthentication = action;
    }

    @Override
    public void configureAuthentication(CommunicationSteps steps) {
        if (configureAuthentication != null) {
            configureAuthentication.invoke(steps);
        }
    }
}
