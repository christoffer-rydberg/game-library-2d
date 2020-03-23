package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.common.io.Write;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.AbstractClientSideCommunicator;
import com.gamelibrary2d.network.common.client.TcpConnectionSettings;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.nio.charset.StandardCharsets;

public class DemoCommunicator extends AbstractClientSideCommunicator {

    public DemoCommunicator(TcpConnectionSettings tcpSettings) {
        super(tcpSettings);
    }
    
    @Override
    public void configureAuthentication(CommunicationSteps steps) {
        steps.add(this::authenticate);
    }

    private void authenticate(Communicator communicator) {
        var outgoing = communicator.getOutgoing();
        Write.textWithSizeHeader("serverPassword123", StandardCharsets.UTF_8, outgoing);
    }
}
