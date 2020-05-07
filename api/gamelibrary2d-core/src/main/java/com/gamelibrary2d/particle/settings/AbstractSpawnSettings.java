package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.particle.systems.Particle;

public abstract class AbstractSpawnSettings implements ParticleSpawnSettings {

    private float offsetX;

    private float offsetY;

    private float offsetZ;

    private int defaultCount = 1;

    private int defaultCountVar = 0;

    private float defaultInterval = 1;

    private boolean pulsating;

    private boolean localGravityCenter;

    protected AbstractSpawnSettings() {

    }

    protected AbstractSpawnSettings(AbstractSpawnSettings other) {
        defaultCount = other.defaultCount;
        defaultInterval = other.defaultInterval;
        pulsating = other.pulsating;
        localGravityCenter = other.localGravityCenter;
    }

    protected AbstractSpawnSettings(DataBuffer buffer) {
        consumeHeader(buffer);
        offsetX = buffer.getFloat();
        offsetY = buffer.getFloat();
        offsetZ = buffer.getFloat();
        defaultCount = buffer.getInt();
        defaultCountVar = buffer.getInt();
        defaultInterval = buffer.getFloat();
        pulsating = buffer.get() == 1;
        localGravityCenter = buffer.get() == 1;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(float offsetZ) {
        this.offsetZ = offsetZ;
    }

    /**
     * Consumes the header from the byte buffer. If the header does not exist, the
     * byte buffer is kept as it is.
     */
    private void consumeHeader(DataBuffer buffer) {
        int header = buffer.getInt();
        if (header != getIOHeader()) {
            buffer.position(buffer.position() - Integer.BYTES);
        }
    }

    @Override
    public final void serialize(DataBuffer buffer) {
        buffer.putInt(getIOHeader());
        buffer.putFloat(offsetX);
        buffer.putFloat(offsetY);
        buffer.putFloat(offsetZ);
        buffer.putInt(defaultCount);
        buffer.putInt(defaultCountVar);
        buffer.putFloat(defaultInterval);
        buffer.put((byte) (pulsating ? 1 : 0));
        buffer.put((byte) (localGravityCenter ? 1 : 0));
        onSerialize(buffer);
    }

    @Override
    public int getDefaultCount() {
        return defaultCount;
    }

    public void setDefaultCount(int defaultCount) {
        this.defaultCount = defaultCount;
    }

    @Override
    public int getDefaultCountVar() {
        return defaultCountVar;
    }

    public void setDefaultCountVar(int defaultCountVar) {
        this.defaultCountVar = defaultCountVar;
    }

    @Override
    public float getDefaultInterval() {
        return defaultInterval;
    }

    public void setDefaultInterval(float defaultInterval) {
        this.defaultInterval = defaultInterval;
    }

    @Override
    public boolean isPulsating() {
        return pulsating;
    }

    public void setPulsating(boolean pulsating) {
        this.pulsating = pulsating;
    }

    public boolean isLocalGravityCenter() {
        return localGravityCenter;
    }

    public void setLocalGravityCenter(boolean localGravityCenter) {
        this.localGravityCenter = localGravityCenter;
    }

    @Override
    public void emit(Particle particle, float x, float y, float z) {
        x += offsetX;
        y += offsetY;
        z += offsetZ;

        initialize(particle, x, y, z);

        if (isLocalGravityCenter()) {
            particle.setGravityCenter(particle.getPosX(), particle.getPosY(), particle.getPosZ());
        } else {
            particle.setGravityCenter(x, y, z);
        }
    }

    protected abstract int getIOHeader();

    protected abstract void onSerialize(DataBuffer buffer);

    /**
     * This method is responsible for setting the initial position of the emitted
     * particle, as well as the gravity center. No other particle properties should
     * be set inside this method, as they will be overridden by the particle
     * settings. Nevertheless, it is possible to update the particle settings from
     * this method in order to customize, for example, the particle color based on
     * the initial position. Warning: The particle settings can potentially be used
     * in other particle systems, be aware of this if making changes to the
     * settings.
     *
     * @param particle The emitted particle.
     * @param x        The X coordinate of the particle system.
     * @param y        The Y coordinate of the particle system.
     * @param z        The Z coordinate of the particle system.
     */
    protected abstract void initialize(Particle particle, float x, float y, float z);
}