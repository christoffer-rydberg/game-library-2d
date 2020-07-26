package com.gamelibrary2d.demos.collisiondetection;

import com.gamelibrary2d.collision.CollisionAware;
import com.gamelibrary2d.collision.CollisionParameters;
import com.gamelibrary2d.collision.CollisionResult;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;
import com.gamelibrary2d.resources.Texture;

import java.io.IOException;

public class Ball extends AbstractGameObject implements CollisionAware<Ball> {
    private final Point velocity = new Point();

    private float speed;
    private float direction;

    public static Ball create(float posX, float posY, float direction, Disposer disposer) throws IOException {
        var ball = new Ball();
        ball.setPosition(posX, posY);
        var texture = Texture.create(Ball.class.getClassLoader().getResource("ball.png"), disposer);
        var quad = Quad.create(Rectangle.centered(32, 32), disposer);
        ball.setContent(new SurfaceRenderer(quad, texture));
        ball.speed = 100f;
        ball.direction = direction;
        return ball;
    }

    @Override
    public Class<Ball> getCollidableClass() {
        return Ball.class;
    }

    @Override
    public CollisionResult onCollision(Ball other, CollisionParameters params) {
        // Collect all collided balls.

        direction += 180;

        return CollisionResult.CONTINUE;
    }

    @Override
    public void update(float deltaTime) {
        velocity.set(0, speed);
        velocity.rotate(direction);
        getPosition().add(velocity.getX() * deltaTime, velocity.getY() * deltaTime);
    }

    @Override
    public boolean canCollide() {
        return true;
    }

    @Override
    public void updated() {
        // Reposition if collision.
        // Divide force on all collected balls (and counter-force on self).
    }
}
