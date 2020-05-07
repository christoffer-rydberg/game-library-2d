package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.particle.systems.Particle;

public class BasicSpawnSettings extends AbstractSpawnSettings {

    /**
     * Header, used when serializing {@link BasicSpawnSettings} to a byte buffer.
     */
    static final int IO_HEADER = 161359846;

    private final float[] positionVar = new float[3];

    private float positionYaw;
    private float positionYawVar;

    private float positionPitch;
    private float positionPitchVar;

    /**
     * Default constructor
     */
    public BasicSpawnSettings() {

    }

    /**
     * Copy constructor
     */
    public BasicSpawnSettings(BasicSpawnSettings other) {
        super(other);
        positionYaw = other.positionYaw;
        positionYawVar = other.positionYawVar;
        positionPitch = other.positionPitch;
        positionPitchVar = other.positionPitchVar;
        positionVar[0] = other.positionVar[0];
        positionVar[1] = other.positionVar[1];
        positionVar[2] = other.positionVar[2];
    }

    /**
     * Copies settings from the abstract base class.
     */
    public BasicSpawnSettings(AbstractSpawnSettings other) {
        super(other);
    }

    /**
     * Copies settings from the serialized buffer.
     */
    public BasicSpawnSettings(DataBuffer buffer) {
        super(buffer);
        positionYaw = buffer.getFloat();
        positionYawVar = buffer.getFloat();
        positionPitch = buffer.getFloat();
        positionPitchVar = buffer.getFloat();
        positionVar[0] = buffer.getFloat();
        positionVar[1] = buffer.getFloat();
        positionVar[2] = buffer.getFloat();
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
        buffer.putFloat(positionVar[0]);
        buffer.putFloat(positionVar[1]);
        buffer.putFloat(positionVar[2]);
    }

    public float[] getPositionVar() {
        return positionVar;
    }

    public void setPositionVar(float x, float y, float z) {
        positionVar[0] = x;
        positionVar[1] = y;
        positionVar[2] = z;
    }

    public float getPositionYaw() {
        return positionYaw;
    }

    public void setPositionYaw(float yaw) {
        this.positionYaw = yaw;
    }

    public float getPositionYawVar() {
        return positionYawVar;
    }

    public void setPositionYawVar(float yawVar) {
        this.positionYawVar = yawVar;
    }

    public float getPositionPitch() {
        return positionPitch;
    }

    public void setPositionPitch(float pitch) {
        this.positionPitch = pitch;
    }

    public float getPositionPitchVar() {
        return positionPitchVar;
    }

    public void setPositionPitchVar(float pitchVar) {
        this.positionPitchVar = pitchVar;
    }

    @Override
    protected void initialize(Particle particle, float x, float y, float z) {
        float xVar = getPositionVar()[0];
        float yVar = getPositionVar()[1];
        float zVar = getPositionVar()[2];

        if (xVar != 0 || yVar != 0 || zVar != 0) {

            float xOffset = xVar * RandomInstance.random11();
            float yOffset = yVar * RandomInstance.random11();
            float zOffset = zVar * RandomInstance.random11();

            float posYawDegrees = getPositionYaw() + getPositionYawVar() * RandomInstance.random11();
            float posPitchDegrees = getPositionPitch() + getPositionPitchVar() * RandomInstance.random11();

            double posYawRadians = -posYawDegrees * Math.PI / 180d;
            double posPitchRadians = posPitchDegrees * Math.PI / 180d;

            // Update pitch
            double xOffset2 = Math.cos(posPitchRadians) * xOffset + Math.sin(posPitchRadians) * zOffset;
            double yOffset2 = yOffset;
            double zOffset2 = -Math.sin(posPitchRadians) * xOffset + Math.cos(posPitchRadians) * zOffset;

            // Update yaw
            double xOffset3 = Math.cos(posYawRadians) * xOffset2 - Math.sin(posYawRadians) * yOffset2;
            double yOffset3 = Math.sin(posYawRadians) * xOffset2 + Math.cos(posYawRadians) * yOffset2;
            double zOffset3 = zOffset2;

            particle.setPosition((float) (x + xOffset3), (float) (y + yOffset3), (float) (z + zOffset3));
        } else {
            particle.setPosition(x, y, z);
        }
    }
}