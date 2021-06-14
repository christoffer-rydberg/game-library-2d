package com.gamelibrary2d.demos.networkgame.client.objects.widgets;

import com.gamelibrary2d.common.Point;
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

public class AccelerationArea implements Renderable, PointerAware, Updatable {
    private final Point origin = new Point();
    private final Renderer background;
    private final LocalPlayer player;
    private final float max, step;
    private int pointerId = -1;
    private int pointerButton = -1;
    private float acceleration;

    private AccelerationArea(Renderer background, LocalPlayer player, float maxDistance) {
        this.background = background;
        this.player = player;
        this.max = maxDistance;
        this.step = 1f;
        setBackgroundColor(0, 0, 0, 1f);
    }

    public static AccelerationArea create(Rectangle bounds, LocalPlayer player, float maxDistance, Disposer disposer) {
        return new AccelerationArea(
                new SurfaceRenderer<>(Quad.create(bounds, disposer)),
                player,
                maxDistance);
    }

    private void setBackgroundColor(float r, float g, float b, float a) {
        this.background.getParameters().setColor(r, g, b, a);
    }

    private void setValue(float value) {
        Controller controller = player.getController();
        float controllerValue = Math.max(0f, Math.min(max, value)) / max;
        controller.setValue(ControllerInputId.UP, controllerValue);
        setBackgroundColor(0, controllerValue, 0, 1f);
    }

    @Override
    public void render(float alpha) {
        background.render(alpha);
    }

    @Override
    public boolean pointerDown(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (background.getBounds().contains(projectedX, projectedY)) {
            if (pointerId < 0) {
                pointerId = id;
                pointerButton = button;
                origin.set(projectedX, projectedY);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean pointerMove(int id, float x, float y, float projectedX, float projectedY) {
        if (pointerId == id) {
            acceleration = (projectedY - origin.getY()) / step;
            return true;
        }

        return false;
    }

    @Override
    public void pointerUp(int id, int button, float x, float y, float projectedX, float projectedY) {
        if (pointerId == id && pointerButton == button) {
            pointerId = -1;
            pointerButton = -1;
            acceleration = 0f;
            setValue(0f);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (pointerId >= 0) {
            setValue(acceleration);
        }
    }
}
