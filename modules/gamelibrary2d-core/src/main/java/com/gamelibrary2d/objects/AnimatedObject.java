package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.updating.Updatable;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.rendering.RenderSettings;

public class AnimatedObject extends BasicObject implements Updatable {

    private float animationTime;

    public AnimatedObject() {
    }

    public AnimatedObject(Renderer renderer) {
        super(renderer);
    }

    protected float getAnimationTime() {
        return animationTime;
    }

    protected void setAnimationTime(float time) {
        this.animationTime = time;
        var renderer = getRenderer();
        if (renderer != null) {
            renderer.updateSettings(RenderSettings.TIME, time);
        }
    }

    @Override
    public void update(float deltaTime) {
        setAnimationTime(animationTime + deltaTime);
    }
}