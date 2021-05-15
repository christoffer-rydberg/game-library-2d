package com.gamelibrary2d.layers;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.markers.Clearable;
import com.gamelibrary2d.markers.NavigationAware;
import com.gamelibrary2d.markers.PointerAware;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.util.Projection;

import java.util.ArrayDeque;
import java.util.Deque;

public class NavigationPanel extends AbstractGameObject<GameObject> implements PointerAware, Clearable {

    private final Deque<GameObject> previous = new ArrayDeque<>();

    public void navigateTo(GameObject content, boolean rememberPrevious) {
        GameObject previousContent = getContent();
        if (previousContent != content) {
            setContent(content);
            if (rememberPrevious && previousContent != null) {
                previous.addLast(previousContent);
            }
            navigatedFrom(previousContent);
            navigatedTo(content);
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
        GameObject content = getContent();
        if (content instanceof PointerAware) {
            Point projected = Projection.projectTo(this, projectedX, projectedY);
            return ((PointerAware) content).pointerDown(id, button, x, y, projected.getX(), projected.getY());
        }

        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        GameObject content = getContent();
        if (content instanceof PointerAware) {
            Point projected = Projection.projectTo(this, projectedX, projectedY);
            return ((PointerAware) content).pointerMove(id, x, y, projected.getX(), projected.getY());
        }

        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        GameObject content = getContent();
        if (content instanceof PointerAware) {
            Point projected = Projection.projectTo(this, projectedX, projectedY);
            ((PointerAware) content).pointerUp(id, button, x, y, projected.getX(), projected.getY());
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
}