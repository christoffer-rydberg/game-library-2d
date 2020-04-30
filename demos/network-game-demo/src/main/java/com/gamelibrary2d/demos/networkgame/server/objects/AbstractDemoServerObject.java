package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.collision.UpdateResult;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.network.AbstractServerObject;

public abstract class AbstractDemoServerObject extends AbstractServerObject implements DemoServerObject, Collidable {

    private final byte objectIdentifier;

    private final Point velocity = new Point();

    private final Point beforeUpdate = new Point();

    private final Rectangle gameBounds;

    private float direction;

    private boolean destroyed;

    private float speed;

    protected AbstractDemoServerObject(byte objectIdentifier, Rectangle gameBounds) {
        this.objectIdentifier = objectIdentifier;
        this.gameBounds = gameBounds;
    }

    protected void reposition() {
        getPosition().set(beforeUpdate);
    }

    @Override
    public byte getObjectIdentifier() {
        return objectIdentifier;
    }

    @Override
    public UpdateResult update(float deltaTime) {
        beforeUpdate.set(getPosition());

        if (speed == 0f) {
            return UpdateResult.STILL;
        }

        getPosition().add(velocity.getX() * deltaTime, velocity.getY() * deltaTime);
        if (bounceIfOutside(gameBounds)) {
            reposition();
            return UpdateResult.STILL;
        } else {
            return UpdateResult.MOVED;
        }
    }

    @Override
    public void updated() {

    }

    @Override
    public void onDestroyed() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public float getDirection() {
        return direction;
    }

    @Override
    public void setDirection(float direction) {
        this.direction = normalizeDirection(direction);
        velocity.set(0, speed);
        velocity.rotate(direction);
    }

    protected void setSpeed(float speed) {
        this.speed = speed;
        velocity.normalize();
        velocity.multiply(speed);
    }

    private void scaleVelocity(float x, float y) {
        velocity.multiply(x, y);
        direction = normalizeDirection(velocity.getAngleDegrees());
        speed = velocity.getLength();
    }

    private float normalizeDirection(float direction) {
        return (((direction % 360f) + 360f) % 360f);
    }

    private boolean bounceIfOutside(Rectangle area) {
        var position = getPosition();
        var bounds = getBounds();

        var horizontalBounce =
                position.getX() + bounds.xMax() > area.xMax()
                        || position.getX() + bounds.xMin() < area.xMin();

        var verticalBounce =
                position.getY() + bounds.yMax() > area.yMax()
                        || position.getY() + bounds.yMin() < area.yMin();

        if (horizontalBounce || verticalBounce) {
            scaleVelocity(horizontalBounce ? -1f : 1f, verticalBounce ? -1f : 1f);
            return true;
        }

        return false;
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
