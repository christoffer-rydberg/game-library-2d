package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.particle.systems.Particle;

public class EllipsoidSpawnSettings extends AbstractSpawnSettings {

    /**
     * Header, used when serializing {@link EllipsoidSpawnSettings} to a byte buffer.
     */
    static final int IO_HEADER = 34786656;

    private final float[] radius = new float[3];
    private final float[] radiusVar = new float[3];

    private float positionYaw;
    private float positionYawVar;

    private float positionPitch;
    private float positionPitchVar;

    /**
     * Default constructor
     */
    public EllipsoidSpawnSettings() {

    }

    /**
     * Copy constructor
     */
    public EllipsoidSpawnSettings(EllipsoidSpawnSettings other) {
        super(other);
        positionYaw = other.positionYaw;
        positionYawVar = other.positionYawVar;
        positionPitch = other.positionPitch;
        positionPitchVar = other.positionPitchVar;
        radius[0] = other.radius[0];
        radius[1] = other.radius[1];
        radius[2] = other.radius[2];
        radiusVar[0] = other.radiusVar[0];
        radiusVar[1] = other.radiusVar[1];
        radiusVar[2] = other.radiusVar[2];
    }

    /**
     * Copies settings from the abstract base class.
     */
    public EllipsoidSpawnSettings(AbstractSpawnSettings other) {
        super(other);
    }

    /**
     * Copies settings from the serialized buffer.
     */
    public EllipsoidSpawnSettings(DataBuffer buffer) {
        super(buffer);
        positionYaw = buffer.getFloat();
        positionYawVar = buffer.getFloat();
        positionPitch = buffer.getFloat();
        positionPitchVar = buffer.getFloat();
        radius[0] = buffer.getFloat();
        radius[1] = buffer.getFloat();
        radius[2] = buffer.getFloat();
        radiusVar[0] = buffer.getFloat();
        radiusVar[1] = buffer.getFloat();
        radiusVar[2] = buffer.getFloat();
    }

    @Override
    protected int getIOHeader() {
        return IO_HEADER;
    }

    @Override
    protected void onSerialize(DataBuffer buffer) {
        buffer.putFloat(positionYaw);
        buffer.putFloat(positionYawVar);
        buffer.putFloat(positionPitch);
        buffer.putFloat(positionPitchVar);
        buffer.putFloat(radius[0]);
        buffer.putFloat(radius[1]);
        buffer.putFloat(radius[2]);
        buffer.putFloat(radiusVar[0]);
        buffer.putFloat(radiusVar[1]);
        buffer.putFloat(radiusVar[2]);
    }

    public float getPositionYaw() {
        return positionYaw;
    }

    public void setPositionYaw(float positionYaw) {
        this.positionYaw = positionYaw;
    }

    public float getPositionYawVar() {
        return positionYawVar;
    }

    public void setPositionYawVar(float positionYawVar) {
        this.positionYawVar = positionYawVar;
    }

    public float getPositionPitch() {
        return positionPitch;
    }

    public void setPositionPitch(float positionPitch) {
        this.positionPitch = positionPitch;
    }

    public float getPositionPitchVar() {
        return positionPitchVar;
    }

    public void setPositionPitchVar(float positionPitchVar) {
        this.positionPitchVar = positionPitchVar;
    }

    public float[] getRadius() {
        return radius;
    }

    public void setRadius(float x, float y, float z) {
        radius[0] = x;
        radius[1] = y;
        radius[2] = z;
    }

    public float[] getRadiusVar() {
        return radiusVar;
    }

    public void setRadiusVar(float x, float y, float z) {
        radiusVar[0] = x;
        radiusVar[1] = y;
        radiusVar[2] = z;
    }

    @Override
    protected void initialize(Particle particle, float x, float y, float z) {
        float posYawDegrees = getPositionYaw() - getPositionYawVar() * RandomInstance.random11() - 90;
        double posYawRadians = posYawDegrees * Math.PI / 180d;

        float pitchDegrees = getPositionPitch() - getPositionPitchVar() * RandomInstance.random11();
        double pitchRadians = pitchDegrees * Math.PI / 180d;

        double cosYaw = Math.cos(posYawRadians);
        double sinYaw = Math.sin(posYawRadians);

        double cosPitch = Math.cos(pitchRadians);
        double sinPitch = Math.sin(pitchRadians);

        float radiusX = getRadius()[0] + getRadiusVar()[0] * RandomInstance.random11();
        float radiusY = getRadius()[1] + getRadiusVar()[1] * RandomInstance.random11();
        float radiusZ = getRadius()[2] + getRadiusVar()[2] * RandomInstance.random11();

        double cosYawTimesCosPitch = cosYaw * cosPitch;
        double sinYawTimesCosPitch = sinYaw * cosPitch;
        float posX = (float) (x + cosYawTimesCosPitch * radiusX);
        float posY = (float) (y - sinYawTimesCosPitch * radiusY);
        float posZ = (float) (z + sinPitch * radiusZ);

        particle.setPosition(posX, posY, posZ);
    }
}