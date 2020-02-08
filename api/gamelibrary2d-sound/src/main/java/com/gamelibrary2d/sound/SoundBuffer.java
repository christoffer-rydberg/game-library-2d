package com.gamelibrary2d.sound;

import com.gamelibrary2d.common.disposal.AbstractDisposable;

import static org.lwjgl.openal.AL10.alDeleteBuffers;

/**
 * Represents an OpenAL sound buffer which is stored on the GPU.
 */
public class SoundBuffer extends AbstractDisposable {

    private final int bufferId;

    /**
     * Creates a new instance of {@link SoundBuffer}.
     *
     * @param bufferId The OpenAl sound buffer identifier.
     */
    public SoundBuffer(int bufferId) {
        this.bufferId = bufferId;
    }

    /**
     * Retrieves the identifier of the OpenAl sound buffer.
     *
     * @return
     */
    public int getBufferId() {
        return bufferId;
    }

    @Override
    protected void onDispose() {
        alDeleteBuffers(bufferId);
    }
}