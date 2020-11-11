package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrameClient;

public class RemotePlayer extends AbstractPlayer {

    public RemotePlayer(byte primaryType, DemoFrameClient client, DataBuffer buffer) {
        super(primaryType, client, buffer);
    }

}
