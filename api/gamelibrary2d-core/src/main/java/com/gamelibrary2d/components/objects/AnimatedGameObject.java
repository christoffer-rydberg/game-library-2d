package com.gamelibrary2d.components.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.components.denotations.Updatable;
import com.gamelibrary2d.renderers.Renderer;
import com.gamelibrary2d.renderers.ShaderParameters;

public class AnimatedGameObject<T extends Renderer> extends AbstractGameObject implements Updatable {
    private float animationTime;
    private T content;
    private Rectangle bounds;

    public AnimatedGameObject() {

    }

    public AnimatedGameObject(T content) {
        this.content = content;
    }

    public AnimatedGameObject(T content, Rectangle bounds) {
        this.content = content;
        this.bounds = bounds;
    }

    @Override
    protected void onRender(float alpha) {
        if (this.content != null) {
            content.render(alpha);
        }
    }

    protected float getAnimationTime() {
        return animationTime;
    }

    protected void setAnimationTime(float time) {
        this.animationTime = time;
        if (content != null) {
            content.getParameters().set(ShaderParameters.TIME, time);
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

    public T getContent() {
        return content;
    }

    public void setContent(T renderer) {
        this.content = renderer;
    }

    @Override
    public Rectangle getBounds() {
        return bounds != null ? bounds : content.getBounds();
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}