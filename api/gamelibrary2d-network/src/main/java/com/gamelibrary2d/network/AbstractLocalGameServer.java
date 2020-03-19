package com.gamelibrary2d.network;

import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.server.AbstractLocalServer;

public abstract class AbstractLocalGameServer extends AbstractLocalServer {

    private GameLogic gameLogic;

    @Override
    protected final void onInitialize() throws InitializationException {
        gameLogic = initializeGameLogic();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (gameLogic != null && gameLogic.update(deltaTime)) {
            updateClients();
        }
    }

    protected abstract void updateClients();

    protected abstract GameLogic initializeGameLogic() throws InitializationException;

}
