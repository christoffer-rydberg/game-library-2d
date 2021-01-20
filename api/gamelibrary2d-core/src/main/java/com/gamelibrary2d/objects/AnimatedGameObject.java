package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.ShaderParameters;

public class AnimatedGameObject<T extends Renderer> extends AbstractGameObject<T> implements ComposableGameObject<T>, Updatable {
    private float animationTime;

    public AnimatedGameObject() {

    }

    public AnimatedGameObject(T renderer) {
        super(renderer);
    }

    public AnimatedGameObject(T renderer, Rectangle bounds) {
        super(renderer);
        setBounds(bounds);
    }

    protected float getAnimationTime() {
        return animationTime;
    }

    protected void setAnimationTime(float time) {
        this.animationTime = time;
        var renderer = getContent();
        if (renderer != null) {
            renderer.getParameters().set(ShaderParameters.TIME, time);
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

    public void setContent(T content) {
        super.setContent(content);
    }
}