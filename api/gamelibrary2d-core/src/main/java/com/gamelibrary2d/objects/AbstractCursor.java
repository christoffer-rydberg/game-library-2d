package com.gamelibrary2d.objects;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.PointerAware;

public abstract class AbstractCursor<T extends Renderable> extends AbstractGameObject<T> implements PointerAware {
    private final Game game;
    private final int pointerId;

    public AbstractCursor(Game game, int pointerId, T content) {
        super(content);
        this.game = game;
        this.pointerId = pointerId;
    }

    @Override
    protected void onRenderProjected(float alpha) {
        if (game.hasPointerFocus(pointerId)) {
            super.onRenderProjected(alpha);
        }
    }

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (id == pointerId) {
            onInteracted();
        }

        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (id == pointerId) {
            setPosition(projectedX, projectedY);
            onInteracted();
        }

        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (id == pointerId) {
            onInteracted();
        }
    }

    protected abstract void onInteracted();
}