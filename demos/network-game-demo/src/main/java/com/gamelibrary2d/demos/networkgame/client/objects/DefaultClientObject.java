package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrameClient;

public class DefaultClientObject extends AbstractClientObject {

    public DefaultClientObject(byte objectIdentifier, DemoFrameClient client, boolean autoRotate, DataBuffer buffer) {
        super(objectIdentifier, client, autoRotate, buffer);
    }

}
