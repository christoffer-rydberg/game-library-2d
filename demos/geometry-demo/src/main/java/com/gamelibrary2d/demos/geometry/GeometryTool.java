package com.gamelibrary2d.demos.geometry;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.event.DefaultEventPublisher;
import com.gamelibrary2d.common.event.EventListener;
import com.gamelibrary2d.common.event.EventPublisher;
import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.MouseAware;

public class GeometryTool implements Renderable, MouseAware {
    private final Factory<Geometry> geometryFactory;
    private final Point prevNode;
    private final float minNodeInterval;
    private final EventPublisher<Geometry> lineCreated = new DefaultEventPublisher<>();

    private Geometry drawnGeometry;
    private int drawButton = -1;

    public GeometryTool(Factory<Geometry> geometryFactory, float minNodeInterval) {
        this.geometryFactory = geometryFactory;
        this.minNodeInterval = minNodeInterval;
        this.prevNode = new Point(-minNodeInterval, -minNodeInterval);
    }

    public void addGeometryCreatedListener(EventListener<Geometry> listener) {
        lineCreated.addListener(listener);
    }

    @Override
    public void render(float alpha) {
        if (drawnGeometry != null) {
            drawnGeometry.render(alpha);
        }
    }

    @Override
    public boolean mouseButtonDown(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (drawButton == -1) {
            drawButton = button;
            drawnGeometry = geometryFactory.create();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseMove(float x, float y, float projectedX, float projectedY) {
        if (drawButton != -1) {
            if (prevNode.getDistance(projectedX, projectedY) > minNodeInterval) {
                drawnGeometry.nodes().add(projectedX, projectedY);
                prevNode.set(projectedX, projectedY);
            }

            return true;
        }

        return false;
    }

    @Override
    public void mouseButtonReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (drawButton == button) {
            drawButton = -1;
            lineCreated.publish(drawnGeometry);
            drawnGeometry = null;
        }
    }
}
