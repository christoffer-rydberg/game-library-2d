package com.gamelibrary2d.tools.particlegenerator.objects;

import com.gamelibrary2d.layers.AbstractPanel;
import com.gamelibrary2d.objects.GameObject;

public class StackPanel extends AbstractPanel<GameObject> {

    private final float defaultMargin;
    private final Orientation orientation;
    public StackPanel(Orientation orientation, float defaultMargin) {
        this.orientation = orientation;
        this.defaultMargin = defaultMargin;
    }

    @Override
    public void add(GameObject item) {
        add(item, defaultMargin);
    }

    public void add(GameObject item, float margin) {
        positionObject(getChildren().size(), item, margin);
        super.add(item);
    }

    @Override
    public void add(int index, GameObject item) {
        add(index, item, defaultMargin);
    }

    public void add(int index, GameObject item, float margin) {
        positionObject(index, item, margin);
        pushObjects(index, 1);
        super.add(index, item);
    }

    @Override
    public boolean remove(Object item) {
        int index = indexOf(item);
        if (index == -1)
            return false;
        pushObjects(index + 1, -1);
        super.remove(index);
        return true;
    }

    @Override
    public void remove(int index) {
        pushObjects(index, -1);
        super.remove(index);
    }

    private void pushObjects(int startIndex, float offset) {
        for (int i = startIndex; i < getChildren().size(); ++i) {
            GameObject obj = get(i);
            if (orientation == Orientation.HORIZONTAL) {
                obj.position().set(obj.position().getX() + offset, obj.position().getY());
            } else {
                obj.position().set(obj.position().getX(), obj.position().getY() + offset);
            }
        }
    }

    private void positionObject(int index, GameObject obj, float margin) {
        if (orientation == Orientation.HORIZONTAL) {
            obj.position().set(positionX(index) + margin, obj.position().getY());
        } else {
            obj.position().set(obj.position().getX(), positionY(index) + margin);
        }
    }

    private float positionX(int index) {
        return index == 0 ? -defaultMargin : get(index - 1).position().getX();
    }

    private float positionY(int index) {
        return index == 0 ? -defaultMargin : get(index - 1).position().getY();
    }

    public enum Orientation {
        VERTICAL, HORIZONTAL
    }
}