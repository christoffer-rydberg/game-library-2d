package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.UdpTransmitter;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

import java.io.IOException;

public class UdpTransmitterInitialization implements ConsumerStep {

    @Override
    public boolean run(Communicator communicator, DataBuffer inbox) throws InitializationException {

        var sendUdp = inbox.getBool();
        if (!sendUdp)
            return true;

        int udpPort = inbox.getInt();
        if (udpPort <= 0)
            throw new InitializationException(String.format("Invalid udp port: %s", udpPort));
        var wrapped = communicator.unwrap();
        if (wrapped instanceof UdpTransmitter) {
            try {
                ((UdpTransmitter) wrapped).connectUdpTransmitter(udpPort);
            } catch (IOException e) {
                throw new InitializationException(String.format("Failed to connect UDP transmitter to host %s:%s",
                        communicator.getEndpoint(), udpPort), e);
            }
        } else {
            throw new InitializationException("Communicator does not implement the UdpTransmitter interface");
        }
        return true;
    }
}