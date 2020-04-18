package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrameClient;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.network.PositionInterpolator;
import com.gamelibrary2d.network.RotationInterpolator;
import com.gamelibrary2d.objects.AbstractGameObject;

public abstract class AbstractClientObject
        extends AbstractGameObject<Renderable> implements ClientObject, Updatable {

    private final int id;
    private final byte objectIdentifier;
    private final DemoFrameClient client;
    private final PositionInterpolator positionInterpolator = new PositionInterpolator(this);
    private final RotationInterpolator rotationInterpolator = new RotationInterpolator(this);

    private UpdateAction updateAction;

    protected AbstractClientObject(byte objectIdentifier, DemoFrameClient client, DataBuffer buffer) {
        this.client = client;
        this.objectIdentifier = objectIdentifier;
        id = buffer.getInt();
        setPosition(buffer.getFloat(), buffer.getFloat());
    }

    @Override
    public int getId() {
        return id;
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
    public void setUpdateAction(UpdateAction action) {
        this.updateAction = action;
    }

    @Override
    public void update(float deltaTime) {
        positionInterpolator.update(deltaTime);
        rotationInterpolator.update(deltaTime);

        if (updateAction != null) {
            updateAction.invoke(this, deltaTime);
        }

        var content = getContent();
        if (content instanceof Updatable) {
            ((Updatable) content).update(deltaTime);
        }
    }

    @Override
    public void setGoalPosition(float x, float y) {
        positionInterpolator.setGoal(x, y, 1f / client.getServerUpdatesPerSecond());
    }

    @Override
    public void setGoalRotation(float rotation) {
        rotationInterpolator.setGoal(rotation, 1f / client.getServerUpdatesPerSecond());
    }
}
