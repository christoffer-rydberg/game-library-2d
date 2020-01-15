package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.lightning.LightRenderer;
import com.gamelibrary2d.particle.systems.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFrameLayer<T extends GameObject> extends AbstractModifiableContainer<T> {

    private final List<ParticleSystem> backgroundParticles;
    private final List<ParticleSystem> foregroundParticles;
    private LightRenderer lightRenderer;
    private Action beforeLightRender;
    private Action afterLightRender;

    protected AbstractFrameLayer() {
        backgroundParticles = new ArrayList<>();
        foregroundParticles = new ArrayList<>();
        setAutoClearing(true);
    }

    public void clear() {
        super.clear();
        clear(backgroundParticles);
        clear(foregroundParticles);
    }

    private void clear(List<ParticleSystem> systems) {
        for (int i = 0; i < systems.size(); ++i) {
            systems.get(i).clear();
        }
    }

    public List<ParticleSystem> backgroundParticles() {
        return backgroundParticles;
    }

    public List<ParticleSystem> foregroundParticles() {
        return foregroundParticles;
    }

    @Override
    public Rectangle getBounds() {
        return Rectangle.INFINITE;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        update(backgroundParticles, deltaTime);
        update(foregroundParticles, deltaTime);
    }

    private void update(List<ParticleSystem> systems, float deltaTime) {
        for (int i = 0; i < systems.size(); ++i) {
            systems.get(i).update(deltaTime);
        }
    }

    @Override
    public void onRender(float alpha) {
        render(backgroundParticles, alpha);
        super.onRender(alpha);
        render(foregroundParticles, alpha);
        if (lightRenderer != null) {
            beforeLightRender.invoke();
            lightRenderer.render(alpha);
            afterLightRender.invoke();
        }
    }

    private void render(List<ParticleSystem> systems, float alpha) {
        for (int i = 0; i < systems.size(); ++i) {
            systems.get(i).render(alpha);
        }
    }

    public void setLightRenderer(LightRenderer lightRenderer) {
        this.lightRenderer = lightRenderer;
        this.beforeLightRender = Action.EMPTY;
        this.afterLightRender = Action.EMPTY;
    }

    public void setLightRenderer(LightRenderer lightRenderer, Action beforeRender, Action afterRender) {
        this.lightRenderer = lightRenderer;
        this.beforeLightRender = beforeRender;
        this.afterLightRender = afterRender;
    }
}