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
        var shrinkX = collidableBounds.getWidth() + maxMovement;
        var shrinkY = collidableBounds.getHeight() + maxMovement;

        boolean activated =
                !(bounds.getLowerX() + shrinkX + posX > collidableBounds.getUpperX() + collidablePosX
                        || bounds.getLowerY() + shrinkY + posY > collidableBounds.getUpperY() + collidablePosY
                        || bounds.getUpperX() - shrinkX + posX < collidableBounds.getLowerX() + collidablePosX
                        || bounds.getUpperY() - shrinkY + posY < collidableBounds.getLowerY() + collidablePosY);

        return activated ? activatedResult : ActivationResult.NEAR_EDGE;
    }
}
