package com.gamelibrary2d.network.common;

import java.io.IOException;

public interface ChannelDisconnectedHandler {

    void onDisconnected(IOException exception);

}
