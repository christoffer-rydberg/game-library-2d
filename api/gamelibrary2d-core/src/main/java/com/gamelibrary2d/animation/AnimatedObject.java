package com.gamelibrary2d.animation;

import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.BasicGameObject;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.rendering.RenderSettings;

public class AnimatedObject extends BasicGameObject implements Updatable {

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
    public final void update(float deltaTime) {
        if (isEnabled()) {
            onUpdate(deltaTime);
        }
    }

    protected void onUpdate(float deltaTime) {
        setAnimationTime(animationTime + deltaTime);
    }
}