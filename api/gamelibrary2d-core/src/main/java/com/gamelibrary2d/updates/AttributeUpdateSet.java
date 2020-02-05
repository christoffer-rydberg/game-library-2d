package com.gamelibrary2d.updates;

import com.gamelibrary2d.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link AttributeUpdateSet} is used to groups several
 * {@link AttributeUpdate attribute updates} into one.
 *
 * @author Christoffer Rydberg
 */
public class AttributeUpdateSet implements AttributeUpdate {

    private final List<AttributeUpdate> targetUpdates;

    public AttributeUpdateSet(AttributeUpdate u) {
        this.targetUpdates = new ArrayList<>(1);
        targetUpdates.add(u);
    }

    public AttributeUpdateSet(AttributeUpdate u, AttributeUpdate u2) {
        this.targetUpdates = new ArrayList<>(2);
        targetUpdates.add(u);
        targetUpdates.add(u2);
    }

    public AttributeUpdateSet(AttributeUpdate u, AttributeUpdate u2, AttributeUpdate u3) {
        this.targetUpdates = new ArrayList<>(3);
        targetUpdates.add(u);
        targetUpdates.add(u2);
        targetUpdates.add(u3);
    }

    public AttributeUpdateSet(AttributeUpdate u, AttributeUpdate u2, AttributeUpdate u3, AttributeUpdate u4) {
        this.targetUpdates = new ArrayList<>(4);
        targetUpdates.add(u);
        targetUpdates.add(u2);
        targetUpdates.add(u3);
        targetUpdates.add(u4);
    }

    public AttributeUpdateSet(List<AttributeUpdate> targetUpdates) {
        this.targetUpdates = targetUpdates;
    }

    @Override
    public void apply(float deltaTime, float scaledDeltaTime) {
        for (int i = 0; i < targetUpdates.size(); ++i) {
            targetUpdates.get(i).apply(deltaTime, scaledDeltaTime);
        }
    }

    @Override
    public void makeRelative(GameObject goal) {
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