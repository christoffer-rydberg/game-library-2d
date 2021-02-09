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
        T obj = info.getCollidable();

        Rectangle bounds = obj.getBounds();

        boolean horizontalBounce =
                obj.getPosX() + bounds.getUpperX() > area.getUpperX()
                        || obj.getPosX() + bounds.getLowerX() < area.getLowerX();

        boolean verticalBounce =
                obj.getPosY() + bounds.getUpperY() > area.getUpperY()
                        || obj.getPosY() + bounds.getLowerY() < area.getLowerY();

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