package com.gamelibrary2d.demos.networkgame.client.frames.menu;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.demos.networkgame.client.ResourceManager;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.components.denotations.Updatable;
import com.gamelibrary2d.components.objects.DefaultGameObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.updaters.DurationUpdater;
import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updates.PositionUpdate;
import com.gamelibrary2d.components.widgets.Label;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class Credits implements Renderable, Updatable {
    private final Window window;
    private final SequentialUpdater updater = new SequentialUpdater();
    private final DefaultGameObject<Label> credits;

    private boolean enabled;
    private float speedFactor;

    Credits(Window window, ResourceManager resourceManager) throws IOException {
        this.window = window;
        try (InputStream stream = resourceManager.open("credits.txt")) {
            String text = Read.text(stream, StandardCharsets.UTF_8);
            Label label = new Label(text, new TextRenderer(Fonts.button()), Color.SOFT_BLUE);
            credits = new DefaultGameObject<>(label);
        }
    }

    void setSpeedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
    }

    boolean isEnabled() {
        return enabled;
    }

    void disable() {
        enabled = false;
    }

    void enable(Action onFinished) {
        enabled = true;
        setSpeedFactor(1f);

        credits.setPosition(window.getWidth() / 2f, 0);
        float height = credits.getContent().calculateBounds().getHeight();
        float windowHeight = window.getHeight();

        updater.clear();

        updater.add(new DurationUpdater(
                60f,
                true,
                new PositionUpdate<>(credits, 0, height + windowHeight)));

        updater.add(new InstantUpdater(dt -> onFinished.perform()));
    }

    @Override
    public void update(float deltaTime) {
        if (enabled) {
            updater.update(deltaTime * speedFactor);
        }
    }

    @Override
    public void render(float alpha) {
        if (enabled) {
            credits.render(alpha);
        }
    }
}
