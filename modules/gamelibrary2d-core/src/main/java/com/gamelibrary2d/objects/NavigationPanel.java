package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.updating.Updatable;

import java.util.*;

public class NavigationPanel extends AbstractInputObject implements Clearable, Container<GameObject> {

    private final Deque<GameObject> previousContents = new ArrayDeque<>();

    private final List<GameObject> gameObjects = new ArrayList<>(1);

    private final List<GameObject> gameObjectsReadonly = Collections.unmodifiableList(gameObjects);

    private boolean autoClearing;

    public NavigationPanel() {
        setListeningToMouseClickEvents(true);
        setListeningToMouseDragEvents(true);
        setListeningToMouseHoverEvents(true);
    }

    public void navigateTo(GameObject content, boolean rememberPrevious) {
        var previousContent = getContent();
        if (previousContent == content)
            return;
        navigatedFrom(previousContent);
        navigatedTo(content);
        if (rememberPrevious && previousContent != null)
            previousContents.addLast(previousContent);
        setContent(content);
    }

    public GameObject getContent() {
        return gameObjects.size() > 0 ? gameObjects.get(0) : null;
    }

    public void setContent(GameObject content) {
        if (gameObjects.size() > 0)
            gameObjects.set(0, content);
        else
            gameObjects.add(content);
    }

    private MouseAware getContentAsInput() {
        var content = getContent();
        return content instanceof MouseAware ? (MouseAware) content : null;
    }

    public void goBack(int steps) {
        GameObject previous = null;

        while (steps > 0 && !previousContents.isEmpty()) {
            --steps;
            previous = previousContents.pollLast();
        }

        if (previous != null)
            navigateTo(previous, false);
    }

    public void clearPreviousContents() {
        previousContents.clear();
    }

    @Override
    public void update(float deltaTime) {
        var obj = getContent();
        if (obj instanceof Updatable && obj.isEnabled()) {
            ((Updatable) obj).update(deltaTime);
        }
    }

    @Override
    public void clear() {
        navigateTo(null, false);
        previousContents.clear();
    }

    @Override
    public boolean isAutoClearing() {
        return autoClearing;
    }

    public void setAutoClearing(boolean autoClearing) {
        this.autoClearing = autoClearing;
    }

    @Override
    protected boolean onMouseClickEvent(int button, int mods, float projectedX, float projectedY) {
        MouseAware content = getContentAsInput();
        return content != null && content.mouseButtonDownEvent(button, mods, projectedX, projectedY);
    }

    @Override
    protected boolean onMouseMoveEvent(float projectedX, float projectedY, boolean drag) {
        MouseAware content = getContentAsInput();
        return content != null && content.mouseMoveEvent(projectedX, projectedY, drag);
    }

    @Override
    protected void onMouseReleaseEvent(int button, int mods, float projectedX, float projectedY) {
        MouseAware content = getContentAsInput();
        if (content != null)
            content.mouseButtonReleaseEvent(button, mods, projectedX, projectedY);
    }

    @Override
    public void charInputEvent(char charInput) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyDownEvent(int key, int scanCode, boolean repeat, int mods) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleaseEvent(int key, int scanCode, int mods) {
        // TODO Auto-generated method stub

    }

    @Override
    public Rectangle getBounds() {
        var content = getContent();
        return content != null ? content.getBounds() : Rectangle.EMPTY;
    }

    @Override
    protected void onRender(float alpha) {
        var content = getContent();
        if (content != null && content.isEnabled())
            content.render(alpha);
    }

    private void navigatedFrom(GameObject obj) {
        NavigationAware navigationAware = asNavigationAware(obj);
        if (navigationAware == null)
            return;
        navigationAware.onNavigatedFrom(this);
    }

    private void navigatedTo(GameObject obj) {
        NavigationAware navigationAware = asNavigationAware(obj);
        if (navigationAware == null)
            return;
        navigationAware.onNavigatedTo(this);
    }

    private NavigationAware asNavigationAware(GameObject obj) {
        return obj instanceof NavigationAware ? (NavigationAware) obj : null;
    }

    @Override
    public List<GameObject> getObjects() {
        return gameObjectsReadonly;
    }

    @Override
    public int size() {
        return gameObjects.size();
    }
}