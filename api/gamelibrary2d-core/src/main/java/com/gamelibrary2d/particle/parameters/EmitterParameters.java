package com.gamelibrary2d.particle.parameters;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Serializable;

/**
 * Parameters for particle emission.
 */
public class EmitterParameters implements Serializable {
    private int defaultCount;
    private int defaultCountVar;
    private float defaultInterval;
    private boolean pulsating;
    private float offsetX;
    private float offsetXVar;
    private float offsetY;
    private float offsetYVar;

    public EmitterParameters() {
        defaultCount = 1;
        defaultInterval = 1;
    }

    public EmitterParameters(DataBuffer buffer) {
        defaultCount = buffer.getInt();
        defaultCountVar = buffer.getInt();
        defaultInterval = buffer.getFloat();
        pulsating = buffer.getBool();
        offsetX = buffer.getFloat();
        offsetXVar = buffer.getFloat();
        offsetY = buffer.getFloat();
        offsetYVar = buffer.getFloat();
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetXVar() {
        return offsetXVar;
    }

    public void setOffsetXVar(float offsetXVar) {
        this.offsetXVar = offsetXVar;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetYVar() {
        return offsetYVar;
    }

    public void setOffsetYVar(float offsetYVar) {
        this.offsetYVar = offsetYVar;
    }

    public int getDefaultCount() {
        return defaultCount;
    }

    public void setDefaultCount(int defaultCount) {
        this.defaultCount = defaultCount;
    }

    public int getDefaultCountVar() {
        return defaultCountVar;
    }

    public void setDefaultCountVar(int defaultCountVar) {
        this.defaultCountVar = defaultCountVar;
    }

    /**
     * @return The default interval, in seconds, between sequentially emitted particles.
     */
    public float getDefaultInterval() {
        return defaultInterval;
    }

    /**
     * Sets the {@link #getDefaultInterval default interval}.
     */
    public void setDefaultInterval(float defaultInterval) {
        this.defaultInterval = defaultInterval;
    }

    /**
     * @return True if all particles, specified by {@link #getDefaultCount() getCount},
     * are emitted at the interval specified by {@link #getDefaultInterval() getInterval},
     * when launching sequentially. If false, only one particle gets emitted at each interval.
     */
    public boolean isPulsating() {
        return pulsating;
    }

    /**
     * Sets the {@link #isPulsating() pulsating} field.
     */
    public void setPulsating(boolean pulsating) {
        this.pulsating = pulsating;
    }

    @Override
    public void serialize(DataBuffer buffer) {
        buffer.putInt(defaultCount);
        buffer.putInt(defaultCountVar);
        buffer.putFloat(defaultInterval);
        buffer.putBool(pulsating);
        buffer.putFloat(offsetX);
        buffer.putFloat(offsetXVar);
        buffer.putFloat(offsetY);
        buffer.putFloat(offsetYVar);
    }
}
