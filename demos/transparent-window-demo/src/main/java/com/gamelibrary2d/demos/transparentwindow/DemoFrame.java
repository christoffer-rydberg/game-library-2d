package com.gamelibrary2d.demos.transparentwindow;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.animation.*;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.objects.ComposableObject;
import com.gamelibrary2d.renderers.AnimationRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DemoFrame extends AbstractFrame {

    private final Game game;
    private ComposableObject<AnimationRenderer> animationObj;

    DemoFrame(Game game) {
        this.game = game;
    }

    private Animation createAnimation() throws IOException {
        var animation = AnimationFactory.create(DemoFrame.class.getResource("/Images/homer.gif"),
                AnimationFormats.GIF, Rectangle.centered(1f, 1f), AnimationFactory.NO_CONSTRAINTS, this);
        var frames = animation.getFrames();
        var frameUpdate = new ArrayList<AnimationFrame>();
        frames.forEach(frameUpdate::add);
        Collections.reverse(frameUpdate);
        frames.forEach(frameUpdate::add);
        return new Animation(frameUpdate, animation.getBounds());
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        var window = game.getWindow();
        try {
            var animation = createAnimation();
            animationObj = new AnimatedObject<>(new AnimationRenderer(animation, false, this));
            animationObj.setPosition(window.width() / 2f, window.height() / 2f);
            add(animationObj);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void handleUpdate(float deltaTime) {
        super.handleUpdate(deltaTime);
        if (animationObj.getContent().isAnimationFinished()) {
            game.exit();
        }
    }
}
