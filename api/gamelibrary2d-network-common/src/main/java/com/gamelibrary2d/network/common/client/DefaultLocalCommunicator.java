package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.functional.ParameterizedAction;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.server.LocalServer;

public class DefaultLocalCommunicator extends AbstractLocalCommunicator {

    private ParameterizedAction<CommunicationSteps> configureAuthentication;

    public DefaultLocalCommunicator(LocalServer localServer) {
        super(localServer);
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
