package com.gamelibrary2d.updates;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link AttributeUpdateSet} is used to groups several
 * {@link AttributeUpdate attribute updates} into one.
 */
public class AttributeUpdateSet<T> implements AttributeUpdate<T> {

    private final List<AttributeUpdate<T>> targetUpdates;

    public AttributeUpdateSet(AttributeUpdate<T> u) {
        this.targetUpdates = new ArrayList<>(1);
        targetUpdates.add(u);
    }

    public AttributeUpdateSet(AttributeUpdate<T> u, AttributeUpdate<T> u2) {
        this.targetUpdates = new ArrayList<>(2);
        targetUpdates.add(u);
        targetUpdates.add(u2);
    }

    public AttributeUpdateSet(AttributeUpdate<T> u, AttributeUpdate<T> u2, AttributeUpdate<T> u3) {
        this.targetUpdates = new ArrayList<>(3);
        targetUpdates.add(u);
        targetUpdates.add(u2);
        targetUpdates.add(u3);
    }

    public AttributeUpdateSet(AttributeUpdate<T> u, AttributeUpdate<T> u2, AttributeUpdate<T> u3, AttributeUpdate<T> u4) {
        this.targetUpdates = new ArrayList<>(4);
        targetUpdates.add(u);
        targetUpdates.add(u2);
        targetUpdates.add(u3);
        targetUpdates.add(u4);
    }

    public AttributeUpdateSet(List<AttributeUpdate<T>> targetUpdates) {
        this.targetUpdates = targetUpdates;
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < targetUpdates.size(); ++i) {
            targetUpdates.get(i).update(deltaTime);
        }
    }

    @Override
    public void makeRelative(T goal) {
        for (int i = 0; i < targetUpdates.size(); ++i) {
            targetUpdates.get(i).makeRelative(goal);
        }
    }

    @Override
    public void makeAbsolute() {
        for (int i = 0; i < targetUpdates.size(); ++i) {
            targetUpdates.get(i).makeAbsolute();
        }
    }
}