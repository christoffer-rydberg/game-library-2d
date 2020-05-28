package com.gamelibrary2d.particle.settings;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Serializable;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.particle.systems.Particle;

public class ParticlePositioner implements Serializable {
    private float spawnAreaOffsetX, spawnAreaOffsetY;
    private float spawnAreaWidth, spawnAreaHeight;
    private float spawnAreaWidthVar, spawnAreaHeightVar;

    private boolean localCenter;
    private float spawnAngle;
    private float spawnAngleVar;
    private SpawnArea spawnArea;

    public ParticlePositioner() {
        spawnArea = SpawnArea.RECTANGLE;
    }

    public ParticlePositioner(DataBuffer buffer) {
        spawnAreaOffsetX = buffer.getFloat();
        spawnAreaOffsetY = buffer.getFloat();
        localCenter = buffer.get() == 1;

        spawnAngle = buffer.getFloat();
        spawnAngleVar = buffer.getFloat();
        spawnAreaWidth = buffer.getFloat();
        spawnAreaHeight = buffer.getFloat();
        spawnAreaWidthVar = buffer.getFloat();
        spawnAreaHeightVar = buffer.getFloat();
        spawnArea = buffer.getEnum(SpawnArea.class);
    }

    @Override
    public final void serialize(DataBuffer buffer) {
        buffer.putFloat(spawnAreaOffsetX);
        buffer.putFloat(spawnAreaOffsetY);
        buffer.put((byte) (localCenter ? 1 : 0));

        buffer.putFloat(spawnAngle);
        buffer.putFloat(spawnAngleVar);
        buffer.putFloat(spawnAreaWidth);
        buffer.putFloat(spawnAreaHeight);
        buffer.putFloat(spawnAreaWidthVar);
        buffer.putFloat(spawnAreaHeightVar);
        buffer.putEnum(spawnArea);
    }

    public SpawnArea getSpawnArea() {
        return spawnArea;
    }

    public void setSpawnArea(SpawnArea spawnArea) {
        this.spawnArea = spawnArea;
    }

    public float getSpawnAreaOffsetX() {
        return spawnAreaOffsetX;
    }

    public void setSpawnAreaOffset(float spawnAreaOffsetX) {
        this.spawnAreaOffsetX = spawnAreaOffsetX;
    }

    public float getSpawnAreaOffsetY() {
        return spawnAreaOffsetY;
    }

    public void setSpawnAreaOffsetY(float spawnAreaOffsetY) {
        this.spawnAreaOffsetY = spawnAreaOffsetY;
    }

    public boolean isLocalCenter() {
        return localCenter;
    }

    public void setLocalCenter(boolean localCenter) {
        this.localCenter = localCenter;
    }

    public void initialize(Particle particle, float x, float y) {
        x += spawnAreaOffsetX;
        y += spawnAreaOffsetY;

        double angle = getSpawnAngle() - getSpawnAngleVar() * RandomInstance.random11() - 90;
        double angleRadians = angle * Math.PI / 180d;

        switch (spawnArea) {
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
        return spawnAngle;
    }

    public void setSpawnAngle(float spawnAngle) {
        this.spawnAngle = spawnAngle;
    }

    public float getSpawnAngleVar() {
        return spawnAngleVar;
    }

    public void setSpawnAngleVar(float spawnAngleVar) {
        this.spawnAngleVar = spawnAngleVar;
    }

    public float getSpawnAreaWidth() {
        return spawnAreaWidth;
    }

    public void setSpawnAreaWidth(float spawnAreaWidth) {
        this.spawnAreaWidth = spawnAreaWidth;
    }

    public float getSpawnAreaHeight() {
        return spawnAreaHeight;
    }

    public void setSpawnAreaHeight(float spawnAreaHeight) {
        this.spawnAreaHeight = spawnAreaHeight;
    }

    public float getSpawnAreaWidthVar() {
        return spawnAreaWidthVar;
    }

    public void setSpawnAreaWidthVar(float spawnAreaWidthVar) {
        this.spawnAreaWidthVar = spawnAreaWidthVar;
    }

    public float getSpawnAreaHeightVar() {
        return spawnAreaHeightVar;
    }

    public void setSpawnAreaHeightVar(float spawnAreaHeightVar) {
        this.spawnAreaHeightVar = spawnAreaHeightVar;
    }

    public void scale(float factor) {
        spawnAreaOffsetX *= factor;
        spawnAreaOffsetY *= factor;
        spawnAreaWidth *= factor;
        spawnAreaHeight *= factor;
        spawnAreaWidthVar *= factor;
        spawnAreaHeightVar *= factor;
    }

    private void positionInRectangleArea(Particle particle, float centerX, float centerY, double angleRadians) {
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
        // Randomize radius based on variation
        float spawnAreaWidth = this.spawnAreaWidth + spawnAreaWidthVar * RandomInstance.random11();
        float spawnAreaHeight = this.spawnAreaHeight + spawnAreaHeightVar * RandomInstance.random11();

        // Create an ellipse
        var posX = Math.cos(angleRadians) * spawnAreaWidth;
        var posY = -Math.sin(angleRadians) * spawnAreaHeight;

        particle.setPosition(centerX + (float) posX, centerY + (float) posY);
    }

    public enum SpawnArea {
        RECTANGLE,
        ELLIPSE
    }
}