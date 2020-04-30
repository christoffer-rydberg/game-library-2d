package com.gamelibrary2d.objects;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.MouseAware;

public abstract class AbstractCursor<T extends Renderable> extends AbstractGameObject<T> implements MouseAware {
    private final Game game;

    public AbstractCursor(Game game, T content) {
        super(content);
        this.game = game;
    }

    @Override
    protected void onRenderProjected(float alpha) {
        if (game.hasCursorFocus()) {
            super.onRenderProjected(alpha);
        }
    }

    @Override
    public boolean onMouseButtonDown(int button, int mods, float x, float y) {
        onInteracted();
        return false;
    }

    @Override
    public boolean onMouseMove(float x, float y) {
        setPosition(x, y);
        onInteracted();
        return false;
    }

    @Override
    public void onMouseButtonReleased(int button, int mods, float x, float y) {
        onInteracted();
    }

    protected abstract void onInteracted();
}