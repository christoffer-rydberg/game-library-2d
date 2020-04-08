package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.UpdateResult;
import com.gamelibrary2d.network.AbstractServerObject;

public abstract class AbstractDemoServerObject extends AbstractServerObject implements DemoServerObject {

    private final byte objectIdentifier;

    private float rotation;

    protected AbstractDemoServerObject(byte objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }

    @Override
    public byte getObjectIdentifier() {
        return objectIdentifier;
    }

    @Override
    public UpdateResult update(float deltaTime) {
        return null;
    }

    @Override
    public void updated() {

    }

    @Override
    public float getRotation() {
        return rotation;
    }

    protected void setRotation(float rotation) {
        this.rotation = (((rotation % 360f) + 360f) % 360f);
    }

    @Override
    public float getPosX() {
        return getPosition().getX();
    }

    @Override
    public float getPosY() {
        return getPosition().getY();
    }

}
