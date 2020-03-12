package com.gamelibrary2d.collision;

import com.gamelibrary2d.common.Pool;
import com.gamelibrary2d.common.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Updates {@link Collidable} objects and performs collision detection. A collision is defined as the bounds of two
 * registered {@link Collidable} instances overlapping. {@link CollisionAware} instances are alerted of collisions
 * in order to perform more fine grained collision detection and/or decide the aftermath of the collision.
 */
public class CollisionDetection<T extends Collidable> {
    private final Pool<InternalQuadTreeNode> nodePool = new Pool<>();
    private final ArrayList<CollisionDetectionArea> collisionDetectionAreas = new ArrayList<>();
    private final ArrayList<T> collidable;
    private final InternalQuadTreeNode rootNode;
    private final ArrayList<T> updateList;
    private final List<T> updateListReadOnly;

    /**
     * Creates a new {@link CollisionDetection} instance.
     *
     * @param area         The collision detection area.
     * @param minNodeWidth The minimum node width of the collision quad tree.
     * @param nodeCapacity The object capacity of each node. When exceeded, the node will split into child nodes (quadrants).
     */
    public CollisionDetection(Rectangle area, float minNodeWidth, int nodeCapacity) {
        collidable = new ArrayList<>();
        rootNode = new InternalQuadTreeNode(nodePool);
        updateList = new ArrayList<>();
        updateListReadOnly = Collections.unmodifiableList(updateList);
        rootNode.setBounds(area.xMin(), area.yMin(), area.xMax(), area.yMax());
        rootNode.setMaxDepth((int) Math.round(Math.log(area.width() / minNodeWidth) / Math.log(2)));
        rootNode.setCapacity(nodeCapacity);
    }

    /**
     * Registers the object for automatic updating and collision detection.
     *
     * @param collidable The object to register.
     */
    public void add(T collidable) {
        this.collidable.add(collidable);
    }

    /**
     * Unregisters the object for automatic updating and collision detection.
     *
     * @param collidable The object to unregister.
     * @return True if the object was unregistered, false otherwise.
     */
    public boolean remove(T collidable) {
        return this.collidable.remove(collidable);
    }

    /**
     * Clears registered objects.
     */
    public void clear() {
        collidable.clear();
    }

    /**
     * @return A modifiable list of restrictive {@link CollisionDetectionArea collision detection areas}. It is safe to move the areas
     * as they are repositioned in the collision quad tree each update. Collision detection will only be performed for
     * {@link Collidable} objects inside one or more area. Objects near the edges can be collided with but are not
     * updated. This is to avoid undetected collisions with objects past the edges. If the list of areas is empty,
     * all objects in the quad tree will be updated and checked for collisions.
     */
    public List<CollisionDetectionArea> getCollisionDetectionAreas() {
        return collisionDetectionAreas;
    }

    protected boolean addIfDetected(T collidable) {
        if (rootNode.insertIfDetected(collidable)) {
            updateList.add(collidable);
            return true;
        }
        return false;
    }

    private void addIfDetected(ArrayList<T> objects) {
        int size = objects.size();
        for (int i = 0; i < size; ++i) {
            addIfDetected(objects.get(i));
        }
    }

    /**
     * Updates all registered objects and performs collision detection.
     *
     * @param deltaTime The time since the last update, in seconds.
     */
    public List<T> update(float deltaTime) {
        rootNode.clear();
        nodePool.reset(0);
        updateList.clear();

        for (int i = 0; i < collisionDetectionAreas.size(); ++i) {
            rootNode.insertDetectionArea(collisionDetectionAreas.get(i));
        }

        int size;
        if (!collisionDetectionAreas.isEmpty()) {
            addIfDetected(collidable);
            size = updateList.size();
        } else {
            updateList.addAll(collidable);
            size = updateList.size();
            for (int i = 0; i < size; ++i) {
                rootNode.insert(updateList.get(i));
            }
        }

        for (int i = 0; i < size; ++i) {
            var obj = updateList.get(i);
            rootNode.update(obj, deltaTime);
            obj.updated();
        }

        return updateListReadOnly;
    }
}