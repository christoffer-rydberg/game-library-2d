package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionDetection;
import com.gamelibrary2d.collision.Obstacle;
import com.gamelibrary2d.collision.handlers.BounceHandler;
import com.gamelibrary2d.collision.handlers.RestrictedAreaHandler;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.common.ObjectTypes;

public class ServerObstacle extends AbstractDemoServerObject implements Obstacle {

    public ServerObstacle(byte secondaryType, Rectangle bounds) {
        super(ObjectTypes.OBSTACLE, secondaryType);
        this.setBounds(bounds);
        setSpeedAndDirection(90f, RandomInstance.get().nextFloat() * 360f);
    }

    @Override
    public void addCollisionDetection(CollisionDetection collisionDetection) {
        collisionDetection.add(
                this,
                new RestrictedAreaHandler<>(collisionDetection.getBounds(), ServerObstacle::accelerate),
                new BounceHandler<>(Obstacle.class)
        );
    }

    @Override
    public float getMass() {
        return 1f;
    }

    @Override
    public void onPushed(Obstacle pusher, float accelerationX, float accelerationY) {
        accelerate(accelerationX, accelerationY);
    }

}
