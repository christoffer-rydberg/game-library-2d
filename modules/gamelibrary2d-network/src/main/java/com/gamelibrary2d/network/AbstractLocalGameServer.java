package com.gamelibrary2d.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.server.AbstractSecureLocalServer;

import java.io.IOException;

public abstract class AbstractLocalGameServer extends AbstractSecureLocalServer {

    private GameLogic gameLogic;

    @Override
    protected void onUpdate(float deltaTime) {
        if (gameLogic != null && gameLogic.update(deltaTime)) {
            gameLogic.updateClients();
        }
    }

    @Override
    protected void onInitialized(Communicator communicator) {
        gameLogic.initialized(communicator);
    }

    @Override
    protected void onDisconnected(Communicator communicator, boolean pending) {
        if (gameLogic != null) {
            gameLogic.disconnected(communicator, pending);
            uninitializeGameLogic();
            gameLogic = null;
        }
    }

    @Override
    protected void onConfigureAuthentication(CommunicationInitializer initializer) throws InitializationException {
        try {
            gameLogic = initializeGameLogic();
            gameLogic.configureAuthentication(initializer);
        } catch (IOException e) {
            throw new InitializationException("Failed to initialize game logic", e);
        }
    }

    @Override
    protected void onConfigureInitialization(CommunicationInitializer initializer) {
        gameLogic.configureInitialization(initializer);
    }

    @Override
    public void onMessage(Communicator com, DataBuffer buffer) {
        gameLogic.onMessage(com, buffer);
    }

    protected abstract GameLogic initializeGameLogic() throws IOException;

    protected abstract void uninitializeGameLogic();
}