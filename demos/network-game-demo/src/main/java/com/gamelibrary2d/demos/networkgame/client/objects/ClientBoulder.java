package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.common.MessageParser;
import com.gamelibrary2d.network.AbstractClientObject;
import com.gamelibrary2d.network.FrameClient;
import com.gamelibrary2d.renderers.Renderer;

public class ClientBoulder extends AbstractClientObject {

    public ClientBoulder(FrameClient frameClient, DataBuffer buffer) {
        super(frameClient, buffer);
        setBounds(MessageParser.readRectangle(buffer));
    }

    public void setRenderer(Renderer renderer) {
        setContent(renderer);
    }

    @Override
    protected void onUpdate(float deltaTime) {

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
