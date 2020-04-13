package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.UdpReceiver;

import java.io.IOException;

public class UdpReceiverInitialization implements ProducerStep {

    private int localPort;

    public UdpReceiverInitialization(int localPort) {
        this.localPort = localPort;
    }

    @Override
    public void run(CommunicationContext context, Communicator communicator) throws IOException {
        var buffer = communicator.getOutgoing();
        if (communicator instanceof UdpReceiver) {
            var udpConnector = (UdpReceiver) communicator;
            udpConnector.connectUdpReceiver(localPort);
            buffer.putBool(true);
            buffer.putInt(localPort);
        } else {
            buffer.putBool(false);
        }
    }
}