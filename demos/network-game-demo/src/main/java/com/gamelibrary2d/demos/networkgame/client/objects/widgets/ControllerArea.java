package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.demos.networkgame.client.input.Controller;
import com.gamelibrary2d.demos.networkgame.client.input.ControllerInputId;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.PointerAware;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;

public class ControllerArea implements Renderable, PointerAware {
    private final Line line;
    private final Renderer background;
    private final Controller controller;
    private final SliderDirection direction;
    private final float max, step;
    private int pointerId = -1;
    private int pointerButton = -1;

    private ControllerArea(Line line, Renderer background, Controller controller, SliderDirection dir, float maxDistance) {
        this.line = line;
        this.background = background;
        this.controller = controller;
        this.direction = dir;
        this.max = maxDistance;
        this.step = 1f;
        setBackgroundColor(0, 0, 0, 1f);
    }

    public static ControllerArea create(Rectangle bounds, Controller controller, SliderDirection dir, float maxDistance, Disposer disposer) {
        return new ControllerArea(
                Line.create(disposer),
                new SurfaceRenderer<>(Quad.create(bounds, disposer)),
                controller,
                dir,
                maxDistance);
    }

    private void setBackgroundColor(float r, float g, float b, float a) {
        this.background.getParameters().setColor(r, g, b, a);
    }

    public void setValue(float value) {
        switch (direction) {
            case HORIZONTAL: {
                float controllerValue = Math.max(-max, Math.min(max, value)) / max;
                if (controllerValue < 0) {
                    controller.setValue(ControllerInputId.RIGHT, 0f);
                    controller.setValue(ControllerInputId.LEFT, Math.abs(controllerValue));
                    setBackgroundColor(0, Math.abs(controllerValue), 0, 1f);
                } else {
                    controller.setValue(ControllerInputId.LEFT, 0f);
                    controller.setValue(ControllerInputId.RIGHT, controllerValue);
                    setBackgroundColor(0, 0, Math.abs(controllerValue), 1f);
                }

                break;
            }
            case VERTICAL: {
                float controllerValue = Math.max(0f, Math.min(max, value)) / max;
                controller.setValue(ControllerInputId.UP, controllerValue);
                setBackgroundColor(0, controllerValue, 0, 1f);
                break;
            }
        }
    }

    private float getValueFromPosition(float x, float y) {
        Point origin = line.getStart();
        switch (direction) {
            case HORIZONTAL:
                return (x - origin.getX()) / step;
            case VERTICAL:
                return (y - origin.getY()) / step;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

    @Override
    public void render(float alpha) {
        background.render(alpha);
        if (pointerId >= 0) {
            line.render(alpha);
        }
    }

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (background.getBounds().contains(projectedX, projectedY)) {
            if (pointerId < 0) {
                pointerId = id;
                pointerButton = button;
                line.getStart().set(projectedX, projectedY);
                line.getEnd().set(projectedX, projectedY);
                line.refresh();
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (background.getBounds().contains(projectedX, projectedY)) {
            if (pointerId == id) {
                line.getEnd().set(projectedX, projectedY);
                line.refresh();
                setValue(getValueFromPosition(projectedX, projectedY));
            }

            return true;
        }

        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (pointerId == id && pointerButton == button) {
            float direction = line.getStart().getDirectionDegrees(line.getEnd());
            float speed = 2 * line.getStart().getDistance(line.getEnd());

            pointerId = -1;
            pointerButton = -1;
            setValue(0f);
        }
    }

    public enum SliderDirection {
        HORIZONTAL,
        VERTICAL
    }
}
