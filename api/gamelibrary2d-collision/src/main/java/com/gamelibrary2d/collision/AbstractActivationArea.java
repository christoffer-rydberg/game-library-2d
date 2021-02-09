package com.gamelibrary2d.collision;

import com.gamelibrary2d.common.Rectangle;

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
        Rectangle collidableBounds = collidable.getBounds();
        float collidablePosX = collidable.getPosX();
        float collidablePosY = collidable.getPosY();

        Rectangle bounds = getBounds();
        float posX = getPosX();
        float posY = getPosY();
        float shrinkX = collidableBounds.getWidth() + maxMovement;
        float shrinkY = collidableBounds.getHeight() + maxMovement;

        boolean activated =
                !(bounds.getLowerX() + shrinkX + posX > collidableBounds.getUpperX() + collidablePosX
                        || bounds.getLowerY() + shrinkY + posY > collidableBounds.getUpperY() + collidablePosY
                        || bounds.getUpperX() - shrinkX + posX < collidableBounds.getLowerX() + collidablePosX
                        || bounds.getUpperY() - shrinkY + posY < collidableBounds.getLowerY() + collidablePosY);

        return activated ? activatedResult : ActivationResult.NEAR_EDGE;
    }
}
