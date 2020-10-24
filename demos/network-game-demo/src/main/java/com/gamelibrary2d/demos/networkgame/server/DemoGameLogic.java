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

    private boolean gameRunning;

    public DemoGameLogic(DemoGameServer server) {
        this.server = server;

        settings = new GameSettings(
                new Rectangle(0, 0, 1024, 1024),
                Rectangle.centered(32f, 32f),
                Rectangle.centered(32f, 32f));

        collisionDetection = new CollisionDetection(
                settings.getGameBounds(),
                getGameSettings().getBoulderBounds().width() * 4,
                10);
    }

    public void startGame(List<ServerPlayer> players) {
        gameRunning = true;

        var center = settings.getGameBounds().center();

        for (var player : players) {
            player.setPosition(center);
            player.setDirection(RandomInstance.get().nextFloat() * 360f);
            spawn(player);
        }

        var portal = new ServerPortal(this, settings.getPortalBounds());
        portal.setPosition(settings.getGameBounds().center());
        spawn(portal);
    }

    public void endGame() {
        gameRunning = false;
        server.endGame();
        collisionDetection.clear();
    }

    public boolean gameIsRunning() {
        return gameRunning;
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
        obj.setDestroyed(true);
        collisionDetection.remove(obj);
        server.destroy(obj);
    }
}