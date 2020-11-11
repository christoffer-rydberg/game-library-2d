package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.objects.DefaultGameObject;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.updaters.DurationUpdater;
import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updates.PositionUpdate;
import com.gamelibrary2d.widgets.Label;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CreditsFrame extends AbstractFrame {
    private final DemoGame game;
    private DefaultGameObject<Label> credits;

    public CreditsFrame(DemoGame game) {
        this.game = game;
    }

    @Override
    protected void onInitialize(InitializationContext context) throws IOException {
        var url = CreditsFrame.class.getClassLoader().getResource("credits.txt");
        var text = Read.text(url, StandardCharsets.UTF_8);
        var label = new Label(text, new TextRenderer(Fonts.button()), Color.SOFT_BLUE);
        credits = new DefaultGameObject<>(label);
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    private void showCredits() {
        var height = credits.getContent().calculateBounds().height();
        var windowHeight = game.getWindow().height();

        var updater = new SequentialUpdater();

        updater.add(new DurationUpdater(
                20f,
                true,
                new PositionUpdate(credits, 0, height + windowHeight)));

        updater.add(new InstantUpdater(dt -> game.goToMenu()));

        runUpdater(updater);
    }

    @Override
    protected void onLoaded(InitializationContext context) {
        credits.setPosition(game.getWindow().width() / 2f, 0);
        add(credits);
        showCredits();
    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }
}
