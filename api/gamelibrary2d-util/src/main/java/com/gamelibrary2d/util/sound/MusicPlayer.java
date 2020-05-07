package com.gamelibrary2d.util.sound;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.frames.Frame;
import com.gamelibrary2d.sound.SoundBuffer;
import com.gamelibrary2d.sound.SoundManager;
import com.gamelibrary2d.sound.SoundSource;
import com.gamelibrary2d.updaters.DurationUpdater;
import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updates.EmptyUpdate;

import java.net.URL;

public class MusicPlayer {
    private final SoundManager manager;
    private final SoundSource[] source;
    private final SequentialUpdater[] updaters;
    private final int channels;

    private Frame frame;
    private int channel;
    private SoundBuffer activeBuffer;

    private MusicPlayer(Frame frame, SoundManager manager, int channels) {
        this.manager = manager;
        this.channels = channels;
        source = new SoundSource[channels];
        updaters = new SequentialUpdater[channels];
        for (int i = 0; i < channels; ++i) {
            source[i] = manager.createSoundSource(true, false);
            updaters[i] = new SequentialUpdater();
        }
        setFrame(frame);
    }

    public static MusicPlayer create(SoundManager manager, int channels, Game game) {
        var musicManager = new MusicPlayer(game.getFrame(), manager, channels);
        game.addFrameChangedListener(musicManager::setFrame);
        return musicManager;
    }

    private void setFrame(Frame frame) {
        if (this.frame == frame) {
            return;
        }

        this.frame = frame;

        if (frame != null) {
            for (var updater : updaters) {
                if (!updater.isFinished()) {
                    frame.runUpdater(updater, false);
                }
            }
        }
    }

    public boolean isPlaying() {
        return activeBuffer != null;
    }

    public boolean isPlaying(URL musicUrl) {
        return isPlaying() && manager.getSoundBuffer(musicUrl) == activeBuffer;
    }

    public boolean stop(float fadeOutTime) {
        if (!isPlaying()) {
            return false;
        }

        SoundSource source = getSource();
        SequentialUpdater updater = getUpdater();

        abort(updater);

        activeBuffer = null;

        updater.add(SoundUpdaterFactory.createFadeOutUpdater(manager, source, fadeOutTime, false));

        frame.runUpdater(updater, false);

        return true;
    }

    public boolean pause(float fadeOutTime) {
        if (!isPlaying()) {
            return false;
        }

        SoundSource source = getSource();
        SequentialUpdater updater = getUpdater();

        abort(updater);

        // Ensure that the sound buffer has been set
        source.setSoundBuffer(activeBuffer);

        activeBuffer = null;

        updater.add(SoundUpdaterFactory.createFadeOutUpdater(manager, source, fadeOutTime, true));

        frame.runUpdater(updater, false);

        moveToNextChannel();

        return true;
    }

    public boolean resume(URL musicUrl, float volume, float fadeInTime) {
        if (!canResume(musicUrl)) {
            return false;
        }

        return resume(volume, fadeInTime);
    }

    public boolean resume(float volume, float fadeInTime) {
        if (!canResume()) {
            return false;
        }

        boolean stopped = stop(fadeInTime / 2);

        moveToPreviousChannel();

        SoundSource source = getSource();
        SequentialUpdater updater = getUpdater();

        abort(updater);

        activeBuffer = source.getSoundBuffer();

        if (stopped) {
            fadeInTime /= 2;
            updater.add(new DurationUpdater(fadeInTime, new EmptyUpdate()));
        }

        updater.add(SoundUpdaterFactory.createFadeInUpdater(manager, source, volume, fadeInTime));

        frame.runUpdater(updater, false);

        return true;
    }

    public void play(URL url, float volume, float fadeInTime, boolean pausePrevious) {
        boolean stopped = pausePrevious ? pause(fadeInTime / 2) : stop(fadeInTime / 2);

        activeBuffer = manager.getSoundBuffer(url);

        if (activeBuffer == null) {
            return;
        }

        SoundSource source = getSource();
        SequentialUpdater updater = getUpdater();
        updater.add(new InstantUpdater((x, y) -> source.setSoundBuffer(activeBuffer)));
        updater.add(SoundUpdaterFactory.createFadeInUpdater(manager, source, volume,
                stopped ? fadeInTime / 2 : fadeInTime));
        frame.runUpdater(updater, false);
    }

    private boolean canResume() {
        moveToPreviousChannel();
        SoundSource source = getSource();
        var buffer = source.getSoundBuffer();
        moveToNextChannel();
        return buffer != null;
    }

    private boolean canResume(URL musicUrl) {
        moveToPreviousChannel();
        SoundSource source = getSource();
        var buffer = source.getSoundBuffer();
        var wantedBuffer = manager.getSoundBuffer(musicUrl);
        moveToNextChannel();
        return buffer != null && buffer == wantedBuffer;
    }

    private SoundSource getSource() {
        return source[channel];
    }

    private SequentialUpdater getUpdater() {
        return updaters[channel];
    }

    private void moveToNextChannel() {
        channel += 1;
        if (channel >= channels)
            channel -= channels;
    }

    private void moveToPreviousChannel() {
        channel -= 1;
        if (channel < 0)
            channel += channels;
    }

    private void abort(SequentialUpdater updater) {
        updater.clear();
    }
}