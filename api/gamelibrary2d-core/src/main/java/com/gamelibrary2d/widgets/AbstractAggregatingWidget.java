package com.gamelibrary2d.widgets;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.util.Projection;

public abstract class AbstractAggregatingWidget<T extends Renderable>
        extends AbstractGameObject<T> implements MouseAware {

    @Override
    public boolean mouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            T content = getContent();
            if (content instanceof MouseAware) {
                Point projected = Projection.projectTo(this, projectedX, projectedY);
                return ((MouseAware) content).mouseButtonDown(button, mods, x, y, projected.getX(), projected.getY());
            }
        }
        return false;
    }

    @Override
    public boolean mouseMove(float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            T content = getContent();
            if (content instanceof MouseAware) {
                Point projected = Projection.projectTo(this, projectedX, projectedY);
                return ((MouseAware) content).mouseMove(x, y, projected.getX(), projected.getY());
            }
        }
        return false;
    }

    @Override
    public void mouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            T content = getContent();
            if (content instanceof MouseAware) {
                Point projected = Projection.projectTo(this, projectedX, projectedY);
                ((MouseAware) content).mouseButtonReleased(button, mods, x, y, projected.getX(), projected.getY());
            }
        }
    }
}