package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.network.AbstractClientObject;
import com.gamelibrary2d.renderers.Renderer;

public class ClientBoulder extends AbstractClientObject {
    private final float serverUpdatesPerSecond;

    public ClientBoulder(DataBuffer buffer, float serverUpdatesPerSecond) {
        super(buffer);
        this.serverUpdatesPerSecond = serverUpdatesPerSecond;
    }

    public void setContent(Renderable content) {
        super.setContent(content);
    }

    @Override
    protected void onUpdate(float deltaTime) {

    }

    @Override
    protected float getUpdatesPerSecond() {
        return serverUpdatesPerSecond;
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
