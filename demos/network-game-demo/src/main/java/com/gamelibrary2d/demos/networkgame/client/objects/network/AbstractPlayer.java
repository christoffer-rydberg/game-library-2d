package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.GameFrameClient;

public abstract class AbstractPlayer extends AbstractClientObject {

    protected AbstractPlayer(byte primaryType, GameFrameClient client, DataBuffer buffer) {
        super(primaryType, client, true, buffer);
        getParticleHotspot().set(0, -30);
    }
}
