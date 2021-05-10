package com.example.sound.android;

import com.gamelibrary2d.common.disposal.AbstractDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.sound.SoundManager;
import com.gamelibrary2d.sound.SoundSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DefaultSoundManager extends AbstractDisposer implements SoundManager<DefaultSoundBuffer> {
    private final HashMap<Object, DefaultSoundBuffer> soundBuffers = new HashMap<>();
    private final HashMap<String, AudioDecoder> decoders = new HashMap<>();
    private final AudioDecoder defaultDecoder = new DefaultAudioDecoder();

    public static DefaultSoundManager create(Disposer disposer) {
        DefaultSoundManager soundManager = new DefaultSoundManager();
        disposer.registerDisposal(soundManager);
        return soundManager;
    }

    public AudioDecoder getDecoder(String format) {
        AudioDecoder decoder = decoders.get(format);
        return decoder == null ? defaultDecoder : decoder;
    }

    public void setDecoder(String format, AudioDecoder decoder) {
        if (decoder == null) {
            decoders.remove(format);
        } else {
            decoders.put(format, decoder);
        }
    }

    @Override
    public SoundSource<DefaultSoundBuffer>[] createSources(int size) {
        DefaultSoundSource[] sources = new DefaultSoundSource[size];
        for (int i = 0; i < size; ++i) {
            DefaultSoundSource source = DefaultSoundSource.create(this);
            sources[i] = source;
        }

        return sources;
    }

    @Override
    public DefaultSoundBuffer getBuffer(Object key) {
        return soundBuffers.get(key);
    }

    @Override
    public void loadBuffer(Object key, InputStream stream, String format) throws IOException {
        DefaultSoundBuffer soundBuffer = DefaultSoundBuffer.create(
                Read.byteArray(stream),
                format,
                this);

        soundBuffers.put(key, soundBuffer);
    }

    @Override
    protected void onDispose() {

    }
}
