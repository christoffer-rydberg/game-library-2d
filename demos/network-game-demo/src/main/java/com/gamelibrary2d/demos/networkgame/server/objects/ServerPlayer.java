package com.gamelibrary2d.demos.networkgame.server.objects;

import com.gamelibrary2d.collision.CollisionAware;
import com.gamelibrary2d.collision.UpdateResult;
import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.demos.networkgame.common.RotationDirection;
import com.gamelibrary2d.demos.networkgame.server.DemoGameLogic;
import com.gamelibrary2d.network.common.Communicator;

public class ServerPlayer extends AbstractDemoServerObject implements CollisionAware<ServerBoulder> {
    private final DemoGameLogic gameLogic;
    private final Point velocity;
    private final Rectangle gameBounds;
    private final Point beforeUpdate = new Point();
    private final Communicator communicator;

    private RotationDirection rotationDirection = RotationDirection.NONE;

    public ServerPlayer(DemoGameLogic gameLogic, Communicator communicator, Rectangle gameBounds, Rectangle bounds) {
        super(ObjectIdentifiers.PLAYER);
        this.gameLogic = gameLogic;
        this.communicator = communicator;
        this.gameBounds = gameBounds;
        this.setBounds(bounds);
        velocity = new Point();
        setRotation(RandomInstance.get().nextFloat() * 360f);
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        var isLocal = communicator.getOutgoing() == buffer;
        buffer.putBool(isLocal);
        super.serializeMessage(buffer);
    }

    @Override
    protected void onSerializeMessage(DataBuffer buffer) {

    }

    @Override
    public Class<ServerBoulder> getCollidableClass() {
        return ServerBoulder.class;
    }

    @Override
    public UpdateResult update(float deltaTime) {
        beforeUpdate.set(getPosition());
        updateRotation(deltaTime);
        getPosition().add(velocity.getX() * deltaTime, velocity.getY() * deltaTime);
        bounceIfOutside(gameBounds);
        return UpdateResult.MOVED;
    }

    private void updateRotation(float deltaTime) {
        switch (rotationDirection) {
            case NONE:
                break;
            case LEFT:
                setRotation(getRotation() - deltaTime * 180);
                break;
            case RIGHT:
                setRotation(getRotation() + deltaTime * 180);
                break;
        }
    }

    @Override
    protected void setRotation(float rotation) {
        super.setRotation(rotation);
        velocity.set(0f, 120f);
        velocity.rotate(getRotation());
    }

    @Override
    public void updated() {

    }

    @Override
    public boolean onCollisionWith(ServerBoulder other) {
        gameLogic.destroy(this);
        return false;
    }

    private void bounceIfOutside(Rectangle area) {
        var position = getPosition();
        var bounds = getBounds();
        if (position.getX() + bounds.xMax() > area.xMax()) {
            position.setX(area.xMax() - bounds.xMax());
            velocity.setX(-velocity.getX());
        } else if (position.getX() + bounds.xMin() < area.xMin()) {
            position.setX(area.xMin() - bounds.xMin());
            velocity.setX(-velocity.getX());
        }

        if (position.getY() + bounds.yMax() > area.yMax()) {
            position.setY(area.yMax() - bounds.yMax());
            velocity.setY(-velocity.getY());
        } else if (position.getY() + bounds.yMin() < area.yMin()) {
            position.setY(area.yMin() - bounds.yMin());
            velocity.setY(-velocity.getY());
        }
    }

    public void setRotation(RotationDirection rotationDirection) {
        this.rotationDirection = rotationDirection;
    }
}
