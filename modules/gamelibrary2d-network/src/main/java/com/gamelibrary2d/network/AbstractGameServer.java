package com.gamelibrary2d.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.server.AbstractSecureNetworkServer;

public abstract class AbstractGameServer extends AbstractSecureNetworkServer {

    private GameLogic gameLogic;

    private int updateCounter;

    private int streamRate;

    private UpdateLoop mainLoop;

    protected AbstractGameServer(int sslPort, int reconnectionPort, int streamRate) {
        super(sslPort, reconnectionPort);
        this.streamRate = streamRate;
    }

    protected AbstractGameServer(int sslPort, int streamRate) {
        super(sslPort);
        this.streamRate = streamRate;
    }

    public void start(int ups) {
        mainLoop = new UpdateLoop(this, ups);
        mainLoop.run();
    }

    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public void onUpdate(float deltaTime) {
        if (gameLogic.update(deltaTime)) {
            if (updateCounter == streamRate) {
                gameLogic.updateClients();
                updateCounter = 1;
            } else {
                ++updateCounter;
            }
        }
    }

    @Override
    protected void onConfigureAuthentication(CommunicationInitializer initializer) {
        gameLogic.configureAuthentication(initializer);
    }

    @Override
    protected void onConfigureInitialization(CommunicationInitializer initializer) {
        gameLogic.configureInitialization(initializer);
    }

    @Override
    protected void onInitialized(Communicator communicator) {
        gameLogic.initialized(communicator);
    }

    @Override
    protected void onConnectionLost(Communicator communicator, boolean pending) {
        gameLogic.disconnected(communicator, pending);
    }

    @Override
    public void onMessage(Communicator com, DataBuffer buffer) {
        gameLogic.onMessage(com, buffer);
    }
}