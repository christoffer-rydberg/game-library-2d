package com.example.sound.android;

import android.media.MediaDataSource;
import android.media.MediaFormat;
import com.gamelibrary2d.common.disposal.AbstractDisposer;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.sound.SoundManager;
import com.gamelibrary2d.sound.SoundSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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

    public AudioDecoder putDecoder(String format, AudioDecoder decoder) {
        return decoders.put(format, decoder);
    }

    @Override
    public SoundSource<DefaultSoundBuffer>[] createSources(int size) {
        Disposer disposer = new DefaultDisposer(this);
        DefaultSoundSource[] sources = new DefaultSoundSource[size];
        for (int i = 0; i < size; ++i) {
            DefaultSoundSource source = DefaultSoundSource.create(disposer);
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
        AudioDecoder decoder = getDecoder(format);
        if (decoder == null) {
            throw new IOException(String.format("No decoder has been registered for the format '%s'", format));
        }

        MediaDataSource source = new InternalArrayDataSource(Read.byteArray(stream));
        AudioDecoderOutputBuffer decoderOutput = new AudioDecoderOutputBuffer(new DynamicByteBuffer());
        decoder.decode(source, decoderOutput);
        decoderOutput.buffer.flip();

        DefaultSoundBuffer soundBuffer = DefaultSoundBuffer.create(
                decoderOutput.buffer.internalByteBuffer(),
                decoderOutput.mediaFormat,
                this);

        soundBuffers.put(key, soundBuffer);
    }

    @Override
    protected void onDispose() {

    }

    static class AudioDecoderOutputBuffer implements AudioDecoder.Output {
        private final DataBuffer buffer;
        private MediaFormat mediaFormat;

        public AudioDecoderOutputBuffer(DataBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void initialize(MediaFormat mediaFormat) {
            this.mediaFormat = mediaFormat;
        }

        @Override
        public void write(ByteBuffer decodedData) {
            buffer.put(decodedData);
        }

        @Override
        public void finished() {

        }
    }
}
