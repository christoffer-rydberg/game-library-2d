package com.example.sound.android;

import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.sound.SoundBuffer;

public class DefaultSoundBuffer implements SoundBuffer {
    private final byte[] buffer;
    private final String format;

    private DefaultSoundBuffer(byte[] bytes, String format) {
        this.buffer = bytes;
        this.format = format;
    }

    static DefaultSoundBuffer create(byte[] bytes, String format, Disposer disposer) {
        DefaultSoundBuffer soundBuffer = new DefaultSoundBuffer(bytes, format);
        disposer.registerDisposal(soundBuffer);
        return soundBuffer;
    }

    String getFormat() {
        return format;
    }

    byte[] getBytes() {
        return buffer;
    }

    @Override
    public void dispose() {

    }
}
