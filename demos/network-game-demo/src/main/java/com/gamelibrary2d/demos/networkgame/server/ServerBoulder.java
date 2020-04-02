package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.collision.CollisionAware;
import com.gamelibrary2d.collision.UpdateResult;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.network.AbstractServerObject;

class ServerBoulder extends AbstractServerObject implements Collidable, CollisionAware<ServerBoulder> {
    private final Point velocity;
    private final Rectangle gameBounds;
    private final Point beforeUpdate = new Point();

    ServerBoulder(Rectangle gameBounds, Rectangle bounds) {
        this.gameBounds = gameBounds;
        this.setBounds(bounds);
        velocity = new Point(0f, 100f);
        velocity.rotate(RandomInstance.get().nextFloat() * 360f);
    }

    @Override
    public float getPosX() {
        return getPosition().getX();
    }

    @Override
    public float getPosY() {
        return getPosition().getY();
    }

    @Override
    protected void onSerializeMessage(DataBuffer buffer) {

    }

    @Override
    public Class<ServerBoulder> getCollidableClass() {
        return ServerBoulder.class;
    }

    @Override
    public UpdateResult update(float deltaTime) {
        beforeUpdate.set(getPosition());
        getPosition().add(velocity.getX() * deltaTime, velocity.getY() * deltaTime);
        bounceIfOutside(gameBounds);
        return UpdateResult.MOVED;
    }

    @Override
    public void updated() {

    }

    @Override
    public boolean onCollisionWith(ServerBoulder other) {
        // TODO: Implement more realistic physics (colliding balls)
        setPosition(beforeUpdate);
        velocity.multiply(-1f);
        return true;
    }

    private void bounceIfOutside(Rectangle area) {
        var position = getPosition();
        var bounds = getBounds();
        if (position.getX() + bounds.xMax() > area.xMax()) {
            position.setX(area.xMax() - bounds.xMax());
            velocity.setX(-velocity.getX());
        } else if (position.getX() + bounds.xMin() < area.xMin()) {
            position.setX(area.xMin() - bounds.xMin());
            velocity.setX(-velocity.getX());
        }

        if (position.getY() + bounds.yMax() > area.yMax()) {
            position.setY(area.yMax() - bounds.yMax());
            velocity.setY(-velocity.getY());
        } else if (position.getY() + bounds.yMin() < area.yMin()) {
            position.setY(area.yMin() - bounds.yMin());
            velocity.setY(-velocity.getY());
        }
    }
}
