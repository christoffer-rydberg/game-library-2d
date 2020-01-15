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

    // Area constants
    private static final int INSIDE_DETECTION_AREA_STOP_SEARCH = 3;
    private static final int INSIDE_DETECTION_AREA = 2;
    private static final int NEAR_DETECTION_AREA = 1;
    private static final int OUTSIDE_DETECTION_AREA = 0;

    private final Pool<InternalQuadTreeNode> nodePool;
    private final ArrayList<Collidable> objects;
    private final ArrayList<CollisionDetectionArea> detectionAreas;
    private final InternalQuadTreeNode[] quadrants;

    private int depth;
    private int maxDepth;
    private int capacity;
    private float xMin, yMin, xMax, yMax;

    InternalQuadTreeNode(Pool<InternalQuadTreeNode> nodePool) {
        this.nodePool = nodePool;
        objects = new ArrayList<>();
        detectionAreas = new ArrayList<>();
        quadrants = new InternalQuadTreeNode[4];
    }

    private static boolean isColliding(Rectangle bounds, float xPos, float yPos, Rectangle bounds2, float xPos2, float yPos2) {
        return !(bounds.getXMin() + xPos > bounds2.getXMax() + xPos2
                || bounds.getYMin() + yPos > bounds2.getYMax() + yPos2
                || bounds.getXMax() + xPos < bounds2.getXMin() + xPos2
                || bounds.getYMax() + yPos < bounds2.getYMin() + yPos2);
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
        detectionAreas.clear();
        for (int i = 0; i < 4; ++i) {
            if (quadrants[i] != null) {
                quadrants[i].clear();
                quadrants[i] = null;
            }
        }
    }

    private int getQuadrantIndex(InternalCollidable collidable) {
        var bounds = collidable.getBounds();
        float xMin = bounds.getXMin();
        float yMin = bounds.getYMin();
        float xMax = bounds.getXMax();
        float yMax = bounds.getYMax();

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

    private <T> void split(ArrayList<T> objects, boolean detectionAreas) {
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
            if (detectionAreas) {
                quadrants[index].insertDetectionAreaHere((CollisionDetectionArea) collidable);
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

    private void insertDetectionAreaHere(CollisionDetectionArea area) {
        detectionAreas.add(area);
        if (quadrants[0] == null && detectionAreas.size() > capacity && depth < maxDepth) {
            split(detectionAreas, true);
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

    private int getInsertionResultHere(Collidable obj, int accumulatedResult) {
        int size = detectionAreas.size();
        for (int i = 0; i < size; ++i) {
            var activationArea = detectionAreas.get(i);
            if (isColliding(activationArea, obj)) {
                var activationResult = activationArea.activate(obj);
                switch (activationResult) {
                    case ACTIVATED:
                        return INSIDE_DETECTION_AREA_STOP_SEARCH;
                    case ACTIVATED_CONTINUE_SEARCH:
                        accumulatedResult = INSIDE_DETECTION_AREA;
                        break;
                    case NOT_ACTIVATED:
                        accumulatedResult = Math.max(accumulatedResult, NEAR_DETECTION_AREA);
                        break;
                }
            }
        }

        return accumulatedResult;
    }

    private int getInsertionResultBelow(Collidable obj, int accumulatedResult) {
        if (accumulatedResult != INSIDE_DETECTION_AREA_STOP_SEARCH && hasQuadrants()) {
            int quadrantIndex = getQuadrantIndex(obj);
            if (quadrantIndex != CROSSING_BOUNDARIES) {
                return quadrants[quadrantIndex].insertIfDetectedHelper(obj, false, accumulatedResult);
            } else {
                for (int i = 0; i < 4; ++i) {
                    accumulatedResult = Math.max(accumulatedResult, quadrants[i].insertIfDetectedHelper(obj, true, OUTSIDE_DETECTION_AREA));
                    if (accumulatedResult == INSIDE_DETECTION_AREA_STOP_SEARCH) {
                        return accumulatedResult;
                    }
                }
            }
        }
        return accumulatedResult;
    }

    private int insertIfDetectedHelper(Collidable obj, boolean fromAbove, int accumulatedResult) {
        accumulatedResult = getInsertionResultHere(obj, accumulatedResult);
        accumulatedResult = getInsertionResultBelow(obj, accumulatedResult);
        if (!fromAbove && accumulatedResult != OUTSIDE_DETECTION_AREA) {
            insertHere(obj);
        }
        return accumulatedResult;
    }

    void insert(Collidable obj) {
        getQuadrant(obj).insertHere(obj);
    }

    void insertDetectionArea(CollisionDetectionArea obj) {
        getQuadrant(obj).insertDetectionAreaHere(obj);
    }

    boolean insertIfDetected(Collidable obj) {
        var result = insertIfDetectedHelper(obj, false, OUTSIDE_DETECTION_AREA);
        return result == INSIDE_DETECTION_AREA;
    }

    public void update(Collidable collidable, float deltaTime) {
        var nodeBeforeUpdate = getQuadrant(collidable);
        var result = collidable.update(deltaTime);
        if (result == UpdateResult.STILL) {
            handleCollisionsIfCollisionAware(collidable);
        } else if (result == UpdateResult.MOVED) {
            handleCollisionsIfCollisionAware(collidable);
            InternalQuadTreeNode nodeAfterUpdate = getQuadrant(collidable);
            if (nodeBeforeUpdate != nodeAfterUpdate) {
                nodeBeforeUpdate.objects.remove(collidable);
                nodeAfterUpdate.insertHere(collidable);
            }
        } else if (result == UpdateResult.SKIP) {
            nodeBeforeUpdate.objects.remove(collidable);
        }
    }

    private boolean hasQuadrants() {
        return quadrants[0] != null;
    }

    private <T extends Collidable> boolean onCollision(CollisionAware<T> collisionAware, Collidable other) {
        var type = collisionAware.getCollidableClass();
        return type.isAssignableFrom(other.getClass()) && collisionAware.onCollisionWith(type.cast(other));
    }

    private <T extends CollisionAware<?>> void onCollided(CollidedAware<T> collisionAware, Collidable other) {
        var type = collisionAware.getCollisionAwareClass();
        if (type.isAssignableFrom(other.getClass()))
            collisionAware.onCollidedBy(type.cast(other));
    }

    private void handleCollisionsIfCollisionAware(Collidable collidable) {
        if (collidable instanceof CollisionAware<?>) {
            handleCollisions((CollisionAware<?>) collidable);
        }
    }

    private <T extends Collidable> void handleCollisions(CollisionAware<T> collisionAware) {
        int size = objects.size();
        for (int i = 0; i < size; ++i) {
            var other = objects.get(i);
            if (collisionAware != other && isColliding(collisionAware, other)) {
                if (onCollision(collisionAware, other) && other instanceof CollidedAware) {
                    onCollided((CollidedAware<?>) other, collisionAware);
                }
            }
        }

        if (hasQuadrants()) {
            int quadrantIndex = getQuadrantIndex(collisionAware);
            if (quadrantIndex == CROSSING_BOUNDARIES) {
                for (int i = 0; i < 4; ++i) {
                    quadrants[i].handleCollisions(collisionAware);
                }
            } else {
                quadrants[quadrantIndex].handleCollisions(collisionAware);
            }
        }
    }
}