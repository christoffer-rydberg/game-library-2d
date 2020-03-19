package com.gamelibrary2d.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;

public interface FrameClient<T extends Communicator> {

    void configureInitialization(CommunicationInitializer initializer);

    void onMessage(DataBuffer buffer);

    T getCommunicator();

    void setCommunicator(T communicator);

    float getServerUpdateRate();

    /**
     * The max number of retries for each initialization step.
     */
    int getInitializationRetries();

    /**
     * The delay between retries of initialization steps in milliseconds.
     */
    int getInitializationRetryDelay();
}
