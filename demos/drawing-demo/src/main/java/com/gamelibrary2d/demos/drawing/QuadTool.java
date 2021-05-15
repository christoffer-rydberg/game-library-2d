package com.gamelibrary2d.demos.drawing;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.event.DefaultEventPublisher;
import com.gamelibrary2d.common.event.EventListener;
import com.gamelibrary2d.common.event.EventPublisher;
import com.gamelibrary2d.common.functional.Func;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.PointerAware;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.DynamicQuad;
import com.gamelibrary2d.resources.Quad;

public class QuadTool implements Renderable, PointerAware {
    private final EventPublisher<Quad> onCreated = new DefaultEventPublisher<>();
    private final Func<Rectangle, Quad> quadFactory;
    private final SurfaceRenderer<DynamicQuad> inProgressRenderer;
    private final int drawButton;

    private QuadInProgress inProgress;

    public QuadTool(int drawButton, DynamicQuad quadInProgress, Func<Rectangle, Quad> quadFactory) {
        this.drawButton = drawButton;
        this.inProgressRenderer = new SurfaceRenderer<>(quadInProgress);
        this.quadFactory = quadFactory;
    }

    public void addQuadCreatedListener(EventListener<Quad> listener) {
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
        if (drawButton == id) {
            inProgress = new QuadInProgress(x, y);
            return true;
        }

        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (isDrawing()) {
            inProgress.update(x, y);
            return true;
        }

        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (drawButton == id) {
            Quad quad = quadFactory.invoke(inProgress.getBounds());
            onCreated.publish(quad);
            inProgress = null;
        }
    }

    private class QuadInProgress implements Renderable {
        private final float x0;
        private final float y0;
        private float x1;
        private float y1;

        public QuadInProgress(float x0, float y0) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x0;
            this.y1 = y0;
        }

        public void update(float x1, float y1) {
            this.x1 = x1;
            this.y1 = y1;
        }

        float getLowerX() {
            return Math.min(x0, x1);
        }

        float getLowerY() {
            return Math.min(y0, y1);
        }

        float getUpperX() {
            return Math.max(x0, x1);
        }

        float getUpperY() {
            return Math.max(y0, y1);
        }

        public Rectangle getBounds() {
            return new Rectangle(
                    getLowerX(),
                    getLowerY(),
                    getUpperX(),
                    getUpperY()
            );
        }

        public void render(float alpha) {
            inProgressRenderer.getSurface().setBounds(
                    getLowerX(), getLowerY(), getUpperX(), getUpperY());

            inProgressRenderer.render(alpha);
        }
    }
}
