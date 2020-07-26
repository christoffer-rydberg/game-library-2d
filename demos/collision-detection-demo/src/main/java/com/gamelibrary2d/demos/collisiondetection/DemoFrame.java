package com.gamelibrary2d.demos.collisiondetection;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.collision.CollisionDetection;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.updaters.InfiniteUpdater;

import java.io.IOException;

public class DemoFrame extends AbstractFrame {
    private final Game game;

    private final CollisionDetection<Ball> collisionDetection;

    DemoFrame(Game game) {
        this.game = game;
        this.collisionDetection = new CollisionDetection<>(
                Rectangle.fromBottomLeft(game.getWindow().width(), game.getWindow().height()),
                128,
                10);
    }

    @Override
    protected void onInitialize(InitializationContext context) {

    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    private void addBall(Ball ball) {
        add(ball);
        collisionDetection.add(ball);
    }

    @Override
    protected void onLoaded(InitializationContext context) throws IOException {
        addBall(Ball.create(400, 400, 180, this));
        addBall(Ball.create(400, 200, 0, this));
        runUpdater(new InfiniteUpdater(collisionDetection::update));
    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }
}
