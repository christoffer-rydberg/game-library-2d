package com.gamelibrary2d.demos.gifviewer;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.animation.AnimationFactory;
import com.gamelibrary2d.animation.AnimationFormats;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.layers.AbstractFrame;
import com.gamelibrary2d.animation.AnimatedObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.objects.TextObject;
import com.gamelibrary2d.renderers.AnimationRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.util.io.FileChooser;

import java.io.IOException;

public class DemoFrame extends AbstractFrame {

    private final TextObject loadButton = new TextObject();
    private final AnimatedObject animatedObject = new AnimatedObject();

    DemoFrame(Game game) {
        super(game);
    }

    @Override
    protected void onPrepare() {
        final float windowWidth = getGame().getWindow().getWidth();
        final float windowHeight = getGame().getWindow().getHeight();

        animatedObject.getPosition().set(windowWidth / 2, windowHeight / 2);

        var font = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 48), this);
        loadButton.setListeningToMouseClickEvents(true);
        loadButton.setTextRenderer(new TextRenderer(font));
        loadButton.setFontColor(Color.WHITE);
        loadButton.setText("Click here to load GIF");
        loadButton.getPosition().set(windowWidth / 2, windowHeight - windowHeight / 6);
        var textBounds = font.textSize(loadButton.getText(), loadButton.getHorizontalAlignment(), loadButton.getVerticalAlignment());
        loadButton.setBounds(textBounds);
        loadButton.addMouseReleaseListener(this::onLoadButtonClicked);
    }

    @Override
    protected void onLoad() {
        add(animatedObject);
        add(loadButton);
    }

    @Override
    protected void onFinish() {

    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    protected void onReset() {

    }

    @Override
    protected void onDispose() {

    }

    private void selectGif() throws IOException {
        var fileChooser = new FileChooser(System.getenv("TEMP") + "/ParticleGenerator/particle_path.txt");
        var path = fileChooser.browse();
        if (path != null) {
            final float windowWidth = getGame().getWindow().getWidth();
            final float windowHeight = getGame().getWindow().getHeight();
            var animation = AnimationFactory.create(path.toURI().toURL(), AnimationFormats.GIF, Rectangle.centered(1f, 1f), new Point(windowWidth, windowHeight), this);
            var renderer = new AnimationRenderer(animation, true, this);
            animatedObject.setRenderer(renderer);
        }
    }

    private void onLoadButtonClicked(GameObject obj, int button, int mods, float projectedX, float projectedY) {
        try {
            selectGif();
        } catch (IOException e) {
            getGame().exit();
        }
    }
}
