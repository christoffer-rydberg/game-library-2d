package com.gamelibrary2d.collision;

import com.gamelibrary2d.common.Pool;
import com.gamelibrary2d.common.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;

class InternalQuadTreeNode {

    // Node constants
    private static final int CROSSING_BOUNDARIES = -1;
    private static final int TOP_LEFT_NODE = 0;
    private static final int TOP_RIGHT_NODE = 1;
    private static final int BOTTOM_LEFT_NODE = 2;
    private static final int BOTTOM_RIGHT_NODE = 3;

    // Activation constants
    private static final int ACTIVATED_STOP_SEARCH = 3;
    private static final int ACTIVATED = 2;
    private static final int NEAR_EDGE = 1;
    private static final int NOT_ACTIVATED = 0;

    private final Pool<InternalQuadTreeNode> nodePool;
    private final ArrayList<InternalCollidableWrapper> objects;
    private final ArrayList<ActivationArea> activationAreas;
    private final InternalQuadTreeNode[] nodes;
    private int depth;
    private int maxDepth;
    private int capacity;
    private float xMin, yMin, xMax, yMax;

    InternalQuadTreeNode(Pool<InternalQuadTreeNode> nodePool) {
        this.nodePool = nodePool;
        objects = new ArrayList<>();
        activationAreas = new ArrayList<>();
        nodes = new InternalQuadTreeNode[4];
    }

    private static boolean isColliding(Rectangle bounds, float xPos, float yPos, Rectangle bounds2, float xPos2, float yPos2) {
        return !(bounds.getLowerX() + xPos > bounds2.getUpperX() + xPos2
                || bounds.getLowerY() + yPos > bounds2.getUpperY() + yPos2
                || bounds.getUpperX() + xPos < bounds2.getLowerX() + xPos2
                || bounds.getUpperY() + yPos < bounds2.getLowerY() + yPos2);
    }

    private static boolean isColliding(InternalArea updated, InternalArea other) {
        return isColliding(updated.getBounds(), updated.getPosX(), updated.getPosY(), other.getBounds(), other.getPosX(),
                other.getPosY());
    }

    private InternalQuadTreeNode createNode(int depth, float xMin, float yMin, float xMax, float yMax) {
        InternalQuadTreeNode qt = nodePool.canGet() ? nodePool.get() : nodePool.store(new InternalQuadTreeNode(nodePool));
        qt.depth = depth;
        qt.maxDepth = maxDepth;
        qt.capacity = capacity;
        qt.xMin = xMin;
        qt.yMin = yMin;
        qt.xMax = xMax;
        qt.yMax = yMax;
        return qt;
    }

    public void setBounds(float xMin, float yMin, float xMax, float yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    void clear() {
        objects.clear();
        activationAreas.clear();
        for (int i = 0; i < 4; ++i) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private int getNodeIndex(InternalArea area) {
        Rectangle bounds = area.getBounds();
        float xMin = bounds.getLowerX();
        float yMin = bounds.getLowerY();
        float xMax = bounds.getUpperX();
        float yMax = bounds.getUpperY();

        float verticalMidpoint = (xMin + xMax) / 2 - area.getPosX();
        float horizontalMidpoint = (yMin + yMax) / 2 - area.getPosY();

        if (xMax < verticalMidpoint) {
            if (yMin > horizontalMidpoint) {
                return TOP_LEFT_NODE; // Top left node
            } else if (yMax < horizontalMidpoint) {
                return BOTTOM_LEFT_NODE; // Bottom left node
            }
        } else if (xMin > verticalMidpoint) {
            if (yMin > horizontalMidpoint) {
                return TOP_RIGHT_NODE; // Top right node
            } else if (yMax < horizontalMidpoint) {
                return BOTTOM_RIGHT_NODE; // Bottom right node
            }
        }

        return CROSSING_BOUNDARIES;
    }

    private void split(ArrayList<?> objects, boolean activationAreas) {
        float yMid = (yMin + yMax) / 2;
        float xMid = (xMin + xMax) / 2;

        int nextDepth = depth + 1;
        nodes[0] = createNode(nextDepth, xMin, yMid, xMid, yMax);
        nodes[1] = createNode(nextDepth, xMid, yMid, xMax, yMax);
        nodes[2] = createNode(nextDepth, xMin, yMin, xMid, yMid);
        nodes[3] = createNode(nextDepth, xMid, yMin, xMax, yMid);

        Iterator<?> it = objects.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            it.remove();
            if (activationAreas) {
                ActivationArea activationArea = (ActivationArea) next;
                int index = getNodeIndex(activationArea);
                if (index == CROSSING_BOUNDARIES)
                    continue;
                nodes[index].insertActivationAreaHere(activationArea);
            } else {
                InternalCollidableWrapper obj = (InternalCollidableWrapper) next;
                int index = getNodeIndex(obj.collidable);
                if (index == CROSSING_BOUNDARIES)
                    continue;
                nodes[index].insertHere(obj);
            }
        }
    }

    private void insertHere(InternalCollidableWrapper obj) {
        objects.add(obj);
        if (nodes[0] == null && objects.size() > capacity && depth < maxDepth) {
            split(objects, false);
        }
    }

    private void insertActivationAreaHere(ActivationArea area) {
        activationAreas.add(area);
        if (nodes[0] == null && activationAreas.size() > capacity && depth < maxDepth) {
            split(activationAreas, true);
        }
    }

    private InternalQuadTreeNode getNode(InternalArea area) {
        if (hasNodes()) {
            int index = getNodeIndex(area);
            return index != CROSSING_BOUNDARIES ? nodes[index].getNode(area) : this;
        } else {
            return this;
        }
    }

    private int detectInCurrentNode(Collidable obj) {
        int result = NOT_ACTIVATED;
        int size = activationAreas.size();
        for (int i = 0; i < size; ++i) {
            ActivationArea activationArea = activationAreas.get(i);
            if (isColliding(activationArea, obj)) {
                ActivationResult activationResult = activationArea.onActivation(obj);
                switch (activationResult) {
                    case ACTIVATED:
                        return ACTIVATED_STOP_SEARCH;
                    case ACTIVATED_CONTINUE_SEARCH:
                        result = ACTIVATED;
                        break;
                    default:
                        result = Math.max(result, NEAR_EDGE);
                        break;
                }
            }
        }

        return result;
    }

    private int insertHelper(InternalCollidableWrapper obj, int detectionResult, boolean nodeFound) {
        detectionResult = detectionResult == ACTIVATED_STOP_SEARCH
                ? ACTIVATED_STOP_SEARCH
                : Math.max(detectionResult, detectInCurrentNode(obj.collidable));

        if (hasNodes()) {
            int nodeIndex = nodeFound ? CROSSING_BOUNDARIES : getNodeIndex(obj.collidable);
            if (nodeIndex != CROSSING_BOUNDARIES) {
                return nodes[nodeIndex].insertHelper(
                        obj,
                        detectionResult,
                        false);
            } else {
                int index = 0;
                while (detectionResult != ACTIVATED_STOP_SEARCH && index < 4) {
                    detectionResult = nodes[index].insertHelper(
                            obj,
                            detectionResult,
                            true);
                    ++index;
                }
            }
        }

        if (detectionResult != NOT_ACTIVATED && !nodeFound) {
            insertHere(obj);
        }

        return detectionResult;
    }

    InsertionResult insert(InternalCollidableWrapper obj) {
        int result = insertHelper(obj, NOT_ACTIVATED, false);
        if (result == ACTIVATED || result == ACTIVATED_STOP_SEARCH) {
            return InsertionResult.INSERTED_ACTIVE;
        } else if (result == NEAR_EDGE) {
            return InsertionResult.INSERTED_NEAR_EDGE;
        } else if (result == NOT_ACTIVATED) {
            return InsertionResult.NOT_INSERTED;
        } else {
            throw new IllegalStateException("Unknown result: " + result);
        }
    }

    void insertWithoutActivation(InternalCollidableWrapper obj) {
        getNode(obj.collidable).insertHere(obj);
    }

    void insertActivationArea(ActivationArea obj) {
        getNode(obj).insertActivationAreaHere(obj);
    }

    private boolean hasMoved(Collidable obj, Rectangle prevBounds, float prevX, float prevY) {
        return prevX != obj.getPosX()
                || prevY != obj.getPosY()
                || !prevBounds.equals(obj.getBounds());
    }

    private InternalQuadTreeNode updateNode(
            InternalCollidableWrapper obj,
            InternalQuadTreeNode prevNode,
            Rectangle prevBounds,
            float prevX,
            float prevY) {
        if (hasMoved(obj.collidable, prevBounds, prevX, prevY)) {
            InternalQuadTreeNode newNode = getNode(obj.collidable);
            if (prevNode != newNode) {
                prevNode.objects.remove(obj);
                newNode.insertHere(obj);
            }
            return newNode;
        }

        return prevNode;
    }

    public void update(InternalCollidableWrapper obj, float deltaTime) {
        InternalQuadTreeNode nodeBeforeUpdate = getNode(obj.collidable);

        obj.update(deltaTime);

        if (obj.collidable.canCollide()) {
            InternalQuadTreeNode nodeAfterUpdate = updateNode(
                    obj,
                    nodeBeforeUpdate,
                    obj.info.getPrevBounds(),
                    obj.info.getPrevX(),
                    obj.info.getPrevY());

            if (obj.isHandlingCollisions()) {
                obj.initializeCollisionHandlers();

                Rectangle boundsAfterUpdate = obj.collidable.getBounds();
                float posXAfterUpdate = obj.collidable.getPosX();
                float posYAfterUpdate = obj.collidable.getPosY();

                nodeAfterUpdate.handleCollisions(obj);

                updateNode(
                        obj,
                        nodeAfterUpdate,
                        boundsAfterUpdate,
                        posXAfterUpdate,
                        posYAfterUpdate);

                obj.finishCollisionHandlers();
            }
        }
    }

    private boolean hasNodes() {
        return nodes[0] != null;
    }

    private CollisionResult handleCollisions(InternalCollidableWrapper updated) {
        int size = objects.size();
        for (int i = 0; i < size; ++i) {
            InternalCollidableWrapper other = objects.get(i);
            Collidable collidable = other.collidable;
            if (updated.collidable != collidable && collidable.canCollide() && isColliding(updated.collidable, collidable)) {
                CollisionResult result = updated.handleCollision(other);
                if (result != CollisionResult.CONTINUE) {
                    return result;
                }
            }
        }

        if (hasNodes()) {
            int nodeIndex = getNodeIndex(updated.collidable);
            if (nodeIndex == CROSSING_BOUNDARIES) {
                for (int i = 0; i < 4; ++i) {
                    CollisionResult result = nodes[i].handleCollisions(updated);
                    if (result != CollisionResult.CONTINUE) {
                        return result;
                    }
                }
            } else {
                return nodes[nodeIndex].handleCollisions(updated);
            }
        }

        return CollisionResult.CONTINUE;
    }
}