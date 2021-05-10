package com.example.sound.android;

import android.media.*;
import com.gamelibrary2d.sound.SoundSource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultSoundSource implements SoundSource<DefaultSoundBuffer> {
    private static final int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final AudioTrack track;
    private final AudioTrackSink audioTrackSink;
    private final DefaultSoundManager soundManager;
    private final AtomicReference<Thread> thread = new AtomicReference<>();
    private float volume = 1f;
    private boolean looping = false;
    private DefaultSoundBuffer soundBuffer;

    private DefaultSoundSource(DefaultSoundManager soundManager, AudioTrack track) {
        this.soundManager = soundManager;
        this.track = track;
        audioTrackSink = new AudioTrackSink(track);
    }

    static DefaultSoundSource create(DefaultSoundManager soundManager) {
        AudioAttributes.Builder attrsBuilder = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME);

        AudioAttributes attrs = attrsBuilder.build();

        int nativeRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

        AudioFormat audioFormat =
                new AudioFormat.Builder()
                        .setSampleRate(nativeRate)
                        .setEncoding(DefaultSoundSource.audioFormat)
                        .build();

        int bufferSize = AudioTrack.getMinBufferSize(
                nativeRate,
                channelConfig,
                DefaultSoundSource.audioFormat);

        AudioTrack track = new AudioTrack(
                attrs,
                audioFormat,
                bufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);

        DefaultSoundSource soundSource = new DefaultSoundSource(soundManager, track);

        soundManager.registerDisposal(soundSource);

        return soundSource;
    }

    @Override
    public boolean isStopped() {
        return thread.get() == null;
    }

    @Override
    public void play() {
        if (isStopped()) {
            final String format = soundBuffer.getFormat();
            final AudioDecoder decoder = soundManager.getDecoder(format);
            final MediaDataSource source = new InMemoryMediaDataSource(soundBuffer.getBytes());

            track.play();
            Thread thread = new Thread(() -> {
                try {
                    do {
                        try {
                            decoder.decode(source, audioTrackSink);
                        } catch (InterruptedException e) {
                            break;
                        }
                    } while (looping && !Thread.currentThread().isInterrupted());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    track.stop();
                    this.thread.compareAndSet(Thread.currentThread(), null);
                }
            });

            this.thread.set(thread);
            thread.start();
        }
    }

    @Override
    public void pause() {
        track.pause();
    }

    @Override
    public void stop() {
        Thread thread = this.thread.getAndSet(null);
        if (thread != null) {
            thread.interrupt();
            track.pause();
            track.flush();
        }
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        track.setVolume(volume);
    }

    @Override
    public DefaultSoundBuffer getSoundBuffer() {
        return soundBuffer;
    }

    @Override
    public void setSoundBuffer(DefaultSoundBuffer soundBuffer) {
        this.soundBuffer = soundBuffer;
    }

    @Override
    public boolean isLooping() {
        return looping;
    }

    @Override
    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    @Override
    public void dispose() {
        track.release();
    }

    private static class AudioTrackSink implements AudioSink {
        private final AudioTrack track;

        AudioTrackSink(AudioTrack track) {
            this.track = track;
        }

        @Override
        public void begin(MediaFormat mediaFormat) {
            track.setPlaybackRate(mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
        }

        @Override
        public void write(ByteBuffer audioData) throws InterruptedException {
            while (audioData.remaining() > 0) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                int result = track.write(audioData, audioData.remaining(), AudioTrack.WRITE_NON_BLOCKING);

                if (result == 0) {
                    double bufferTimeCapacity = ((double) track.getBufferCapacityInFrames() / track.getPlaybackRate()) * 1000.0;
                    Thread.sleep((long) bufferTimeCapacity / 2);
                } else if (result < 0) {
                    switch (result) {
                        case AudioTrack.ERROR_INVALID_OPERATION:
                            throw new RuntimeException("Invalid audio track operation");
                        case AudioTrack.ERROR_BAD_VALUE:
                            throw new RuntimeException("Bad audio track value");
                        case AudioTrack.ERROR_DEAD_OBJECT:
                            throw new RuntimeException("Dead audio track object");
                        case AudioTrack.ERROR:
                            throw new RuntimeException("Audio track operation failed");
                    }
                }
            }
        }

        @Override
        public void end() {

        }
    }
}
