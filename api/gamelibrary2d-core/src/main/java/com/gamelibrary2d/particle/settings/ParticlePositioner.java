package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Serializable;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.particle.systems.Particle;

public class ParticlePositioner implements Serializable {
    private static final int STRIDE = 10;

    private static final int SPAWN_AREA_OFFSET_X = 0;
    private static final int SPAWN_AREA_OFFSET_Y = 1;
    private static final int SPAWN_ANGLE = 2;
    private static final int SPAWN_ANGLE_VAR = 3;

    private static final int SPAWN_AREA_WIDTH = 4;
    private static final int SPAWN_AREA_WIDTH_VAR = 5;
    private static final int SPAWN_AREA_HEIGHT = 6;
    private static final int SPAWN_AREA_HEIGHT_VAR = 7;

    private static final int SPAWN_AREA = 8;
    private static final int LOCAL_CENTER = 9;

    private SpawnArea spawnArea;
    private float cachedSpawnArea = -1;

    private float[] internalState = new float[STRIDE];
    private int updateCounter;

    public ParticlePositioner() {
        spawnArea = SpawnArea.RECTANGLE;
    }

    public ParticlePositioner(DataBuffer buffer) {
        for (int i = 0; i < STRIDE; ++i) {
            internalState[i] = buffer.getFloat();
        }
    }

    @Override
    public final void serialize(DataBuffer buffer) {
        for (int i = 0; i < STRIDE; ++i) {
            buffer.putFloat(internalState[i]);
        }
    }

    public float[] getInternalStateArray() {
        return internalState;
    }

    public SpawnArea getSpawnArea() {
        var value = internalState[SPAWN_AREA];
        if (value != cachedSpawnArea) {
            setSpawnAreaFromFloatValue(value);
        }

        return spawnArea;
    }

    public void setSpawnArea(SpawnArea spawnArea) {
        switch (spawnArea) {
            case RECTANGLE:
                cachedSpawnArea = 0;
                break;
            case ELLIPSE:
                cachedSpawnArea = 1;
                break;
        }
        this.spawnArea = spawnArea;
    }

    private void setSpawnAreaFromFloatValue(float value) {
        cachedSpawnArea = value;
        if (value == 0f) {
            spawnArea = SpawnArea.RECTANGLE;
        } else {
            spawnArea = SpawnArea.ELLIPSE;
        }
    }

    public float getSpawnAreaOffsetX() {
        return internalState[SPAWN_AREA_OFFSET_X];
    }

    public void setSpawnAreaOffsetX(float spawnAreaOffsetX) {
        setInternalState(SPAWN_AREA_OFFSET_X, spawnAreaOffsetX);
    }

    private void setInternalState(int index, float value) {
        ++updateCounter;
        internalState[index] = value;
    }

    /**
     * The update counter is incremented whenever a parameter is changed.
     */
    public int getUpdateCounter() {
        return updateCounter;
    }

    public float getSpawnAreaOffsetY() {
        return internalState[SPAWN_AREA_OFFSET_Y];
    }

    public void setSpawnAreaOffsetY(float spawnAreaOffsetY) {
        setInternalState(SPAWN_AREA_OFFSET_Y, spawnAreaOffsetY);
    }

    public boolean isLocalCenter() {
        return internalState[LOCAL_CENTER] != 0;
    }

    public void setLocalCenter(boolean localCenter) {
        setInternalState(LOCAL_CENTER, localCenter ? 1 : 0);
    }

    public void initialize(Particle particle, float x, float y) {
        x += getSpawnAreaOffsetX();
        y += getSpawnAreaOffsetY();

        double angle = getSpawnAngle() - getSpawnAngleVar() * RandomInstance.random11() - 90;
        double angleRadians = angle * Math.PI / 180d;

        switch (getSpawnArea()) {
            case RECTANGLE:
                positionInRectangleArea(particle, x, y, angleRadians);
                break;
            case ELLIPSE:
                positionInEllipseArea(particle, x, y, angleRadians);
                break;
        }

        if (isLocalCenter()) {
            particle.setCenter(particle.getPosX(), particle.getPosY());
        } else {
            particle.setCenter(x, y);
        }
    }

    public float getSpawnAngle() {
        return internalState[SPAWN_ANGLE];
    }

    public void setSpawnAngle(float spawnAngle) {
        setInternalState(SPAWN_ANGLE, spawnAngle);
    }

    public float getSpawnAngleVar() {
        return internalState[SPAWN_ANGLE_VAR];
    }

    public void setSpawnAngleVar(float spawnAngleVar) {
        setInternalState(SPAWN_ANGLE_VAR, spawnAngleVar);
    }

    public float getSpawnAreaWidth() {
        return internalState[SPAWN_AREA_WIDTH];
    }

    public void setSpawnAreaWidth(float spawnAreaWidth) {
        setInternalState(SPAWN_AREA_WIDTH, spawnAreaWidth);
    }

    public float getSpawnAreaHeight() {
        return internalState[SPAWN_AREA_HEIGHT];
    }

    public void setSpawnAreaHeight(float spawnAreaHeight) {
        setInternalState(SPAWN_AREA_HEIGHT, spawnAreaHeight);
    }

    public float getSpawnAreaWidthVar() {
        return internalState[SPAWN_AREA_WIDTH_VAR];
    }

    public void setSpawnAreaWidthVar(float spawnAreaWidthVar) {
        setInternalState(SPAWN_AREA_WIDTH_VAR, spawnAreaWidthVar);
    }

    public float getSpawnAreaHeightVar() {
        return internalState[SPAWN_AREA_HEIGHT_VAR];
    }

    public void setSpawnAreaHeightVar(float spawnAreaHeightVar) {
        setInternalState(SPAWN_AREA_HEIGHT_VAR, spawnAreaHeightVar);
    }

    public void scale(float factor) {
        setSpawnAreaOffsetX(getSpawnAreaOffsetX() * factor);
        setSpawnAreaOffsetY(getSpawnAreaOffsetY() * factor);
        setSpawnAreaWidth(getSpawnAreaWidth() * factor);
        setSpawnAreaHeight(getSpawnAreaHeight() * factor);
        setSpawnAreaWidthVar(getSpawnAreaWidthVar() * factor);
        setSpawnAreaHeightVar(getSpawnAreaHeightVar() * factor);
    }

    private void positionInRectangleArea(Particle particle, float centerX, float centerY, double angleRadians) {
        var spawnAreaWidth = getSpawnAreaWidth();
        var spawnAreaWidthVar = getSpawnAreaWidthVar();
        var spawnAreaHeight = getSpawnAreaHeight();
        var spawnAreaHeightVar = getSpawnAreaHeightVar();

        // Create a circle that is touching the corners of the rectangle
        var radius = Math.sqrt(spawnAreaWidth * spawnAreaWidth + spawnAreaHeight * spawnAreaHeight);
        var posX = Math.cos(angleRadians) * radius;
        var posY = -Math.sin(angleRadians) * radius;

        // Cut the circle horizontally and vertically from the rectangle corners.
        if (Math.abs(posX) > spawnAreaWidth)
            posX = posX < 0 ? -spawnAreaWidth : spawnAreaWidth;
        if (Math.abs(posY) > spawnAreaHeight)
            posY = posY < 0 ? -spawnAreaHeight : spawnAreaHeight;

        // Randomize based on variation
        posX += spawnAreaWidthVar * RandomInstance.random11();
        posY += spawnAreaHeightVar * RandomInstance.random11();

        particle.setPosition(centerX + (float) posX, centerY + (float) posY);
    }

    private void positionInEllipseArea(Particle particle, float centerX, float centerY, double angleRadians) {
        var spawnAreaWidth = getSpawnAreaWidth();
        var spawnAreaWidthVar = getSpawnAreaWidthVar();
        var spawnAreaHeight = getSpawnAreaHeight();
        var spawnAreaHeightVar = getSpawnAreaHeightVar();

        // Randomize radius based on variation
        float width = spawnAreaWidth + spawnAreaWidthVar * RandomInstance.random11();
        float height = spawnAreaHeight + spawnAreaHeightVar * RandomInstance.random11();

        // Create an ellipse
        var posX = Math.cos(angleRadians) * width;
        var posY = -Math.sin(angleRadians) * height;

        particle.setPosition(centerX + (float) posX, centerY + (float) posY);
    }

    public enum SpawnArea {
        RECTANGLE,
        ELLIPSE
    }
}