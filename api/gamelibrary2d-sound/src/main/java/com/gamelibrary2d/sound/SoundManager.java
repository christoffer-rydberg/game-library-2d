package com.gamelibrary2d.sound;

import com.gamelibrary2d.common.disposal.AbstractDisposer;
import com.gamelibrary2d.common.disposal.Disposable;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.sound.decoders.AudioDecoder;
import org.lwjgl.openal.*;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundManager extends AbstractDisposer implements Disposable {

    private final long device;
    private final long context;
    private final HashMap<URL, SoundBuffer> soundBufferMap;
    private final List<SoundSource> soundSources;

    private SoundListener listener;

    private SoundManager(long device, long context) {
        this.device = device;
        this.context = context;
        soundBufferMap = new HashMap<>();
        soundSources = new ArrayList<>();
    }

    public static SoundManager create(Disposer disposer) {
        long device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        long context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }

        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

        SoundManager soundManager = new SoundManager(device, context);
        disposer.registerDisposal(soundManager);
        return soundManager;
    }

    /**
     * Creates a new instance of {@link SoundSource}.
     *
     * @param loop     Determines if the sound will play continuously, or if it will
     *                 play only once and then stop.
     * @param relative Determines if the speed and position of the sound source will
     *                 be interpreted as relative to the velocity and position of
     *                 the {@link SoundListener}.
     * @return The created {@link SoundSource} instance.
     */
    public SoundSource createSoundSource(boolean loop, boolean relative) {
        SoundSource source = SoundSource.create(loop, relative);
        soundSources.add(source);
        return source;
    }

    /**
     * Disposes the specified sound source. All sound sources will automatically be
     * disposed when the {@link SoundManager} is disposed, however, this method can
     * be used to dispose a sound source earlier. It will also remove the sound
     * source from the {@link SoundManager}.
     *
     * @param source The sound source.
     */
    public void disposeSoundSource(SoundSource source) {
        soundSources.remove(source);
        source.dispose();
    }

    /**
     * Starts playing the specified {@link SoundSource}.
     */
    public void play(SoundSource soundSource) {
        soundSource.play();
    }

    /**
     * Stops playing the specified {@link SoundSource}.
     */
    public void stop(SoundSource soundSource) {
        soundSource.stop();
    }

    /**
     * Pauses the specified {@link SoundSource}.
     */
    public void pause(SoundSource soundSource) {
        soundSource.pause();
    }

    /**
     * Checks if a {@link SoundBuffer} exists for the specified URL. If not, the
     * sound buffer is loaded, {@link #registerSoundBuffer(URL, SoundBuffer)
     * registered} and returned. Otherwise, the already existing sound buffer is
     * returned.
     *
     * @param source  The URL of the audio data.
     * @param decoder The decoder that will be used to decode the audio data.
     * @return The created or retrieved {@link SoundBuffer}.
     */
    public SoundBuffer loadSoundBuffer(URL source, AudioDecoder decoder) throws IOException {
        SoundBuffer soundBuffer = getSoundBuffer(source);
        if (soundBuffer == null) {
            soundBuffer = decoder.decode(source, this);
            registerSoundBuffer(source, soundBuffer);
        }
        return soundBuffer;
    }

    /**
     * Registers the specified {@link SoundBuffer}. Note that this method is
     * automatically called when a {@link SoundBuffer} is created with the
     * {@link #loadSoundBuffer} method.
     *
     * @param identifier  The {@link URL} identifier.
     * @param soundBuffer The sound buffer.
     */
    public void registerSoundBuffer(URL identifier, SoundBuffer soundBuffer) {
        soundBufferMap.put(identifier, soundBuffer);
    }

    /**
     * Retrieves a sound buffer registered to the specified {@link URL} identifier.
     */
    public SoundBuffer getSoundBuffer(URL identifier) {
        return soundBufferMap.get(identifier);
    }

    /**
     * Gets the attached {@link SoundListener}. The sound listener is automatically
     * activated/deactivated when it is attached/detached to the sound manager.
     */
    public SoundListener getListener() {
        return this.listener;
    }

    /**
     * Setter for the {@link #getListener() attached sound listener}.
     */
    public void setListener(SoundListener listener) {
        if (this.listener == listener) {
            return;
        }

        if (this.listener != null) {
            this.listener.setActive(false);
        }

        this.listener = listener;

        if (this.listener != null) {
            alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
            this.listener.setActive(true);
        } else {
            alDistanceModel(AL10.AL_NONE);
        }
    }

    @Override
    protected void onDispose() {
        soundSources.clear();
        soundBufferMap.clear();

        if (context != NULL) {
            alcDestroyContext(context);
        }

        if (device != NULL) {
            alcCloseDevice(device);
        }
    }
}