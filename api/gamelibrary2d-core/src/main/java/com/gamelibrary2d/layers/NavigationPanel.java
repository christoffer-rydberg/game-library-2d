package com.gamelibrary2d.layers;

import com.gamelibrary2d.markers.Clearable;
import com.gamelibrary2d.markers.MouseAware;
import com.gamelibrary2d.markers.NavigationAware;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.objects.GameObject;
import com.gamelibrary2d.util.Projection;

import java.util.ArrayDeque;
import java.util.Deque;

public class NavigationPanel extends AbstractGameObject<GameObject> implements MouseAware, Clearable {

    private final Deque<GameObject> previous = new ArrayDeque<>();

    public void navigateTo(GameObject content, boolean rememberPrevious) {
        var previousContent = getContent();
        if (previousContent != content) {
            setContent(content);
            if (rememberPrevious && previousContent != null) {
                previous.addLast(previousContent);
            }
            navigatedFrom(previousContent);
            navigatedTo(content);
        }
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
    public boolean onMouseButtonDown(int button, int mods, float x, float y) {
        var content = getContent();
        if (content instanceof MouseAware) {
            var projected = Projection.projectTo(this, x, y);
            return ((MouseAware) content).onMouseButtonDown(button, mods, projected.getX(), projected.getY());
        }

        return false;
    }

    @Override
    public boolean onMouseMove(float x, float y) {
        var content = getContent();
        if (content instanceof MouseAware) {
            var projected = Projection.projectTo(this, x, y);
            return ((MouseAware) content).onMouseMove(projected.getX(), projected.getY());
        }

        return false;
    }

    @Override
    public void onMouseButtonRelease(int button, int mods, float x, float y) {
        var content = getContent();
        if (content instanceof MouseAware) {
            var projected = Projection.projectTo(this, x, y);
            ((MouseAware) content).onMouseButtonRelease(button, mods, projected.getX(), projected.getY());
        }
    }

    @Override
    public boolean isAutoClearing() {
        return true;
    }

    private void navigatedFrom(GameObject obj) {
        var navigationAware = asNavigationAware(obj);
        if (navigationAware != null) {
            navigationAware.onNavigatedFrom(this);
        }
    }

    private void navigatedTo(GameObject obj) {
        var navigationAware = asNavigationAware(obj);
        if (navigationAware != null) {
            navigationAware.onNavigatedTo(this);
        }
    }

    private NavigationAware asNavigationAware(GameObject obj) {
        return obj instanceof NavigationAware ? (NavigationAware) obj : null;
    }
}