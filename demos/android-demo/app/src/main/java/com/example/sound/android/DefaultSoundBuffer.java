package com.example.sound.android;

import android.media.MediaFormat;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.sound.SoundBuffer;

import java.nio.ByteBuffer;

public class DefaultSoundBuffer implements SoundBuffer {
    private final byte[] buffer;
    private final MediaFormat mediaFormat;

    private DefaultSoundBuffer(ByteBuffer buffer, MediaFormat mediaFormat) {
        this.buffer = new byte[buffer.remaining()];
        this.mediaFormat = mediaFormat;
        buffer.get(this.buffer);
    }

    static DefaultSoundBuffer create(ByteBuffer audioData, MediaFormat format, Disposer disposer) {
        DefaultSoundBuffer soundBuffer = new DefaultSoundBuffer(audioData, format);
        disposer.registerDisposal(soundBuffer);
        return soundBuffer;
    }

    MediaFormat getMediaFormat() {
        return mediaFormat;
    }

    byte[] getBytes() {
        return buffer;
    }

    @Override
    public void dispose() {

    }
}
