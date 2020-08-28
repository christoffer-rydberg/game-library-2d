package com.gamelibrary2d.collision.handlers;

import com.gamelibrary2d.collision.CollidableInfo;
import com.gamelibrary2d.collision.CollisionResult;
import com.gamelibrary2d.collision.Obstacle;

public class BounceHandler<T1 extends Obstacle, T2 extends Obstacle> implements CollisionHandler<T1, T2> {
    private final Class<T2> obstacleClass;

    private CollidableInfo<T1> updated;
    private boolean reposition;

    public BounceHandler(Class<T2> obstacleClass) {
        this.obstacleClass = obstacleClass;
    }

    @Override
    public Class<T2> getCollidableClass() {
        return obstacleClass;
    }

    @Override
    public void initialize(CollidableInfo<T1> updated) {
        this.updated = updated;
    }

    private double getSpeedTowardsCollision(CollidableInfo obj, CollidableInfo other) {
        var speedX = obj.getSpeedX();
        var speedY = obj.getSpeedY();
        var speed = Math.sqrt(speedX * speedX + speedY * speedY);

        var collisionAngle = Math.atan2(
                other.getX() - obj.getX(),
                other.getY() - obj.getY()
        );

        var direction = Math.atan2(speedX, speedY);

        return (speed * Math.cos(collisionAngle - direction));
    }

    @Override
    public CollisionResult collision(CollidableInfo<T2> collided) {
        var distX = collided.getX() - updated.getX();
        var distY = collided.getY() - updated.getY();
        var dist = Math.sqrt(distX * distX + distY * distY);

        var diameter = updated.getCollidable().getBounds().width();
        var otherDiameter = collided.getCollidable().getBounds().width();
        var isCollision = dist * 2 <= diameter + otherDiameter;
        if (isCollision) {
            var u1 = getSpeedTowardsCollision(updated, collided);
            var u2 = -getSpeedTowardsCollision(collided, updated);
            if (u1 > u2) {
                var m1 = updated.getCollidable().getMass();
                var m2 = collided.getCollidable().getMass();
                var mTotal = m1 + m2;

                var commonFactor = ((m1 - m2) / mTotal);
                var v1 = commonFactor * u1 + (2 * m2 / mTotal) * u2;
                var v2 = (2 * m1 / mTotal) * u1 - commonFactor * u2;

                var normalizedDistX = distX / dist;
                var normalizedDistY = distY / dist;

                updated.getCollidable().onPushed(
                        collided.getCollidable(),
                        (float) (normalizedDistX * v1 - normalizedDistX * u1),
                        (float) (normalizedDistY * v1 - normalizedDistY * u1)
                );

                collided.getCollidable().onPushed(
                        updated.getCollidable(),
                        (float) (normalizedDistX * v2 - normalizedDistX * u2),
                        (float) (normalizedDistY * v2 - normalizedDistY * u2)
                );

                reposition = true;
            }
        }

        return CollisionResult.CONTINUE;
    }

    @Override
    public void finish() {
        if (reposition) {
            updated.getCollidable().reposition(updated.getPrevX(), updated.getPrevY());
            reposition = false;
        }
    }
}