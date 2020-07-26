package com.gamelibrary2d.collision;

import com.gamelibrary2d.common.Pool;
import com.gamelibrary2d.common.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;

class InternalQuadTreeNode {

    // Quadrant constants
    private static final int CROSSING_BOUNDARIES = -1;
    private static final int TOP_LEFT_QUADRANT = 0;
    private static final int TOP_RIGHT_QUADRANT = 1;
    private static final int BOTTOM_LEFT_QUADRANT = 2;
    private static final int BOTTOM_RIGHT_QUADRANT = 3;

    // Activation constants
    private static final int ACTIVATED_STOP_SEARCH = 3;
    private static final int ACTIVATED = 2;
    private static final int NEAR_EDGE = 1;
    private static final int NOT_ACTIVATED = 0;

    private final Pool<InternalQuadTreeNode> nodePool;
    private final ArrayList<Collidable> objects;
    private final ArrayList<ActivationArea> activationAreas;
    private final InternalQuadTreeNode[] quadrants;
    private final CollisionParameters params = new CollisionParameters();
    private int depth;
    private int maxDepth;
    private int capacity;
    private float xMin, yMin, xMax, yMax;

    InternalQuadTreeNode(Pool<InternalQuadTreeNode> nodePool) {
        this.nodePool = nodePool;
        objects = new ArrayList<>();
        activationAreas = new ArrayList<>();
        quadrants = new InternalQuadTreeNode[4];
    }

    private static boolean isColliding(Rectangle bounds, float xPos, float yPos, Rectangle bounds2, float xPos2, float yPos2) {
        return !(bounds.xMin() + xPos > bounds2.xMax() + xPos2
                || bounds.yMin() + yPos > bounds2.yMax() + yPos2
                || bounds.xMax() + xPos < bounds2.xMin() + xPos2
                || bounds.yMax() + yPos < bounds2.yMin() + yPos2);
    }

    private static boolean isColliding(InternalCollidable updated, InternalCollidable other) {
        return isColliding(updated.getBounds(), updated.getPosX(), updated.getPosY(), other.getBounds(), other.getPosX(),
                other.getPosY());
    }

    private InternalQuadTreeNode createQuadrant(int depth, float xMin, float yMin, float xMax, float yMax) {
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
            if (quadrants[i] != null) {
                quadrants[i].clear();
                quadrants[i] = null;
            }
        }
    }

    private int getQuadrantIndex(InternalCollidable collidable) {
        var bounds = collidable.getBounds();
        float xMin = bounds.xMin();
        float yMin = bounds.yMin();
        float xMax = bounds.xMax();
        float yMax = bounds.yMax();

        float verticalMidpoint = (xMin + xMax) / 2 - collidable.getPosX();
        float horizontalMidpoint = (yMin + yMax) / 2 - collidable.getPosY();

        if (xMax < verticalMidpoint) {
            if (yMin > horizontalMidpoint) {
                return TOP_LEFT_QUADRANT; // Top left quadrant
            } else if (yMax < horizontalMidpoint) {
                return BOTTOM_LEFT_QUADRANT; // Bottom left quadrant
            }
        } else if (xMin > verticalMidpoint) {
            if (yMin > horizontalMidpoint) {
                return TOP_RIGHT_QUADRANT; // Top right quadrant
            } else if (yMax < horizontalMidpoint) {
                return BOTTOM_RIGHT_QUADRANT; // Bottom right quadrant
            }
        }

        return CROSSING_BOUNDARIES;
    }

    private <T> void split(ArrayList<T> objects, boolean activationAreas) {
        float yMid = (yMin + yMax) / 2;
        float xMid = (xMin + xMax) / 2;

        var nextDepth = depth + 1;
        quadrants[0] = createQuadrant(nextDepth, xMin, yMid, xMid, yMax);
        quadrants[1] = createQuadrant(nextDepth, xMid, yMid, xMax, yMax);
        quadrants[2] = createQuadrant(nextDepth, xMin, yMin, xMid, yMid);
        quadrants[3] = createQuadrant(nextDepth, xMid, yMin, xMax, yMid);

        Iterator<T> it = objects.iterator();
        while (it.hasNext()) {
            T next = it.next();
            var collidable = (InternalCollidable) next;
            int index = getQuadrantIndex(collidable);
            if (index == CROSSING_BOUNDARIES)
                continue;
            it.remove();
            if (activationAreas) {
                quadrants[index].insertActivationAreaHere((ActivationArea) collidable);
            } else {
                quadrants[index].insertHere((Collidable) collidable);
            }
        }
    }

    private void insertHere(Collidable obj) {
        objects.add(obj);
        if (quadrants[0] == null && objects.size() > capacity && depth < maxDepth) {
            split(objects, false);
        }
    }

    private void insertActivationAreaHere(ActivationArea area) {
        activationAreas.add(area);
        if (quadrants[0] == null && activationAreas.size() > capacity && depth < maxDepth) {
            split(activationAreas, true);
        }
    }

    private InternalQuadTreeNode getQuadrant(InternalCollidable obj) {
        if (hasQuadrants()) {
            int index = getQuadrantIndex(obj);
            return index != CROSSING_BOUNDARIES ? quadrants[index].getQuadrant(obj) : this;
        } else {
            return this;
        }
    }

    private int detectInCurrentNode(Collidable obj) {
        var result = NOT_ACTIVATED;
        int size = activationAreas.size();
        for (int i = 0; i < size; ++i) {
            var activationArea = activationAreas.get(i);
            if (isColliding(activationArea, obj)) {
                var activationResult = activationArea.onActivation(obj);
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

    private int insertHelper(Collidable obj, int detectionResult, boolean nodeFound) {
        detectionResult = detectionResult == ACTIVATED_STOP_SEARCH
                ? ACTIVATED_STOP_SEARCH
                : Math.max(detectionResult, detectInCurrentNode(obj));

        if (hasQuadrants()) {
            int quadrantIndex = nodeFound ? CROSSING_BOUNDARIES : getQuadrantIndex(obj);
            if (quadrantIndex != CROSSING_BOUNDARIES) {
                return quadrants[quadrantIndex].insertHelper(
                        obj,
                        detectionResult,
                        false);
            } else {
                int index = 0;
                while (detectionResult != ACTIVATED_STOP_SEARCH && index < 4) {
                    detectionResult = quadrants[index].insertHelper(
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

    InsertionResult insert(Collidable obj) {
        var result = insertHelper(obj, NOT_ACTIVATED, false);
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

    void insertWithoutActivation(Collidable obj) {
        getQuadrant(obj).insertHere(obj);
    }

    void insertActivationArea(ActivationArea obj) {
        getQuadrant(obj).insertActivationAreaHere(obj);
    }

    private boolean hasMoved(Collidable collidable, Rectangle prevBounds, float prevX, float prevY) {
        return prevX != collidable.getPosX()
                || prevY != collidable.getPosY()
                || !prevBounds.equals(collidable.getBounds());
    }

    private InternalQuadTreeNode updateNode(
            Collidable collidable, InternalQuadTreeNode prevNode, Rectangle prevBounds, float prevX, float prevY) {
        if (hasMoved(collidable, prevBounds, prevX, prevY)) {
            var newNode = getQuadrant(collidable);
            if (prevNode != newNode) {
                prevNode.objects.remove(collidable);
                newNode.insertHere(collidable);
            }
            return newNode;
        }

        return prevNode;
    }

    public void update(Collidable collidable, float deltaTime) {
        var boundsBeforeUpdate = collidable.getBounds();
        var xBeforeUpdate = collidable.getPosX();
        var yBeforeUpdate = collidable.getPosY();

        // Node before update
        var currentNode = getQuadrant(collidable);

        collidable.update(deltaTime);

        if (collidable.canCollide()) {
            // Node after update
            currentNode = updateNode(
                    collidable,
                    currentNode,
                    boundsBeforeUpdate,
                    xBeforeUpdate,
                    yBeforeUpdate);

            if (collidable instanceof CollisionAware<?>) {
                params.reset(
                        deltaTime,
                        xBeforeUpdate,
                        yBeforeUpdate,
                        collidable.getPosX(),
                        collidable.getPosY());

                for (int i = 0; i < Integer.MAX_VALUE; ++i) {
                    params.attempt = i;

                    var boundsBeforeCollision = collidable.getBounds();
                    var xBeforeCollision = collidable.getPosX();
                    var yBeforeCollision = collidable.getPosY();

                    var result = currentNode.handleCollisions((CollisionAware<?>) collidable, params);

                    // Node after collision detection
                    currentNode = updateNode(
                            collidable,
                            currentNode,
                            boundsBeforeCollision,
                            xBeforeCollision,
                            yBeforeCollision);

                    if (result != CollisionResult.RERUN) {
                        break;
                    }
                }
            }
        }
    }

    private boolean hasQuadrants() {
        return quadrants[0] != null;
    }

    private <T extends Collidable> CollisionResult onCollision(
            CollisionAware<T> collisionAware, Collidable other, CollisionParameters params) {
        var type = collisionAware.getCollidableClass();
        if (type.isAssignableFrom(other.getClass())) {
            return collisionAware.onCollision(type.cast(other), params);
        }

        return CollisionResult.CONTINUE;
    }

    private <T extends Collidable> CollisionResult handleCollisions(
            CollisionAware<T> collisionAware, CollisionParameters params) {
        int size = objects.size();
        for (int i = 0; i < size; ++i) {
            var other = objects.get(i);
            if (collisionAware != other && other.canCollide() && isColliding(collisionAware, other)) {
                var result = onCollision(collisionAware, other, params);
                if (result == CollisionResult.CONTINUE) {
                    if (collisionAware.canCollide()) {
                        continue;
                    }

                    return CollisionResult.ABORT;
                } else {
                    return result;
                }
            }
        }

        if (hasQuadrants()) {
            int quadrantIndex = getQuadrantIndex(collisionAware);
            if (quadrantIndex == CROSSING_BOUNDARIES) {
                for (int i = 0; i < 4; ++i) {
                    var result = quadrants[i].handleCollisions(collisionAware, params);
                    if (result == CollisionResult.CONTINUE) {
                        continue;
                    } else {
                        return result;
                    }
                }
            } else {
                return quadrants[quadrantIndex].handleCollisions(collisionAware, params);
            }
        }

        return CollisionResult.CONTINUE;
    }
}