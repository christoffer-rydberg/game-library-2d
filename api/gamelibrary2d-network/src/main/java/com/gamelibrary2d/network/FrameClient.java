package com.gamelibrary2d.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

public interface FrameClient {

    void configureInitialization(CommunicationSteps steps);

    void onInitialized();

    void onMessage(DataBuffer buffer);

    Communicator getCommunicator();

    void setCommunicator(Communicator communicator);

    float getServerUpdatesPerSecond();

    /**
     * The max number of retries for each communication step.
     */
    int getInitializationRetries();

    /**
     * The delay between retries of communication steps in milliseconds.
     */
    int getInitializationRetryDelay();

    boolean isUpdatingLocalServer();

    void setUpdateLocalServer(boolean updateLocalServer);
}
