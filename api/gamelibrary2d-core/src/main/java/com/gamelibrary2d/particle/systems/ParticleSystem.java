package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.denotations.Updatable;

public interface ParticleSystem extends Updatable, Renderable {

    int getParticleCount();

    @Override
    void update(float deltaTime);

    @Override
    void render(float alpha);
}
