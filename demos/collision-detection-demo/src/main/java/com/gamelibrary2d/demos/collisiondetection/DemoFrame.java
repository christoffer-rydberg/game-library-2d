package com.gamelibrary2d.demos.collisiondetection;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.collision.CollisionDetection;
import com.gamelibrary2d.collision.handlers.BounceHandler;
import com.gamelibrary2d.collision.handlers.CollisionHandler;
import com.gamelibrary2d.collision.handlers.RestrictedAreaHandler;
import com.gamelibrary2d.collision.handlers.UpdatedHandler;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.frames.InitializationContext;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Surface;
import com.gamelibrary2d.resources.Texture;
import com.gamelibrary2d.updaters.InfiniteUpdater;
import com.gamelibrary2d.util.RenderSettings;

import java.io.IOException;

public class DemoFrame extends AbstractFrame {
    private final CollisionDetection collisionDetection;
    private final Rectangle gameArea;
    private final UpdatedHandler<Ball> restrictedAreaHandler;
    private final CollisionHandler<Ball, Ball> bounceHandler = new BounceHandler<>(Ball.class);
    private final Layer<Ball> ballLayer = new BasicLayer<>();
    private Surface ballSurface;
    private Renderer ballRenderer;
    private BallTool tool;

    DemoFrame(Game game) {
        gameArea = Rectangle.fromBottomLeft(game.getWindow().width(), game.getWindow().height());
        restrictedAreaHandler = new RestrictedAreaHandler<>(gameArea, Ball::accelerate);
        collisionDetection = new CollisionDetection(gameArea, 128, 10);
    }

    @Override
    protected void onInitialize(InitializationContext context) throws IOException {
        Texture ballTexture = Texture.create(Ball.class.getClassLoader().getResource("ball.png"), this);
        ballSurface = Quad.create(Rectangle.centered(32, 32), this);
        ballRenderer = new SurfaceRenderer(ballSurface, ballTexture);
        ballRenderer.updateSettings(RenderSettings.COLOR_R, 152f / 255f, 251f / 255f, 152f / 255f);
        tool = BallTool.create(this, ballRenderer, this::addBall);
    }

    @Override
    protected void onLoad(InitializationContext context) {

    }

    private void addBall(Ball ball) {
        ballLayer.add(ball);
        collisionDetection.add(ball, restrictedAreaHandler, bounceHandler);
    }

    private void addBall(float x, float y) {
        addBall(new Ball(ballRenderer, x, y));
    }

    @Override
    protected void onLoaded(InitializationContext context) {
        final var ballWidth = ballSurface.getBounds().width();
        final var ballHeight = ballSurface.getBounds().height();
        final float pyramidCenterX = gameArea.width() / 2f;
        final float pyramidBaseY = 2f * gameArea.height() / 3f;

        // 1st row
        addBall(pyramidCenterX - 2 * ballWidth, pyramidBaseY);
        addBall(pyramidCenterX - ballWidth, pyramidBaseY);
        addBall(pyramidCenterX, pyramidBaseY);
        addBall(pyramidCenterX + ballWidth, pyramidBaseY);
        addBall(pyramidCenterX + 2 * ballWidth, pyramidBaseY);

        // 2nd row
        addBall(pyramidCenterX - ballWidth * 1.5f, pyramidBaseY - ballHeight * 0.9f);
        addBall(pyramidCenterX - ballWidth * 0.5f, pyramidBaseY - ballHeight * 0.9f);
        addBall(pyramidCenterX + ballWidth * 0.5f, pyramidBaseY - ballHeight * 0.9f);
        addBall(pyramidCenterX + ballWidth * 1.5f, pyramidBaseY - ballHeight * 0.9f);

        // 3nd row
        addBall(pyramidCenterX - ballWidth, pyramidBaseY - ballHeight * 0.9f * 2);
        addBall(pyramidCenterX, pyramidBaseY - ballHeight * 0.9f * 2);
        addBall(pyramidCenterX + ballWidth, pyramidBaseY - ballHeight * 0.9f * 2);

        // 4th row
        addBall(pyramidCenterX - ballWidth * 0.5f, pyramidBaseY - ballHeight * 0.9f * 3);
        addBall(pyramidCenterX + ballWidth * 0.5f, pyramidBaseY - ballHeight * 0.9f * 3);

        // 5th row
        addBall(pyramidCenterX, pyramidBaseY - ballHeight * 0.9f * 4);

        add(ballLayer);
        add(tool);

        runUpdater(new InfiniteUpdater(collisionDetection::update));
    }

    @Override
    protected void onBegin() {

    }

    @Override
    protected void onEnd() {

    }
}