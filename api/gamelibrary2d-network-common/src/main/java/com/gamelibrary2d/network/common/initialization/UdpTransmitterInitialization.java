package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.UdpTransmitter;

import java.io.IOException;

public class UdpTransmitterInitialization implements ConsumerStep {

    @Override
    public boolean run(CommunicationContext context, Communicator communicator, DataBuffer inbox) throws IOException {
        var sendUdp = inbox.getBool();
        if (!sendUdp)
            return true;

        int udpPort = inbox.getInt();
        if (udpPort <= 0)
            throw new IOException(String.format("Invalid udp port: %s", udpPort));

        if (communicator instanceof UdpTransmitter) {
            ((UdpTransmitter) communicator).connectUdpTransmitter(udpPort);
        } else {
            throw new IOException("Communicator does not implement the UdpTransmitter interface");
        }
        return true;
    }
}