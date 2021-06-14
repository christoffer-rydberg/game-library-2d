package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.FloatUtils;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.demos.networkgame.client.input.Controller;
import com.gamelibrary2d.demos.networkgame.client.input.ControllerInputId;
import com.gamelibrary2d.demos.networkgame.client.objects.network.LocalPlayer;
import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.denotations.PointerAware;
import com.gamelibrary2d.components.denotations.Updatable;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.SurfaceRenderer;
import com.gamelibrary2d.resources.Quad;

public class RotationArea implements Renderable, PointerAware, Updatable {
    private final Line line;
    private final Renderer background;
    private final LocalPlayer player;
    private final float max;
    private final RotationMode mode;
    private int pointerId = -1;
    private int pointerButton = -1;
    private float direction;
    private float rotationSpeed;

    private RotationArea(RotationMode mode, Line line, Renderer background, LocalPlayer player, float maxDistance) {
        this.mode = mode;
        this.line = line;
        this.background = background;
        this.player = player;
        this.max = maxDistance;
        setBackgroundColor(0, 0, 0, 1f);
    }

    public static RotationArea create(RotationMode mode, Rectangle bounds, LocalPlayer player, float maxDistance, Disposer disposer) {
        return new RotationArea(
                mode,
                Line.create(disposer),
                new SurfaceRenderer<>(Quad.create(bounds, disposer)),
                player,
                maxDistance);
    }

    private void setBackgroundColor(float r, float g, float b, float a) {
        this.background.getParameters().setColor(r, g, b, a);
    }

    private void setValue(float value) {
        Controller controller = player.getController();

        float controllerValue = Math.max(-max, Math.min(max, value)) / max;

        if (mode == RotationMode.TOWARD_DIRECTION) {
            player.setAccelerationLimit(1f - Math.abs(controllerValue));
        }

        if (controllerValue < 0) {
            controller.setValue(ControllerInputId.RIGHT, 0f);
            controller.setValue(ControllerInputId.LEFT, Math.abs(controllerValue));
            setBackgroundColor(0, Math.abs(controllerValue), 0, 1f);
        } else {
            controller.setValue(ControllerInputId.LEFT, 0f);
            controller.setValue(ControllerInputId.RIGHT, controllerValue);
            setBackgroundColor(0, 0, Math.abs(controllerValue), 1f);
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
        if (pointerId == id) {
            switch (mode) {
                case LEFT_OR_RIGHT:
                    direction = line.getStart().getDirectionDegrees(projectedX, line.getStart().getY());
                    rotationSpeed = Math.min(max, line.getStart().getDistance(projectedX, projectedY));
                    break;
                case TOWARD_DIRECTION:
                    direction = line.getStart().getDirectionDegrees(projectedX, projectedY);
                    rotationSpeed = Math.min(max, line.getStart().getDistance(projectedX, projectedY));
                    break;
            }

            line.getEnd().set(line.getStart());
            line.getEnd().offsetDegrees(rotationSpeed, direction);
            line.refresh();

            return true;
        }

        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (pointerId == id && pointerButton == button) {
            pointerId = -1;
            pointerButton = -1;
            direction = 0;
            rotationSpeed = 0;
            setValue(0f);
            player.setAccelerationLimit(1f);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (pointerId >= 0) {
            switch (mode) {
                case LEFT_OR_RIGHT:
                    setValue(direction >= 0 ? rotationSpeed : -rotationSpeed);
                    break;
                case TOWARD_DIRECTION:
                    float delta = FloatUtils.normalizeDegrees(direction - player.getRotation());
                    setValue((delta / 180f) * rotationSpeed * 2);
                    break;
            }
        }
    }

    public enum RotationMode {
        LEFT_OR_RIGHT,
        TOWARD_DIRECTION
    }
}
