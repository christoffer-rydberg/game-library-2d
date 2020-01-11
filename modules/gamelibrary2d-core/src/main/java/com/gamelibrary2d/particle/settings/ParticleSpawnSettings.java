package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.Serializable;
import com.gamelibrary2d.particle.systems.Particle;

/**
 * Implementations of this interface holds settings related to how often or how
 * many particles are emitted by the particle system. Another responsibility is
 * to "emit" new particles by setting the initial position, gravity center, and
 * apply particle settings.
 *
 * @author Christoffer Rydberg
 */
public interface ParticleSpawnSettings extends Serializable {

    /**
     * @return The amount of particles that should be emitted simultaneously to get
     * the desired effect of the particle system. This can be used to create
     * explosion-like effects, or pulsating effects when particles are
     * emitted sequentially. For the latter, the {@link #isPulsating()
     * isPulsating} method must return true.
     */
    int getDefaultCount();

    /**
     * @return The variation of the {@link #getDefaultCount() default count}.
     */
    int getDefaultCountVar();

    /**
     * @return The interval, in seconds, between sequentially emitted particles.
     */
    float getDefaultInterval();

    /**
     * @return True if all particles, specified by {@link #getDefaultCount()
     * getCount}, are emitted at the interval specified by
     * {@link #getDefaultInterval() getInterval}, when launching
     * sequentially. If false, only one particle gets emitted at each
     * interval.
     */
    boolean isPulsating();

    /**
     * Sets the position and gravity center of the new particle and applies the
     * particle settings.
     *
     * @param particle The new particle.
     * @param x        The X coordinate of the particle system.
     * @param y        The Y coordinate of the particle system.
     * @param z        The Z coordinate of the particle system.
     */
    void emit(Particle particle, float x, float y, float z);
}