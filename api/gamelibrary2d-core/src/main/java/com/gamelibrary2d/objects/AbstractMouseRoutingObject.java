package com.gamelibrary2d.objects;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.util.Projection;

public class AbstractMouseRoutingObject<T extends Renderable> extends AbstractGameObject<T> implements MouseAware {

    @Override
    public boolean onMouseButtonDown(int button, int mods, float x, float y) {
        if (isEnabled()) {
            var content = getContent();
            if (content instanceof MouseAware) {
                var projected = Projection.projectTo(this, x, y);
                return ((MouseAware) content).onMouseButtonDown(button, mods, projected.getX(), projected.getY());
            }
        }
        return false;
    }

    @Override
    public boolean onMouseMove(float x, float y) {
        if (isEnabled()) {
            var content = getContent();
            if (content instanceof MouseAware) {
                var projected = Projection.projectTo(this, x, y);
                return ((MouseAware) content).onMouseMove(projected.getX(), projected.getY());
            }
        }
        return false;
    }

    @Override
    public void onMouseButtonRelease(int button, int mods, float x, float y) {
        if (isEnabled()) {
            var content = getContent();
            if (content instanceof MouseAware) {
                var projected = Projection.projectTo(this, x, y);
                ((MouseAware) content).onMouseButtonRelease(button, mods, projected.getX(), projected.getY());
            }
        }
    }
}
