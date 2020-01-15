package com.gamelibrary2d.sound;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.AbstractDisposable;

import static org.lwjgl.openal.AL10.*;

/**
 * This class represents a sound source. It contains methods to specify the
 * speed, position and volume of the source, as well as the underlying
 * {@link SoundBuffer}. Speed and position are taken into account only if the
 * {@link SoundManager} has an attached {@link SoundListener}. A sound source
 * can be played by invoking {@link SoundManager#play(SoundSource)}.
 *
 * @author Christoffer Rydberg
 */
public class SoundSource extends AbstractDisposable {

    private final int sourceId;

    private SoundBuffer soundBuffer;

    private float volume = 1f;

    private SoundSource(int sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * Creates a new instance of {@link SoundSource}. This method is internal to
     * only give the {@link SoundManager} permission to create new sound sources,
     * and in such way give it full control the sound sources and their disposal. To
     * create a {@link SoundSource}, see
     * {@link SoundManager#createSoundSource(boolean, boolean)}.
     *
     * @param loop     Determines if the sound will play continuously, or if it will
     *                 play only once and then stop.
     * @param relative Determines if the speed and position of the sound source will
     *                 be interpreted as relative to the velocity and position of
     *                 the {@link SoundListener}.
     * @return The created {@link SoundSource} instance.
     */
    static SoundSource create(boolean loop, boolean relative) {
        int sourceId = alGenSources();
        alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        alSourcei(sourceId, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
        return new SoundSource(sourceId);
    }

    /**
     * The sound buffer contains the sound data that is played.
     */
    public SoundBuffer getSoundBuffer() {
        return soundBuffer;
    }

    /**
     * Setter for the {@link #getSoundBuffer() sound buffer}.
     */
    public void setSoundBuffer(SoundBuffer soundBuffer) {
        this.soundBuffer = soundBuffer;
        alSourcei(sourceId, AL_BUFFER, soundBuffer != null ? soundBuffer.getBufferId() : 0);
    }

    /**
     * Sets the position of the sound source.
     *
     * @param position The position.
     */
    public void setPosition(Point position) {
        alSource3f(sourceId, AL_POSITION, position.getX(), position.getY(), 0);
    }

    /**
     * Sets the velocity of the sound source.
     *
     * @param x The velocity along the x-axis.
     * @param y The velocity along the y-axis.
     * @param z The velocity along the z-axis.
     */
    public void setVelocity(Point velocity) {
        alSource3f(sourceId, AL_VELOCITY, velocity.getX(), velocity.getY(), 0);
    }

    public float getVolume() {
        return volume;
    }

    /**
     * Sets the volume of the sound source.
     *
     * @param volume
     */
    public void setVolume(float volume) {
        this.volume = volume;
        alSourcef(sourceId, AL_GAIN, volume);
    }

    /**
     * This method can be used to set an arbitrary property to the OpenAL sound
     * source.
     *
     * @param param The property parameter.
     * @param value The property value.
     */
    public void setProperty(int param, float value) {
        alSourcef(sourceId, param, value);
    }

    /**
     * Checks if the sound source is currently playing.
     */
    public boolean isPlaying() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    /**
     * Checks if the sound source is currently paused.
     */
    public boolean isPaused() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PAUSED;
    }

    /**
     * Checks if the sound source is currently stopped.
     */
    public boolean isStopped() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        return state == AL_STOPPED || state == AL_INITIAL;
    }

    @Override
    protected void onDispose() {
        stop();
        alDeleteSources(sourceId);
    }

    void stop() {
        if (!isStopped()) {
            alSourceStop(sourceId);
        }
    }

    void play() {
        if (!isPlaying()) {
            alSourcePlay(sourceId);
        }
    }

    void pause() {
        if (!isPaused()) {
            alSourcePause(sourceId);
        }
    }
}