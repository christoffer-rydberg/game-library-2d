package com.gamelibrary2d.widgets;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.PointerAware;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.util.Projection;

public abstract class AbstractAggregatingWidget<T extends Renderable>
        extends AbstractGameObject<T> implements PointerAware {

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            T content = getContent();
            if (content instanceof PointerAware) {
                Point projected = Projection.projectTo(this, projectedX, projectedY);
                return ((PointerAware) content).pointerDown(id, button, x, y, projected.getX(), projected.getY());
            }
        }
        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            T content = getContent();
            if (content instanceof PointerAware) {
                Point projected = Projection.projectTo(this, projectedX, projectedY);
                return ((PointerAware) content).pointerMove(id, x, y, projected.getX(), projected.getY());
            }
        }
        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (isEnabled()) {
            T content = getContent();
            if (content instanceof PointerAware) {
                Point projected = Projection.projectTo(this, projectedX, projectedY);
                ((PointerAware) content).pointerUp(id, button, x, y, projected.getX(), projected.getY());
            }
        }
    }
}