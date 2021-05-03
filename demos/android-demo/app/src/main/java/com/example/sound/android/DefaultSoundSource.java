package com.example.sound.android;

import android.media.*;
import com.gamelibrary2d.common.disposal.Disposer;
import com.gamelibrary2d.sound.SoundSource;

import java.util.concurrent.atomic.AtomicReference;

public class DefaultSoundSource implements SoundSource<DefaultSoundBuffer> {
    private static final int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final AudioTrack track;

    private final AtomicReference<Thread> thread = new AtomicReference<>();
    private float volume = 1f;
    private boolean looping = false;
    private DefaultSoundBuffer soundBuffer;

    private DefaultSoundSource(AudioTrack track) {
        this.track = track;
    }

    public static DefaultSoundSource create(Disposer disposer) {
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

        DefaultSoundSource soundSource = new DefaultSoundSource(track);

        disposer.registerDisposal(soundSource);

        return soundSource;
    }

    @Override
    public boolean isStopped() {
        return thread.get() == null;
    }

    @Override
    public void play() {
        if (isStopped()) {
            byte[] data = soundBuffer.getBytes();

            track.play();
            Thread thread = new Thread(() -> {
                try {
                    do {
                        int offset = 0;
                        while (!Thread.currentThread().isInterrupted() && offset < data.length) {
                            int result = track.write(data, offset, data.length - offset, AudioTrack.WRITE_NON_BLOCKING);
                            if (result == 0) {
                                try {
                                    double bufferTimeCapacity = ((double) track.getBufferCapacityInFrames() / track.getPlaybackRate()) * 1000.0;
                                    Thread.sleep((long) bufferTimeCapacity / 2);
                                } catch (InterruptedException e) {
                                    return;
                                }
                            } else if (result < 0) {
                                // TODO: Log error?
                                return;
                            }

                            offset += result;
                        }
                    } while (looping);
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
        track.setPlaybackRate(soundBuffer.getMediaFormat().getInteger(MediaFormat.KEY_SAMPLE_RATE));
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
}
