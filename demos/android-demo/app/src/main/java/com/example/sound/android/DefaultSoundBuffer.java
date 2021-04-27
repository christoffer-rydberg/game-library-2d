package com.example.sound.android;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.sound.SoundBuffer;

import java.nio.ByteBuffer;

public class DefaultSoundBuffer implements SoundBuffer {
    private final byte[] buffer;

    private DefaultSoundBuffer(ByteBuffer buffer) {
        this.buffer = new byte[buffer.remaining()];
        buffer.get(this.buffer);
    }

    static DefaultSoundBuffer create(ByteBuffer audioData, Disposer disposer) {
        DefaultSoundBuffer soundBuffer = new DefaultSoundBuffer(audioData);
        disposer.registerDisposal(soundBuffer);
        return soundBuffer;
    }

    byte[] getBytes() {
        return buffer;
    }

    @Override
    public void dispose() {

    }
}
