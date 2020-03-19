package com.gamelibrary2d.network;

import com.gamelibrary2d.Game;

public abstract class AbstractNetworkFrame<TFrameClient extends FrameClient>
        extends AbstractGenericNetworkFrame<TFrameClient, ClientObject, ClientPlayer> implements NetworkFrame<TFrameClient> {
    protected AbstractNetworkFrame(Game game, TFrameClient client) {
        super(game, client);
    }
}
