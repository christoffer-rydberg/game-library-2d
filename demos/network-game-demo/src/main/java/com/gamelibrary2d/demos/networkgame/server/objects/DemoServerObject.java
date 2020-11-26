package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionDetection;
import com.gamelibrary2d.network.ServerObject;

public interface DemoServerObject extends ServerObject {

    byte getObjectIdentifier();

    float getDirection();

    float getRotation();

    float getSpeed();

    boolean isDestroyed();

    void setDestroyed(boolean destroyed);

    void addCollisionDetection(CollisionDetection collisionDetection);

    boolean isAccelerating();
}
