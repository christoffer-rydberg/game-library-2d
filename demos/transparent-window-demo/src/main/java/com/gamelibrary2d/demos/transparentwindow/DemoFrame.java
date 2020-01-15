package com.gamelibrary2d.demos.transparentwindow;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.animation.*;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.objects.AbstractFrame;
import com.gamelibrary2d.objects.AnimatedObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.renderers.AnimationRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DemoFrame extends AbstractFrame {

    DemoFrame(Game game) {
        super(game);
    }

    @Override
    protected void onPrepare() {
        var game = getGame();
        var window = game.getWindow();
        try {
            var animation = AnimationFactory.create(DemoFrame.class.getResource("/Images/homer.gif"),
                    AnimationFormats.GIF, Rectangle.centered(1f, 1f), AnimationFactory.NO_CONSTRAINTS, this);
            var frames = animation.getFrames();
            var frameUpdate = new ArrayList<AnimationFrame>();
            frames.forEach(frameUpdate::add);
            Collections.reverse(frameUpdate);
            frames.forEach(frameUpdate::add);

            var animatedRenderer = new AnimationRenderer(new Animation(frameUpdate, animation.getBounds()), false, this);

            GameObject animatedObject = new AnimatedObject(animatedRenderer);
            animatedObject.getPosition().set(window.getWidth() / 2f, window.getHeight() / 2f);
            add(animatedObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLoad() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onFinish() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onBegin() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onEnd() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onUpdate(float deltaTime) {
        var allAnimationsFinished = getObjects().stream()
                .filter(x -> x instanceof AnimatedObject)
                .map(x -> (AnimatedObject) x)
                .filter(x -> x.getRenderer() instanceof AnimationRenderer)
                .map(x -> (AnimationRenderer) x.getRenderer())
                .allMatch(AnimationRenderer::isAnimationFinished);

        if (allAnimationsFinished) {
            getGame().exit();
        }
    }

    @Override
    protected void onReset() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onDispose() {
        // TODO Auto-generated method stub
    }
}
