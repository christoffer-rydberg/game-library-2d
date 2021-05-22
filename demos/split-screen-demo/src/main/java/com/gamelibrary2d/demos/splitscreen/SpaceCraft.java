package com.gamelibrary2d.demos.splitscreen;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.renderers.Renderer;

public class SpaceCraft extends AbstractGameObject implements Updatable {
    private final Rectangle area;
    private final Renderer renderer;
    private final Point delta;

    public SpaceCraft(Rectangle area, Renderer renderer) {
        this.area = area;
        this.renderer = renderer;
        float direction = RandomInstance.get().nextFloat() * 360f;
        delta = new Point(0, 100);
        delta.rotate(direction);
    }

    private static float getInRange(float value, float min, float max) {
        float width = max - min;
        float dist = (value - min) / width;
        float distDecimals = dist - (int) dist;
        if (distDecimals < 0) {
            distDecimals += 1f;
        }

        return distDecimals * width + min;
    }

    private static void wrap(Rectangle area, Point p) {
        float x = getInRange(p.getX(), area.getLowerX(), area.getUpperX());
        float y = getInRange(p.getY(), area.getLowerY(), area.getUpperY());
        p.set(x, y);
        p.add(area.getLowerX(), area.getLowerY());
    }

    @Override
    protected void onRender(float alpha) {
        renderer.render(alpha);
    }

    @Override
    public void update(float deltaTime) {
        setRotation(delta.getAngleDegrees());
        getPosition().add(delta.getX() * deltaTime, delta.getY() * deltaTime);
        wrap(area, getPosition());
    }

    @Override
    public Rectangle getBounds() {
        return renderer.getBounds();
    }
}
