package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.UdpReceiver;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

import java.io.IOException;

public class UdpReceiverInitialization implements ProducerStep {

    private int localPort;

    public UdpReceiverInitialization(int localPort) {
        this.localPort = localPort;
    }

    @Override
    public void run(Communicator communicator) throws InitializationException {
        var buffer = communicator.getOutgoing();
        if (communicator instanceof UdpReceiver) {
            var udpConnector = (UdpReceiver) communicator;
            try {
                udpConnector.connectUdpReceiver(localPort);
            } catch (IOException e) {
                throw new InitializationException(
                        String.format("Failed to connect UDP receiver to localPort %s", localPort), e);
            }
            buffer.putBool(true);
            buffer.putInt(localPort);
        } else {
            buffer.putBool(false);
        }
    }
}