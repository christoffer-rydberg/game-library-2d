package com.gamelibrary2d.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

public interface FrameClient<T extends Communicator> {

    void configureInitialization(CommunicationSteps steps);

    void onInitialized();

    void onMessage(DataBuffer buffer);

    T getCommunicator();

    void setCommunicator(T communicator);

    float getServerUpdatesPerSecond();

    /**
     * The max number of retries for each communication step.
     */
    int getInitializationRetries();

    /**
     * The delay between retries of communication steps in milliseconds.
     */
    int getInitializationRetryDelay();

}
