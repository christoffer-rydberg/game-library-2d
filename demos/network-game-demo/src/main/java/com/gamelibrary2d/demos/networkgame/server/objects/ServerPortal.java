package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionParameters;
import com.gamelibrary2d.collision.CollisionAware;
import com.gamelibrary2d.collision.CollisionResult;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.demos.networkgame.server.DemoGameLogic;

public class ServerPortal extends AbstractDemoServerObject implements CollisionAware<ServerBoulder> {
    private static final float SPAWN_RATE = 1f;

    private final DemoGameLogic gameLogic;
    private float spawnTimer;
    private boolean collided;

    public ServerPortal(DemoGameLogic gameLogic, Rectangle gameBounds, Rectangle bounds) {
        super(ObjectIdentifiers.PORTAL, gameBounds);
        this.gameLogic = gameLogic;
        setBounds(bounds);
    }

    @Override
    public Class<ServerBoulder> getCollidableClass() {
        return ServerBoulder.class;
    }

    @Override
    public CollisionResult onCollision(ServerBoulder collidable, CollisionParameters params) {
        collided = true;
        return CollisionResult.ABORT;
    }

    @Override
    public void update(float deltaTime) {
        spawnTimer += deltaTime;
        super.update(deltaTime);
    }

    @Override
    public void updated() {
        super.updated();
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
