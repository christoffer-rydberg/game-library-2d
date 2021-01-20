package com.gamelibrary2d.demos.transparentwindow;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.animation.Animation;
import com.gamelibrary2d.animation.AnimationFrame;
import com.gamelibrary2d.animation.io.AnimationLoader;
import com.gamelibrary2d.animation.io.StandardAnimationFormats;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.objects.AnimatedGameObject;
import com.gamelibrary2d.objects.ComposableGameObject;
import com.gamelibrary2d.renderers.AnimationRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DemoFrame extends AbstractFrame {

    private final Game game;
    private ComposableGameObject<AnimationRenderer> animationObj;

    DemoFrame(Game game) {
        this.game = game;
    }

    private Animation createAnimation() throws IOException {
        var animationUrl = DemoFrame.class.getResource("/Images/homer.gif");
        var animationScale = Rectangle.create(1f, 1f);

        var animation = AnimationLoader
                .load(animationUrl, StandardAnimationFormats.GIF)
                .createAnimation(
                        animationScale,
                        game.getWindow().getWidth(),
                        game.getWindow().getHeight(),
                        this);

        var frames = animation.getFrames();

        var updatedFrames = new ArrayList<AnimationFrame>(frames.size() * 2);
        frames.forEach(updatedFrames::add);
        Collections.reverse(updatedFrames);
        frames.forEach(updatedFrames::add);

        return new Animation(updatedFrames);
    }

    @Override
    protected void onInitialize(InitializationContext context) {
        var window = game.getWindow();
        try {
            var animation = createAnimation();
            animationObj = new AnimatedGameObject<>(new AnimationRenderer(animation, false, this));
            animationObj.setPosition(window.getWidth() / 2f, window.getHeight() / 2f);
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
    protected void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        if (animationObj.getContent().isAnimationFinished()) {
            game.exit();
        }
    }
}
