package com.gamelibrary2d.sound.lwjgl;

import com.gamelibrary2d.common.Point;
import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.sound.SoundSource;

import static org.lwjgl.openal.AL10.*;

/**
 * This class represents a sound source. It contains methods to specify the
 * speed, position and volume of the source, as well as the underlying
 * {@link DefaultSoundBuffer}. Speed and position are taken into account only if the
 * {@link DefaultSoundManager} has an attached {@link SoundListener}.
 */
public class DefaultSoundSource extends AbstractDisposable implements SoundSource<DefaultSoundBuffer> {

    private final int sourceId;

    private DefaultSoundBuffer soundBuffer;

    private float volume = 1f;

    private DefaultSoundSource(int sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * Creates a new instance of {@link DefaultSoundSource}.
     *
     * @return The created {@link DefaultSoundSource} instance.
     */
    static DefaultSoundSource create(Disposer disposer) {
        int sourceId = alGenSources();
        DefaultSoundSource source = new DefaultSoundSource(sourceId);
        disposer.registerDisposal(source);
        return source;
    }

    @Override
    public boolean isLooping() {
        return alGetSourcei(sourceId, AL_LOOPING) == AL_TRUE ? true : false;
    }

    /**
     * Determines if the sound will play continuously, or if it will play only once and then stop.
     */
    public void setLooping(boolean looping) {
        alSourcei(sourceId, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);

    }

    /**
     * Determines if the speed and position of the sound source will be interpreted
     * as relative to the velocity and position of the {@link SoundListener}.
     */
    public void setRelative(boolean relative) {
        alSourcei(sourceId, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
    }

    /**
     * The sound buffer contains the sound data that is played.
     */
    @Override
    public DefaultSoundBuffer getSoundBuffer() {
        return soundBuffer;
    }

    /**
     * Setter for the {@link #getSoundBuffer() sound buffer}.
     */
    @Override
    public void setSoundBuffer(DefaultSoundBuffer soundBuffer) {
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
     * @param velocity The velocity along the x-axis.
     */
    public void setVelocity(Point velocity) {
        alSource3f(sourceId, AL_VELOCITY, velocity.getX(), velocity.getY(), 0);
    }

    @Override
    public float getVolume() {
        return volume;
    }

    /**
     * Sets the volume of the sound source.
     *
     * @param volume
     */
    @Override
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
    @Override
    public boolean isStopped() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        return state == AL_STOPPED || state == AL_INITIAL;
    }

    @Override
    protected void onDispose() {
        stop();
        alDeleteSources(sourceId);
    }

    @Override
    public void stop() {
        if (!isStopped()) {
            alSourceStop(sourceId);
        }
    }

    @Override
    public void play() {
        if (!isPlaying()) {
            alSourcePlay(sourceId);
        }
    }

    @Override
    public void pause() {
        if (!isPaused()) {
            alSourcePause(sourceId);
        }
    }
}