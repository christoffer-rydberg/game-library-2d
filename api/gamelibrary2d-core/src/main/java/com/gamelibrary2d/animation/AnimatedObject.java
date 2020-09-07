package com.gamelibrary2d.animation;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.objects.AbstractGameObject;
import com.gamelibrary2d.objects.ComposableObject;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.RenderingParameters;

public class AnimatedObject<T extends Renderer> extends AbstractGameObject<T> implements ComposableObject<T>, Updatable {
    private float animationTime;

    public AnimatedObject() {

    }

    public AnimatedObject(T renderer) {
        super(renderer);
    }

    public AnimatedObject(T renderer, Rectangle bounds) {
        super(renderer);
        setBounds(bounds);
    }

    public T getRenderer() {
        return super.getContent();
    }

    public void setRenderer(T content) {
        super.setContent(content);
    }

    protected float getAnimationTime() {
        return animationTime;
    }

    protected void setAnimationTime(float time) {
        this.animationTime = time;
        var renderer = getContent();
        if (renderer != null) {
            renderer.getParameters().set(RenderingParameters.TIME, time);
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

    @Override
    public T getContent() {
        return super.getContent();
    }
}