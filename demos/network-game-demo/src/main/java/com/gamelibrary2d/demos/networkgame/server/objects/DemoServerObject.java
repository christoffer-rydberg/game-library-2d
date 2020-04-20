package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.network.ServerObject;

public interface DemoServerObject extends ServerObject, Collidable {

    byte getObjectIdentifier();

    float getDirection();
}
