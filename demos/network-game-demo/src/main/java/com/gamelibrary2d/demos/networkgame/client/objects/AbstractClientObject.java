package com.gamelibrary2d.demos.networkgame.client.objects;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrameClient;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.util.DirectionAware;
import com.gamelibrary2d.util.DirectionInterpolation;
import com.gamelibrary2d.util.PositionInterpolator;

public abstract class AbstractClientObject
        extends AbstractGameObject<Renderable> implements ClientObject, Updatable, DirectionAware {

    private final int id;
    private final byte objectIdentifier;
    private final DemoFrameClient client;
    private final boolean autoRotate;
    private final PositionInterpolator positionInterpolator = new PositionInterpolator(this);
    private final DirectionInterpolation directionInterpolation = new DirectionInterpolation(this);

    private float direction;
    private Updatable updateAction;

    protected AbstractClientObject(byte objectIdentifier, DemoFrameClient client, boolean autoRotate, DataBuffer buffer) {
        this.client = client;
        this.objectIdentifier = objectIdentifier;
        this.autoRotate = autoRotate;
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
    public void setUpdateAction(Updatable updateAction) {
        this.updateAction = updateAction;
    }

    @Override
    public void update(float deltaTime) {
        positionInterpolator.update(deltaTime);
        directionInterpolation.update(deltaTime);

        if (updateAction != null) {
            updateAction.update(deltaTime);
        }

        var content = getContent();
        if (content instanceof Updatable) {
            ((Updatable) content).update(deltaTime);
        }
    }

    @Override
    public float getDirection() {
        return direction;
    }

    @Override
    public void setDirection(float direction) {
        this.direction = direction;
        if (autoRotate) {
            setRotation(direction);
        }
    }

    @Override
    public void setGoalPosition(float x, float y) {
        positionInterpolator.setGoal(x, y, 1f / client.getServerUpdatesPerSecond());
    }

    @Override
    public void setGoalDirection(float direction) {
        directionInterpolation.setGoal(direction, 1f / client.getServerUpdatesPerSecond());
    }
}
