package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionParameters;
import com.gamelibrary2d.collision.CollisionAware;
import com.gamelibrary2d.collision.CollisionResult;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.demos.networkgame.common.RotationDirection;
import com.gamelibrary2d.demos.networkgame.server.DemoGameLogic;
import com.gamelibrary2d.network.common.Communicator;

public class ServerPlayer extends AbstractDemoServerObject implements CollisionAware<ServerBoulder> {
    private final DemoGameLogic gameLogic;

    private final Communicator communicator;

    private RotationDirection rotationDirection = RotationDirection.NONE;

    public ServerPlayer(DemoGameLogic gameLogic, Communicator communicator, Rectangle gameBounds, Rectangle bounds) {
        super(ObjectIdentifiers.PLAYER, gameBounds);
        this.gameLogic = gameLogic;
        this.communicator = communicator;
        this.setBounds(bounds);
        setSpeed(100f);
        setDirection(RandomInstance.get().nextFloat() * 360f);
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        var isLocal = communicator.getOutgoing() == buffer;
        buffer.putBool(isLocal);
        super.serializeMessage(buffer);
    }

    @Override
    public Class<ServerBoulder> getCollidableClass() {
        return ServerBoulder.class;
    }

    @Override
    public void update(float deltaTime) {
        updateRotation(deltaTime);
        super.update(deltaTime);
    }

    private void updateRotation(float deltaTime) {
        switch (rotationDirection) {
            case NONE:
                break;
            case LEFT:
                setDirection(getDirection() - deltaTime * 180);
                break;
            case RIGHT:
                setDirection(getDirection() + deltaTime * 180);
                break;
        }
    }

    @Override
    public CollisionResult onCollision(ServerBoulder other, CollisionParameters params) {
        gameLogic.destroy(other);
        gameLogic.destroy(this);
        return CollisionResult.ABORT;
    }

    public void setRotationDirection(RotationDirection rotationDirection) {
        this.rotationDirection = rotationDirection;
    }
}
