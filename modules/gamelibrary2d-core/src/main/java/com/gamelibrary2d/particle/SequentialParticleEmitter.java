package com.gamelibrary2d.particle;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.updating.Updatable;
import com.gamelibrary2d.particle.systems.DefaultParticleSystem;

public class SequentialParticleEmitter implements Updatable {

    private final DefaultParticleSystem particleSystem;

    private final Point position;

    private float interval = -1;

    private float time;

    public SequentialParticleEmitter(DefaultParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
        this.position = new Point();
    }

    public SequentialParticleEmitter(DefaultParticleSystem particleSystem, float posX, float posY) {
        this.particleSystem = particleSystem;
        this.position = new Point(posX, posY);
    }

    /**
     * The interval between emitted particles, in seconds. If this value is zero or
     * negative, the default interval of the particle system will be used.
     */
    public float getInterval() {
        return interval;
    }

    /**
     * Sets the {@link #getInterval interval} between emitted particles.
     */
    public void setInterval(float interval) {
        this.interval = interval;
    }

    public Point getPosition() {
        return position;
    }

    public DefaultParticleSystem getParticleSystem() {
        return particleSystem;
    }

    @Override
    public void update(float deltaTime) {
        if (interval > 0)
            time = particleSystem.emitSequential(position.getX(), position.getY(), 0, time, deltaTime, interval);
        else
            time = particleSystem.emitSequential(position.getX(), position.getY(), 0, time, deltaTime);
    }
}