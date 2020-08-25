package com.gamelibrary2d.widgets;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.util.Projection;

public abstract class AbstractAggregatingWidget<T extends Renderable>
        extends AbstractGameObject<T> implements MouseAware {

    @Override
    public boolean mouseButtonDown(int button, int mods, float x, float y) {
        if (isEnabled()) {
            var content = getContent();
            if (content instanceof MouseAware) {
                var projected = Projection.projectTo(this, x, y);
                return ((MouseAware) content).mouseButtonDown(button, mods, projected.getX(), projected.getY());
            }
        }
        return false;
    }

    @Override
    public boolean mouseMove(float x, float y) {
        if (isEnabled()) {
            var content = getContent();
            if (content instanceof MouseAware) {
                var projected = Projection.projectTo(this, x, y);
                return ((MouseAware) content).mouseMove(projected.getX(), projected.getY());
            }
        }
        return false;
    }

    @Override
    public void mouseButtonReleased(int button, int mods, float x, float y) {
        if (isEnabled()) {
            var content = getContent();
            if (content instanceof MouseAware) {
                var projected = Projection.projectTo(this, x, y);
                ((MouseAware) content).mouseButtonReleased(button, mods, projected.getX(), projected.getY());
            }
        }
    }
}