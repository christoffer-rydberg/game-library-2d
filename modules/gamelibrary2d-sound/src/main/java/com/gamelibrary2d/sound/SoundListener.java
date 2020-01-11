package com.gamelibrary2d.sound;

import com.gamelibrary2d.common.Point;

import static org.lwjgl.openal.AL10.*;

/**
 * This class is used to specify the position, speed and orientation of the
 * sound listener. This works in conjunction with {@link SoundSource} instances,
 * which are used to omit sound. The {@link SoundListener} is activated when
 * it's attached to the {@link SoundManager} by calling its
 * {@link SoundManager#setListener(SoundListener) setListener} method.
 *
 * @author Christoffer Rydberg
 */
public class SoundListener {

    private final float[] orientation = new float[6];

    private boolean active;

    private float posX;
    private float posY;
    private float posZ;

    private float speedX;
    private float speedY;
    private float speedZ;

    /**
     * Sets the velocity of the sound listener.
     *
     * @param velocity The velocity vector.
     */
    public void setVelocity(Point velocity) {
        setVelocity(velocity.getX(), velocity.getY(), 0);
    }

    /**
     * Sets the velocity of the sound listener.
     *
     * @param x The velocity along the x-axis.
     * @param y The velocity along the y-axis.
     * @param z The velocity along the z-axis.
     */
    public void setVelocity(float x, float y, float z) {
        speedX = x;
        speedY = y;
        speedZ = z;
        if (active) {
            updateVelocity();
        }
    }

    /**
     * Sets the position of the sound listener.
     *
     * @param position The position.
     */
    public void setPosition(Point position) {
        setPosition(position.getX(), position.getY(), 0);
    }

    /**
     * Sets the position of the sound listener.
     *
     * @param x The position's x-coordinate.
     * @param y The position's y-coordinate.
     * @param z The position's z-coordinate.
     */
    public void setPosition(float x, float y, float z) {
        posX = x;
        posY = y;
        posZ = z;
        if (active) {
            updatePosition();
        }
    }

    /**
     * Sets the orientation of the sound listener.
     *
     * @param front The front direction vector.
     * @param up    The up direction vector.
     */
    public void setOrientation(Point front, Point up) {
        orientation[0] = front.getX();
        orientation[1] = front.getY();
        orientation[2] = 0;
        orientation[3] = up.getX();
        orientation[4] = up.getY();
        orientation[5] = 0;
        if (active) {
            updateOrientation();
        }
    }

    /**
     * Internal method, invoked by {@link SoundManager} when the
     * {@link SoundListener} is attached/detached.
     *
     * @param active True if the sound listener is attached (active) and false if it is
     *               detached (not active).
     */
    void setActive(boolean active) {
        this.active = active;
        if (active) {
            updateVelocity();
            updatePosition();
            updateOrientation();
        }
    }

    private void updateVelocity() {
        alListener3f(AL_VELOCITY, speedX, speedY, speedZ);
    }

    private void updatePosition() {
        alListener3f(AL_POSITION, posX, posY, posZ);
    }

    private void updateOrientation() {
        alListenerfv(AL_ORIENTATION, orientation);
    }
}