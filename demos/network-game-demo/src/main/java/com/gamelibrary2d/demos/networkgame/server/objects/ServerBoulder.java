package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionAware;
import com.gamelibrary2d.collision.UpdateResult;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;

public class ServerBoulder extends AbstractDemoServerObject implements CollisionAware<ServerBoulder> {
    private final Point velocity = new Point();
    private final Rectangle gameBounds;
    private final Point beforeUpdate = new Point();

    public ServerBoulder(Rectangle gameBounds, Rectangle bounds) {
        super(ObjectIdentifiers.BOULDER);
        this.gameBounds = gameBounds;
        this.setBounds(bounds);
        setDirection(RandomInstance.get().nextFloat() * 360f);
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
    protected void setDirection(float direction) {
        velocity.set(0, 100f);
        velocity.rotate(direction);
        super.setDirection(direction);
    }

    @Override
    public void updated() {

    }

    @Override
    public boolean onCollisionWith(ServerBoulder other) {
        // TODO: Implement more realistic physics (colliding balls)
        setPosition(beforeUpdate);
        setDirection(getDirection() + 180f);
        return true;
    }

    private void bounceIfOutside(Rectangle area) {
        var bounced = false;

        var position = getPosition();
        var bounds = getBounds();
        if (position.getX() + bounds.xMax() > area.xMax()) {
            position.setX(area.xMax() - bounds.xMax());
            velocity.setX(-velocity.getX());
            bounced = true;
        } else if (position.getX() + bounds.xMin() < area.xMin()) {
            position.setX(area.xMin() - bounds.xMin());
            velocity.setX(-velocity.getX());
            bounced = true;
        }

        if (position.getY() + bounds.yMax() > area.yMax()) {
            position.setY(area.yMax() - bounds.yMax());
            velocity.setY(-velocity.getY());
            bounced = true;
        } else if (position.getY() + bounds.yMin() < area.yMin()) {
            position.setY(area.yMin() - bounds.yMin());
            velocity.setY(-velocity.getY());
            bounced = true;
        }

        if (bounced) {
            super.setDirection(velocity.getAngleDegrees());
        }
    }
}
