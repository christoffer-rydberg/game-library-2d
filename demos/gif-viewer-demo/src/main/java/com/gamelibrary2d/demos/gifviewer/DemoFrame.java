package com.gamelibrary2d.demos.gifviewer;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.animation.AnimatedObject;
import com.gamelibrary2d.animation.AnimationFactory;
import com.gamelibrary2d.animation.AnimationFormats;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.AnimationRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.util.io.FileChooser;
import com.gamelibrary2d.widgets.DefaultWidget;
import com.gamelibrary2d.widgets.Label;

import java.io.IOException;

public class DemoFrame extends AbstractFrame {
    private final Game game;
    private AnimatedObject<AnimationRenderer> animatedObject;

    DemoFrame(Game game) {
        this.game = game;
    }

    private GameObject createLoadButton() {
        var font = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 48), this);

        var loadButtonContext = new Label();
        loadButtonContext.setTextRenderer(new TextRenderer(font));
        loadButtonContext.setFontColor(Color.WHITE);
        loadButtonContext.setText("Click here to load GIF");
        var textBounds = font.textSize(loadButtonContext.getText(), loadButtonContext.getHorizontalAlignment(), loadButtonContext.getVerticalAlignment());

        var loadButton = new DefaultWidget<>();
        loadButton.setContent(loadButtonContext);
        loadButton.setBounds(textBounds);
        loadButton.addMouseButtonReleasedListener(this::onLoadButtonClicked);

        return loadButton;
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        final float windowWidth = game.getWindow().width();
        final float windowHeight = game.getWindow().height();

        var loadButton = createLoadButton();
        loadButton.setPosition(windowWidth / 2, windowHeight - windowHeight / 6);

        animatedObject = new AnimatedObject<>();
        animatedObject.setPosition(windowWidth / 2, windowHeight / 2);

        add(animatedObject);
        add(loadButton);
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    @Override
    protected void onLoaded(InitializationContext context) {

    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }

    private void selectGif() throws IOException {
        var fileChooser = new FileChooser(System.getenv("TEMP") + "/ParticleGenerator/particle_path.txt");
        var path = fileChooser.browse();
        if (path != null) {
            final float windowWidth = game.getWindow().width();
            final float windowHeight = game.getWindow().height();
            var animation = AnimationFactory.create(path.toURI().toURL(), AnimationFormats.GIF, Rectangle.centered(1f, 1f), new Point(windowWidth, windowHeight), this);
            var renderer = new AnimationRenderer(animation, true, this);
            animatedObject.setRenderer(renderer);
        }
    }

    private void onLoadButtonClicked(int button, int mods, float projectedX, float projectedY) {
        try {
            selectGif();
        } catch (IOException e) {
            game.exit();
        }
    }
}
