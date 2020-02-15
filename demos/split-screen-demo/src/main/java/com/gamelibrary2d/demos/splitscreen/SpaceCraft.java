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
        var direction = RandomInstance.get().nextFloat() * 360f;
        delta = new Point(0, 100);
        delta.rotate(direction);
    }

    @Override
    protected void onRenderProjected(float alpha) {
        renderer.render(alpha);
    }

    @Override
    public void update(float deltaTime) {
        setRotation(delta.getAngleDegrees());
        position().add(delta.getX() * deltaTime, delta.getY() * deltaTime);
        area.wrap(position());
    }
}
