package com.gamelibrary2d.collision.handlers;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.collision.CollidableInfo;
import com.gamelibrary2d.common.Rectangle;

public class RestrictedAreaHandler<T extends Collidable> implements UpdatedHandler<T> {
    private final Rectangle area;
    private final CollisionListener<T> collisionListener;

    public RestrictedAreaHandler(Rectangle area, CollisionListener<T> collisionListener) {
        this.area = area;
        this.collisionListener = collisionListener;
    }

    @Override
    public void updated(CollidableInfo<T> info) {
        var obj = info.getCollidable();

        var bounds = obj.getBounds();

        var horizontalBounce =
                obj.getPosX() + bounds.xMax() > area.xMax()
                        || obj.getPosX() + bounds.xMin() < area.xMin();

        var verticalBounce =
                obj.getPosY() + bounds.yMax() > area.yMax()
                        || obj.getPosY() + bounds.yMin() < area.yMin();

        if (horizontalBounce || verticalBounce) {
            collisionListener.onCollision(
                    obj,
                    horizontalBounce ? -2 * info.getSpeedX() : 0f,
                    verticalBounce ? -2 * info.getSpeedY() : 0f);
            obj.reposition(info.getPrevX(), info.getPrevY());
        }
    }

    public interface CollisionListener<T> {
        void onCollision(T obj, float forceX, float forceY);
    }
}