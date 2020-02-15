package com.gamelibrary2d.collision;

public abstract class AbstractCollisionDetectionArea implements CollisionDetectionArea {

    private final float maxCollidableSpeed;

    private final ActivationResult activatedResult;

    protected AbstractCollisionDetectionArea(float maxCollidableSpeed, boolean continueSearchWhenActivated) {
        activatedResult = continueSearchWhenActivated ? ActivationResult.ACTIVATED_CONTINUE_SEARCH : ActivationResult.ACTIVATED;
        this.maxCollidableSpeed = maxCollidableSpeed;
    }

    @Override
    public ActivationResult activate(Collidable collidable) {
        var collidableBounds = collidable.getBounds();
        var collidablePosX = collidable.getPosX();
        var collidablePosY = collidable.getPosY();

        var bounds = getBounds();
        var posX = getPosX();
        var posY = getPosY();
        var shrinkX = collidableBounds.width() + maxCollidableSpeed;
        var shrinkY = collidableBounds.height() + maxCollidableSpeed;

        boolean activated =
                !(bounds.xMin() + shrinkX + posX > collidableBounds.xMax() + collidablePosX
                        || bounds.yMin() + shrinkY + posY > collidableBounds.yMax() + collidablePosY
                        || bounds.xMax() - shrinkX + posX < collidableBounds.xMin() + collidablePosX
                        || bounds.yMax() - shrinkY + posY < collidableBounds.yMin() + collidablePosY);

        return activated ? activatedResult : ActivationResult.NOT_ACTIVATED;
    }
}
