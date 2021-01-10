package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.collision.CollisionDetection;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.demos.networkgame.server.objects.DemoServerObject;
import com.gamelibrary2d.demos.networkgame.server.objects.ServerPlayer;
import com.gamelibrary2d.demos.networkgame.server.objects.ServerPortal;

import java.util.List;

public class DemoGameLogic {
    private final DemoGameServer server;
    private final GameSettings settings;
    private final CollisionDetection collisionDetection;

    private boolean gameOver = true;

    public DemoGameLogic(DemoGameServer server) {
        this.server = server;

        settings = new GameSettings(
                new Rectangle(0, 0, 1024, 1024),
                Rectangle.create(32f, 32f),
                Rectangle.create(32f, 32f));

        collisionDetection = new CollisionDetection(
                settings.getGameBounds(),
                getGameSettings().getObstacleBounds().getWidth() * 4,
                10);
    }

    public void startGame(List<ServerPlayer> players) {
        gameOver = false;

        var center = settings.getGameBounds().getCenter();

        for (byte i = 0; i < players.size(); ++i) {
            var player = players.get(i);
            player.setPosition(center);
            var initialRotation = RandomInstance.get().nextFloat() * 360f;
            player.setRotation(initialRotation);
            player.setSpeedAndDirection(ServerPlayer.MAX_SPEED, initialRotation);
            player.setSecondaryType(i);
            spawn(player);
        }

        var portal = new ServerPortal(this, settings.getPortalBounds());
        portal.setPosition(settings.getGameBounds().getCenter());
        spawn(portal);
    }

    public void endGame() {
        gameOver = true;
        server.endGame();
        collisionDetection.clear();
    }

    public boolean gameOver() {
        return gameOver;
    }

    public GameSettings getGameSettings() {
        return settings;
    }

    public void update(float deltaTime) {
        collisionDetection.update(deltaTime);
    }

    public void spawn(DemoServerObject obj) {
        obj.setDestroyed(false);
        obj.addCollisionDetection(collisionDetection);
        server.spawn(obj);
    }

    public void destroy(DemoServerObject obj) {
        if (!obj.isDestroyed()) {
            obj.setDestroyed(true);
            collisionDetection.remove(obj);
            server.destroy(obj);
        }
    }
}