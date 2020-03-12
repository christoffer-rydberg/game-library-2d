package com.gamelibrary2d.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;

public interface FrameClient {

    void configureAuthentication(CommunicationInitializer initializer);

    void configureInitialization(CommunicationInitializer initializer);

    void onMessage(DataBuffer buffer);

    float getServerUpdateRate();

    void onDisconnected(Throwable cause);

    /**
     * The max number of retries for each initialization step.
     */
    int getInitializationRetries();

    /**
     * The delay between retries of initialization steps in milliseconds.
     */
    int getInitializationRetryDelay();
}
