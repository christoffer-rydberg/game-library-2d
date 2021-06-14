package com.gamelibrary2d.components.objects;

import com.gamelibrary2d.framework.Renderable;
import com.gamelibrary2d.components.denotations.Bounded;
import com.gamelibrary2d.components.denotations.Opacifiable;
import com.gamelibrary2d.components.denotations.Transformable;

/**
 * Defines a {@link Renderable} object that can be positioned, scaled and rotated.
 */
public interface GameObject extends Renderable, Bounded, Transformable, Opacifiable {

    /**
     * Checks if the object is enabled. Disabled objects are not rendered or updated.
     */
    boolean isEnabled();

    /**
     * Sets if the object is {@link #isEnabled() enabled}.
     */
    void setEnabled(boolean enabled);

}