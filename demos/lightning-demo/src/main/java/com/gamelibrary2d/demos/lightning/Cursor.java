package com.gamelibrary2d.demos.lightning;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.Rectangle;
import com.gamelibrary2d.lightning.DefaultDynamicLightMap;
import com.gamelibrary2d.components.denotations.Updatable;
import com.gamelibrary2d.components.objects.AbstractCursor;
import com.gamelibrary2d.particle.SequentialParticleEmitter;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.renderers.Renderer;

public class Cursor extends AbstractCursor implements Updatable {
    private final Renderer renderer;
    private final DefaultDynamicLightMap lightMap;
    private final SequentialParticleEmitter particleEmitter;

    Cursor(Game game, Renderer renderer, DefaultDynamicLightMap lightMap, DefaultParticleSystem particleSystem) {
        super(game, 0);
        this.renderer = renderer;
        this.lightMap = lightMap;
        this.particleEmitter = new SequentialParticleEmitter(particleSystem);
        particleEmitter.getParticleSystem().setUpdateListener((sys, par) -> {
            float alpha = par.getColorA();
            float size = par.getScale() * par.getScale();
            float light = 2 * (alpha + 0.5f) * size;
            lightMap.addInterpolated(par.getPosX() / 32f, par.getPosY() / 32f, light);
            return true;
        });
    }

    @Override
    public void update(float deltaTime) {
        lightMap.addInterpolated(getPosition().getX() / 32f, getPosition().getY() / 32f, 20);
        particleEmitter.getPosition().set(getPosition());
        particleEmitter.update(deltaTime);
    }

    @Override
    protected void onInteracted() {

    }

    @Override
    protected void onRender(float alpha, boolean hasWindowFocus) {
        if (hasWindowFocus) {
            renderer.render(alpha);
        }
    }

    @Override
    public Rectangle getBounds() {
        return renderer.getBounds();
    }
}
