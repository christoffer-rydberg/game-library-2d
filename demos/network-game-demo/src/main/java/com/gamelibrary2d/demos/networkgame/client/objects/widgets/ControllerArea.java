package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.demos.networkgame.client.input.Controller;
import com.gamelibrary2d.demos.networkgame.client.input.ControllerInputId;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.layers.BasicLayer;
import com.gamelibrary2d.layers.Layer;
import com.gamelibrary2d.objects.ComposableGameObject;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.widgets.AbstractAggregatingWidget;
import com.gamelibrary2d.widgets.DefaultWidget;

public class ControllerArea extends AbstractAggregatingWidget<Layer> {
    private final Point dragOrigin = new Point();
    private final Controller controller;
    private ComposableGameObject<Renderer> handle;
    private SliderDirection direction;
    private float max, step;
    private int dragButton = -1;

    public ControllerArea(Renderer handle, Controller controller, SliderDirection dir, float maxDistance) {
        this.handle = createHandle(handle);
        this.handle.getContent().getParameters().setColor(0, 0, 0, 1f);
        this.controller = controller;
        this.direction = dir;
        this.max = maxDistance;
        this.step = 1f;
        Layer<Renderable> content = new BasicLayer<>();
        content.add(this.handle);
        setContent(content);
    }

    public void setValue(float value) {
        switch (direction) {
            case HORIZONTAL: {
                float controllerValue = Math.max(-max, Math.min(max, value)) / max;
                if (controllerValue < 0) {
                    controller.setValue(ControllerInputId.RIGHT, 0f);
                    controller.setValue(ControllerInputId.LEFT, Math.abs(controllerValue));
                    handle.getContent().getParameters().setColor(0, Math.abs(controllerValue), 0, 1f);
                } else {
                    controller.setValue(ControllerInputId.LEFT, 0f);
                    controller.setValue(ControllerInputId.RIGHT, controllerValue);
                    handle.getContent().getParameters().setColor(0, 0, Math.abs(controllerValue), 1f);
                }

                break;
            }
            case VERTICAL: {
                float controllerValue = Math.max(0f, Math.min(max, value)) / max;
                controller.setValue(ControllerInputId.UP, controllerValue);
                handle.getContent().getParameters().setColor(0, controllerValue, 0, 1f);
                break;
            }
        }
    }

    private float getValueFromPosition(float x, float y) {
        switch (direction) {
            case HORIZONTAL:
                return (handle.getPosition().getX() + x - dragOrigin.getX()) / step;
            case VERTICAL:
                return (handle.getPosition().getY() + y - dragOrigin.getY()) / step;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

    private DefaultWidget<Renderer> createHandle(Renderer renderable) {
        DefaultWidget<Renderer> handleObj = new DefaultWidget<>(renderable);
        handleObj.addMouseButtonDownListener(this::onHandleClicked);
        handleObj.addMouseDragListener(this::onHandleDragged);
        handleObj.addMouseButtonReleasedListener(this::onHandleReleased);
        return handleObj;
    }

    private void onHandleClicked(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (dragButton < 0) {
            dragButton = button;
            dragOrigin.set(projectedX, projectedY);
        }
    }

    private void onHandleReleased(int button, int mods, float x, float y, float projectedX, float projectedY) {
        if (dragButton == button) {
            dragButton = -1;
            setValue(0f);
        }
    }

    private void onHandleDragged(float x, float y, float projectedX, float projectedY) {
        if (dragButton >= 0) {
            setValue(getValueFromPosition(projectedX, projectedY));
        }
    }

    public enum SliderDirection {
        HORIZONTAL,
        VERTICAL
    }
}
