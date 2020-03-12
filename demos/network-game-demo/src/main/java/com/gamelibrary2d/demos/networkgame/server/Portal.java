package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.collision.CollisionAware;
import com.gamelibrary2d.collision.UpdateResult;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;

public class Portal implements Collidable, CollisionAware<ServerBoulder> {
    private static final float SPAWN_RATE = 1f;

    private final DemoGameLogic gameLogic;
    private final Rectangle bounds;
    private final Point position;
    private float spawnTimer;
    private boolean collided;

    public Portal(DemoGameLogic gameLogic, float posX, float posY) {
        this.gameLogic = gameLogic;
        this.bounds = gameLogic.getGameSettings().getBoulderBounds();
        position = new Point(posX, posY);
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
            gameLogic.spawnBoulder(position.getX(), position.getY());
            spawnTimer -= SPAWN_RATE;
        }

        collided = false;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public float getPosX() {
        return position.getX();
    }

    @Override
    public float getPosY() {
        return position.getY();
    }
}
