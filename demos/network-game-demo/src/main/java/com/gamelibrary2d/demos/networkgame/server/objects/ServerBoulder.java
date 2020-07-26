package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionParameters;
import com.gamelibrary2d.collision.CollisionAware;
import com.gamelibrary2d.collision.CollisionResult;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;

public class ServerBoulder extends AbstractDemoServerObject implements CollisionAware<ServerBoulder> {

    public ServerBoulder(Rectangle gameBounds, Rectangle bounds) {
        super(ObjectIdentifiers.BOULDER, gameBounds);
        this.setBounds(bounds);
        setSpeed(90f);
        setDirection(RandomInstance.get().nextFloat() * 360f);
    }

    @Override
    public Class<ServerBoulder> getCollidableClass() {
        return ServerBoulder.class;
    }

    @Override
    public CollisionResult onCollision(ServerBoulder other, CollisionParameters params) {
        // TODO: Implement more realistic physics (colliding balls)
        reposition();
        setDirection(getDirection() + 180f);
        return CollisionResult.ABORT;
    }
}
