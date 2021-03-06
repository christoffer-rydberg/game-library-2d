package com.gamelibrary2d.demos.drawing;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.event.DefaultEventPublisher;
import com.gamelibrary2d.common.event.EventListener;
import com.gamelibrary2d.common.event.EventPublisher;
import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.denotations.PointerAware;

public class GeometryTool implements Renderable, PointerAware {
    private final EventPublisher<Geometry> onCreated = new DefaultEventPublisher<>();
    private final Factory<Geometry> geometryFactory;
    private final int drawButton;
    private final Point prevNode;
    private final float minNodeInterval;

    private Geometry inProgress;

    public GeometryTool(int drawButton, Factory<Geometry> geometryFactory, float minNodeInterval) {
        this.drawButton = drawButton;
        this.geometryFactory = geometryFactory;
        this.minNodeInterval = minNodeInterval;
        this.prevNode = new Point();
    }

    public void addGeometryCreatedListener(EventListener<Geometry> listener) {
        onCreated.addListener(listener);
    }

    @Override
    public void render(float alpha) {
        if (isDrawing()) {
            inProgress.render(alpha);
        }
    }

    private boolean isDrawing() {
        return inProgress != null;
    }

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (drawButton == button) {
            inProgress = geometryFactory.create();
            inProgress.nodes().add(projectedX, projectedY);
            prevNode.set(projectedX, projectedY);
            return true;
        }

        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (isDrawing()) {
            if (prevNode.getDistance(projectedX, projectedY) > minNodeInterval) {
                inProgress.nodes().add(projectedX, projectedY);
                prevNode.set(projectedX, projectedY);
            }
            return true;
        }

        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (drawButton == button) {
            onCreated.publish(inProgress);
            inProgress = null;
        }
    }
}
