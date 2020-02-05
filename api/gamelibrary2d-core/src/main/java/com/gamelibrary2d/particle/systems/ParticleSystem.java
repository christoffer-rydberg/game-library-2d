package com.gamelibrary2d.particle.systems;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.markers.Updatable;
import com.gamelibrary2d.markers.Clearable;

public interface ParticleSystem extends Updatable, Clearable, Renderable {

    int getParticleCount();

    @Override
    void update(float deltaTime);

    @Override
    void render(float alpha);

    @Override
    void clear();

    @Override
    default boolean isAutoClearing() {
        return true;
    }
}
