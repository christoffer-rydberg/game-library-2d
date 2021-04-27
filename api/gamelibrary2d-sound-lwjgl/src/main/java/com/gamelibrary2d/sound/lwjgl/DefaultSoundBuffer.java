package com.gamelibrary2d.sound.lwjgl;

import com.gamelibrary2d.common.disposal.AbstractDisposable;
import com.gamelibrary2d.sound.SoundBuffer;

import static org.lwjgl.openal.AL10.alDeleteBuffers;

/**
 * Represents an OpenAL sound buffer which is stored on the GPU.
 */
public class DefaultSoundBuffer extends AbstractDisposable implements SoundBuffer {

    private final int bufferId;

    /**
     * Creates a new instance of {@link DefaultSoundBuffer}.
     *
     * @param bufferId The OpenAl sound buffer identifier.
     */
    public DefaultSoundBuffer(int bufferId) {
        this.bufferId = bufferId;
    }

    /**
     * Retrieves the identifier of the OpenAl sound buffer.
     */
    int getBufferId() {
        return bufferId;
    }

    @Override
    protected void onDispose() {
        alDeleteBuffers(bufferId);
    }
}