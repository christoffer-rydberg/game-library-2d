package com.gamelibrary2d.collision;

public abstract class AbstractActivationArea implements ActivationArea {
    private final float maxMovement;
    private final ActivationResult activatedResult;

    protected AbstractActivationArea(float maxMovement, boolean continueSearchIfActivated) {
        this.maxMovement = maxMovement;
        activatedResult = continueSearchIfActivated
                ? ActivationResult.ACTIVATED_CONTINUE_SEARCH
                : ActivationResult.ACTIVATED;
    }

    @Override
    public ActivationResult onActivation(Collidable collidable) {
        var collidableBounds = collidable.getBounds();
        var collidablePosX = collidable.getPosX();
        var collidablePosY = collidable.getPosY();

        var bounds = getBounds();
        var posX = getPosX();
        var posY = getPosY();
        var shrinkX = collidableBounds.width() + maxMovement;
        var shrinkY = collidableBounds.height() + maxMovement;

        boolean activated =
                !(bounds.xMin() + shrinkX + posX > collidableBounds.xMax() + collidablePosX
                        || bounds.yMin() + shrinkY + posY > collidableBounds.yMax() + collidablePosY
                        || bounds.xMax() - shrinkX + posX < collidableBounds.xMin() + collidablePosX
                        || bounds.yMax() - shrinkY + posY < collidableBounds.yMin() + collidablePosY);

        return activated ? activatedResult : ActivationResult.NEAR_EDGE;
    }
}
