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
        var shrinkX = collidableBounds.getWidth() + maxCollidableSpeed;
        var shrinkY = collidableBounds.getHeight() + maxCollidableSpeed;

        boolean activated =
                !(bounds.getXMin() + shrinkX + posX > collidableBounds.getXMax() + collidablePosX
                        || bounds.getYMin() + shrinkY + posY > collidableBounds.getYMax() + collidablePosY
                        || bounds.getXMax() - shrinkX + posX < collidableBounds.getXMin() + collidablePosX
                        || bounds.getYMax() - shrinkY + posY < collidableBounds.getYMin() + collidablePosY);

        return activated ? activatedResult : ActivationResult.NOT_ACTIVATED;
    }
}
