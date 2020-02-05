package com.gamelibrary2d.demos.gifviewer;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.animation.AnimatedObject;
import com.gamelibrary2d.animation.AnimationFactory;
import com.gamelibrary2d.animation.AnimationFormats;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.objects.ObservableObject;
import com.gamelibrary2d.renderable.Label;
import com.gamelibrary2d.renderers.AnimationRenderer;
import com.gamelibrary2d.renderers.TextRenderer;
import com.gamelibrary2d.resources.Font;
import com.gamelibrary2d.util.io.FileChooser;

import java.io.IOException;

public class DemoFrame extends AbstractFrame {
    private AnimatedObject animatedObject;

    DemoFrame(Game game) {
        super(game);
    }

    private GameObject createLoadButton() {
        var font = Font.create(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 48), this);

        var loadButtonContext = new Label();
        loadButtonContext.setTextRenderer(new TextRenderer(font));
        loadButtonContext.setFontColor(Color.WHITE);
        loadButtonContext.setText("Click here to load GIF");
        var textBounds = font.textSize(loadButtonContext.getText(), loadButtonContext.getHorizontalAlignment(), loadButtonContext.getVerticalAlignment());

        var loadButton = new ObservableObject<>();
        loadButton.setContent(loadButtonContext);
        loadButton.setBounds(textBounds);
        loadButton.addMouseButtonReleaseListener(this::onLoadButtonClicked);

        return loadButton;
    }

    @Override
    protected void onPrepare() {
        final float windowWidth = getGame().getWindow().getWidth();
        final float windowHeight = getGame().getWindow().getHeight();

        var loadButton = createLoadButton();
        loadButton.getPosition().set(windowWidth / 2, windowHeight - windowHeight / 6);

        animatedObject = new AnimatedObject();
        animatedObject.getPosition().set(windowWidth / 2, windowHeight / 2);

        add(animatedObject);
        add(loadButton);
    }

    @Override
    protected void onLoad() {

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
