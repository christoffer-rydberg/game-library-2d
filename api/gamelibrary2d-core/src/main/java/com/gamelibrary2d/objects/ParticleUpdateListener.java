package com.gamelibrary2d.objects;

import com.gamelibrary2d.particle.systems.DefaultParticleSystem;
import com.gamelibrary2d.particle.systems.Particle;

public interface ParticleUpdateListener {

    /**
     * This method is called whenever a particle has been updated (if registered to
     * the particle system). This can, for example, be used to apply lightning
     * effects and/or collision detection. If any of the listeners returns true, the
     * particle will be destroyed.
     *
     * @param system   The particle system owning the particle.
     * @param particle The updated particle.
     * @return True if the particle has collided and should be destroyed.
     */
    boolean updated(DefaultParticleSystem system, Particle particle);

}