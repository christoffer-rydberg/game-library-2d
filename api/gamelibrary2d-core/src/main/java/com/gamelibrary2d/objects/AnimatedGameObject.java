package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.ShaderParameters;

public class AnimatedGameObject<T extends Renderer> extends AbstractGameObject implements ComposableGameObject<T>, Updatable {
    private float animationTime;
    private T composition;
    private Rectangle bounds;

    public AnimatedGameObject() {

    }

    public AnimatedGameObject(T composition) {
        this.composition = composition;
    }

    public AnimatedGameObject(T composition, Rectangle bounds) {
        this.composition = composition;
        this.bounds = bounds;
    }

    @Override
    protected void onRender(float alpha) {
        if (this.composition != null) {
            composition.render(alpha);
        }
    }

    protected float getAnimationTime() {
        return animationTime;
    }

    protected void setAnimationTime(float time) {
        this.animationTime = time;
        if (composition != null) {
            composition.getParameters().set(ShaderParameters.TIME, time);
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
    public T getComposition() {
        return composition;
    }

    public void setComposition(T renderer) {
        this.composition = renderer;
    }

    @Override
    public Rectangle getBounds() {
        return bounds != null ? bounds : composition.getBounds();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}