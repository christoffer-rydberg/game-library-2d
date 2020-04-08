package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrameClient;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.network.AbstractClientObject;

public abstract class AbstractDemoClientObject extends AbstractClientObject implements DemoClientObject {
    private final DemoFrameClient client;

    private final byte objectIdentifier;

    protected AbstractDemoClientObject(byte objectIdentifier, DemoFrameClient client, DataBuffer buffer) {
        super(buffer);
        this.objectIdentifier = objectIdentifier;
        this.client = client;
    }

    @Override
    public byte getObjectIdentifier() {
        return objectIdentifier;
    }

    @Override
    public void setContent(Renderable content) {
        super.setContent(content);
    }

    @Override
    protected float getUpdatesPerSecond() {
        return client.getServerUpdatesPerSecond();
    }

    @Override
    protected boolean handleMouseButtonDown(int button, int mods, float projectedX, float projectedY) {
        return false;
    }

    @Override
    protected boolean handleMouseHover(float projectedX, float projectedY) {
        return false;
    }

    @Override
    protected boolean handleMouseDrag(float projectedX, float projectedY) {
        return false;
    }

    @Override
    protected void handleMouseButtonRelease(int button, int mods, float projectedX, float projectedY) {

    }
}
