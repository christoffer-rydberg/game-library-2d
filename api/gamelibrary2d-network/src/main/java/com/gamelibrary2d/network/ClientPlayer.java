package com.gamelibrary2d.network;

import com.gamelibrary2d.network.common.Communicator;

public interface ClientPlayer extends ClientObject {

    boolean isLocal(Communicator communicator);

}