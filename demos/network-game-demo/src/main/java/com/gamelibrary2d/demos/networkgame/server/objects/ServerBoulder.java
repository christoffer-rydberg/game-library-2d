package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionAware;
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
    public boolean onCollisionWith(ServerBoulder other) {
        // TODO: Implement more realistic physics (colliding balls)
        reposition();
        setDirection(getDirection() + 180f);
        return true;
    }
}
