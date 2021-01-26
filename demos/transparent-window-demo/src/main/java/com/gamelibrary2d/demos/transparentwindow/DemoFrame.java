package com.gamelibrary2d.demos.transparentwindow;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.imaging.AnimationLoader;
import com.gamelibrary2d.imaging.StandardAnimationFormats;
import com.gamelibrary2d.objects.AnimatedGameObject;
import com.gamelibrary2d.objects.ComposableGameObject;
import com.gamelibrary2d.renderers.AnimationRenderer;
import com.gamelibrary2d.resources.Animation;
import com.gamelibrary2d.resources.AnimationFrame;

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

        var imageAnimation = AnimationLoader.load(animationUrl, StandardAnimationFormats.GIF);

        var animation = Animation.fromImageAnimation(
                imageAnimation,
                imageAnimation.getBounds()
                        .resize(Rectangle.create(1f, 1f))
                        .restrict(game.getWindow().getWidth(), game.getWindow().getHeight()),
                this);

        var frames = animation.getFrames();

        var updatedFrames = new ArrayList<AnimationFrame>(frames.size() * 2);
        updatedFrames.addAll(frames);
        Collections.reverse(updatedFrames);
        updatedFrames.addAll(frames);

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
