package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionDetection;
import com.gamelibrary2d.collision.CollisionResult;
import com.gamelibrary2d.collision.Obstacle;
import com.gamelibrary2d.collision.handlers.DefaultCollisionHandler;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.demos.networkgame.server.DemoGameLogic;

public class ServerPortal extends AbstractDemoServerObject {
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
    public void update(float deltaTime) {
        spawnTimer += deltaTime;
        super.update(deltaTime);

        if (!collided && spawnTimer > SPAWN_RATE) {
            var boulder = new ServerBoulder(gameLogic.getGameSettings().getBoulderBounds());
            boulder.setPosition(getPosition());
            gameLogic.spawn(boulder);
            spawnTimer -= SPAWN_RATE;
        }

        collided = false;
    }

    @Override
    public void addCollisionDetection(CollisionDetection collisionDetection) {
        collisionDetection.add(this, new DefaultCollisionHandler<>(Obstacle.class, ServerPortal::onCollision));
    }

    private CollisionResult onCollision(Obstacle obstacle) {
        collided = true;
        return CollisionResult.ABORT;
    }
}