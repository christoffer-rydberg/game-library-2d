package com.gamelibrary2d.objects;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.renderers.Renderer;

public abstract class AbstractCursor extends AbstractGameObject implements MouseAware {

    private final Game game;

    private Renderer renderer;

    private AbstractCursor(Game game) {
        this.game = game;
    }

    public AbstractCursor(Game game, Renderer renderer) {
        this(game);
        setRenderer(renderer);
    }

    @Override
    protected void onRenderProjected(float alpha) {
        if (game.hasCursorFocus() && renderer != null) {
            renderer.render(alpha);
        }
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean mouseButtonDownEvent(int button, int mods, float projectedX, float projectedY) {
        onInteracted();
        return false;
    }

    @Override
    public boolean mouseMoveEvent(float projectedX, float projectedY, boolean drag) {
        getPosition().set(projectedX, projectedY);
        onInteracted();
        return false;
    }

    @Override
    public boolean mouseButtonReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        onInteracted();
        return false;
    }

    protected abstract void onInteracted();
}