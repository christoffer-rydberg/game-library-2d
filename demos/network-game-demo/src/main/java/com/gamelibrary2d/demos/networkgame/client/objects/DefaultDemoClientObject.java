package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrameClient;

public class DefaultDemoClientObject extends AbstractDemoClientObject {

    public DefaultDemoClientObject(byte objectIdentifier, DemoFrameClient client, DataBuffer buffer) {
        super(objectIdentifier, client, buffer);
    }

    @Override
    protected void onUpdate(float deltaTime) {

    }
}
