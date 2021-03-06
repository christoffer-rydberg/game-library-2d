package com.gamelibrary2d.demos.networkgame.client.objects.network;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.frames.game.GameFrameClient;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.DurationEffect;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.InstantEffect;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.interpolation.InterpolatableAngle;
import com.gamelibrary2d.interpolation.PositionInterpolator;
import com.gamelibrary2d.components.denotations.Bounded;
import com.gamelibrary2d.components.denotations.Updatable;
import com.gamelibrary2d.components.objects.AbstractGameObject;

public abstract class AbstractClientObject
        extends AbstractGameObject implements ClientObject, Updatable {

    private final int id;
    private final byte primaryType;
    private final byte secondaryType;
    private final GameFrameClient client;
    private final PositionInterpolator positionInterpolator = new PositionInterpolator(this);
    private final InterpolatableAngle direction = new InterpolatableAngle();

    private final Point particleHotspot = new Point();

    private DurationEffect updateEffect;
    private InstantEffect destroyedEffect;
    private boolean accelerating;

    private Renderable content;

    protected AbstractClientObject(byte primaryType, GameFrameClient client, DataBuffer buffer) {
        this.primaryType = primaryType;
        this.client = client;
        this.secondaryType = buffer.get();
        id = buffer.getInt();
        setPosition(buffer.getFloat(), buffer.getFloat());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public byte getPrimaryType() {
        return primaryType;
    }

    @Override
    public byte getSecondaryType() {
        return secondaryType;
    }

    public Renderable getContent() {
        return content;
    }

    @Override
    public void setContent(Renderable content) {
        this.content = content;
    }

    @Override
    public void setUpdateEffect(DurationEffect updateEffect) {
        this.updateEffect = updateEffect;
    }

    @Override
    public Point getParticleHotspot() {
        return particleHotspot;
    }

    @Override
    public void setDestroyedEffect(InstantEffect destroyedEffect) {
        this.destroyedEffect = destroyedEffect;
    }

    public boolean isAccelerating() {
        return accelerating;
    }

    @Override
    public void setAccelerating(boolean accelerating) {
        this.accelerating = accelerating;
    }

    @Override
    public void destroy() {
        if (destroyedEffect != null) {
            destroyedEffect.onUpdate(this);
        }
    }

    @Override
    public void update(float deltaTime) {
        positionInterpolator.update(deltaTime);
        direction.update(deltaTime);

        if (useUpdateEffect()) {
            updateEffect.onUpdate(this, deltaTime);
        }

        if (content instanceof Updatable) {
            ((Updatable) content).update(deltaTime);
        }
    }

    protected boolean useUpdateEffect() {
        return updateEffect != null;
    }

    @Override
    public float getDirection() {
        return direction.getAngle();
    }

    @Override
    public void setGoalPosition(float x, float y) {
        positionInterpolator.setGoal(x, y, 1f / client.getServerUpdatesPerSecond());
    }

    @Override
    public void setGoalDirection(float direction) {
        this.direction.beginInterpolation(direction, 1f / client.getServerUpdatesPerSecond());
    }

    @Override
    public void setGoalRotation(float rotation) {

    }

    @Override
    protected void onRender(float alpha) {
        if (content != null) {
            content.render(alpha);
        }
    }

    @Override
    public Rectangle getBounds() {
        if (content instanceof Bounded)
            return ((Bounded) content).getBounds();
        else
            return Rectangle.EMPTY;
    }
}
