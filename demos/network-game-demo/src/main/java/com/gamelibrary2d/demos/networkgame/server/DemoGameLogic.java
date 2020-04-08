package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.collision.Collidable;
import com.gamelibrary2d.collision.CollisionDetection;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.demos.networkgame.server.objects.DemoServerObject;
import com.gamelibrary2d.demos.networkgame.server.objects.ServerPortal;

public class DemoGameLogic {
    private final DemoGameServer server;
    private final GameSettings settings;
    private final CollisionDetection<Collidable> collisionDetection;

    public DemoGameLogic(DemoGameServer server) {
        this.server = server;

        settings = new GameSettings(
                new Rectangle(0, 0, 1024, 1024),
                Rectangle.centered(32f, 32f),
                Rectangle.centered(32f, 32f));

        collisionDetection = new CollisionDetection<>(
                settings.getGameBounds(),
                getGameSettings().getBoulderBounds().width() * 4,
                10);

        var portal = new ServerPortal(this, settings.getBoulderBounds());
        portal.setPosition(settings.getGameBounds().center());
        spawn(portal);
    }

    public GameSettings getGameSettings() {
        return settings;
    }

    public void update(float deltaTime) {
        collisionDetection.update(deltaTime);
    }

    public void spawn(DemoServerObject obj) {
        collisionDetection.add(obj);
        server.spawn(obj);
    }

    public void destroy(DemoServerObject obj) {
        collisionDetection.remove(obj);
        server.destroy(obj);
    }
}