package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.functional.ParameterizedAction;
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
    private final Point particleHotspot = new Point();

    private float direction;
    private Updatable updateAction;
    private ParameterizedAction<ClientObject> destroyAction;

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
    public Point getParticleHotspot() {
        return particleHotspot;
    }

    @Override
    public void setDestroyAction(ParameterizedAction<ClientObject> destroyAction) {
        this.destroyAction = destroyAction;
    }

    @Override
    public void destroy() {
        if (destroyAction != null) {
            destroyAction.invoke(this);
        }
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
