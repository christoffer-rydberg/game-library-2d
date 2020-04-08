package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionAware;
import com.gamelibrary2d.collision.UpdateResult;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.demos.networkgame.server.DemoGameLogic;

public class ServerPortal extends AbstractDemoServerObject implements CollisionAware<ServerBoulder> {
    private static final float SPAWN_RATE = 1f;

    private final DemoGameLogic gameLogic;
    private float spawnTimer;
    private boolean collided;

    public ServerPortal(DemoGameLogic gameLogic, Rectangle bounds) {
        super(ObjectIdentifiers.PORTAL);
        this.gameLogic = gameLogic;
        setBounds(bounds);
    }

    @Override
    public Class<ServerBoulder> getCollidableClass() {
        return ServerBoulder.class;
    }

    @Override
    public boolean onCollisionWith(ServerBoulder collidable) {
        collided = true;
        return true;
    }

    @Override
    public UpdateResult update(float deltaTime) {
        spawnTimer += deltaTime;
        return UpdateResult.STILL;
    }

    @Override
    public void updated() {
        if (!collided && spawnTimer > SPAWN_RATE) {
            var boulder = new ServerBoulder(
                    gameLogic.getGameSettings().getGameBounds(),
                    gameLogic.getGameSettings().getBoulderBounds());
            boulder.setPosition(getPosition());
            gameLogic.spawn(boulder);
            spawnTimer -= SPAWN_RATE;
        }

        collided = false;
    }
}
