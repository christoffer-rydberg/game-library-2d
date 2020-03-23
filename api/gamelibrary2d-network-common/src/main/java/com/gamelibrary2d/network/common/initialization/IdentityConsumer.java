package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;

public class IdentityConsumer implements ConsumerStep {

    @Override
    public boolean run(Communicator communicator, DataBuffer inbox) {
        int communicatorId = inbox.getInt();
        communicator.setId(communicatorId);
        return true;
    }
}