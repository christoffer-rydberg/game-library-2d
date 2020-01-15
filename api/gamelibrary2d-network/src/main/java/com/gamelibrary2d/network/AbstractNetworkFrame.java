package com.gamelibrary2d.network;

import com.gamelibrary2d.Game;

public abstract class AbstractNetworkFrame extends AbstractGenericNetworkFrame<ClientObject, ClientPlayer> implements NetworkFrame {
    protected AbstractNetworkFrame(Game game) {
        super(game);
    }
}
