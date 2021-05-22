package com.gamelibrary2d.layers;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.markers.Clearable;
import com.gamelibrary2d.markers.NavigationAware;
import com.gamelibrary2d.markers.PointerAware;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.util.Projection;

import java.util.ArrayDeque;
import java.util.Deque;

public class NavigationPanel extends AbstractGameObject implements PointerAware, Clearable {
    private final Point projectionOutput = new Point();
    private final Deque<GameObject> previous = new ArrayDeque<>();

    private GameObject current;
    private Rectangle bounds;

    public void navigateTo(GameObject obj, boolean rememberPrevious) {
        GameObject previous = this.current;
        if (previous != obj) {
            this.current = obj;
            if (rememberPrevious && previous != null) {
                this.previous.addLast(previous);
            }
            navigatedFrom(previous);
            navigatedTo(obj);
        }
    }

    public void goBack() {
        goBack(1);
    }

    public void goBack(int steps) {
        GameObject previous = null;

        while (steps > 0 && !this.previous.isEmpty()) {
            --steps;
            previous = this.previous.pollLast();
        }

        if (previous != null)
            navigateTo(previous, false);
    }

    @Override
    public void clear() {
        navigateTo(null, false);
        previous.clear();
    }

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (current instanceof PointerAware) {
            Projection.projectTo(this, projectedX, projectedY, projectionOutput);
            return ((PointerAware) current).pointerDown(id, button, x, y, projectionOutput.getX(), projectionOutput.getY());
        }

        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (current instanceof PointerAware) {
            Projection.projectTo(this, projectedX, projectedY, projectionOutput);
            return ((PointerAware) current).pointerMove(id, x, y, projectionOutput.getX(), projectionOutput.getY());
        }

        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (current instanceof PointerAware) {
            Projection.projectTo(this, projectedX, projectedY, projectionOutput);
            ((PointerAware) current).pointerUp(id, button, x, y, projectionOutput.getX(), projectionOutput.getY());
        }
    }

    @Override
    public boolean isAutoClearing() {
        return true;
    }

    private void navigatedFrom(GameObject obj) {
        NavigationAware navigationAware = asNavigationAware(obj);
        if (navigationAware != null) {
            navigationAware.navigatedFrom(this);
        }
    }

    private void navigatedTo(GameObject obj) {
        NavigationAware navigationAware = asNavigationAware(obj);
        if (navigationAware != null) {
            navigationAware.navigatedTo(this);
        }
    }

    private NavigationAware asNavigationAware(GameObject obj) {
        return obj instanceof NavigationAware ? (NavigationAware) obj : null;
    }

    @Override
    protected void onRender(float alpha) {
        if (current != null) {
            current.render(alpha);
        }
    }

    @Override
    public Rectangle getBounds() {
        return bounds != null ? bounds : current.getBounds();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}