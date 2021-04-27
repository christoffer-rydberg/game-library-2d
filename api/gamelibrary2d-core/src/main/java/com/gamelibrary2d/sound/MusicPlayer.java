package com.gamelibrary2d.sound;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.frames.Frame;
import com.gamelibrary2d.updaters.DurationUpdater;
import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updaters.Updater;
import com.gamelibrary2d.updates.EmptyUpdate;

public class MusicPlayer {
    private final GenericMusicPlayer player;

    public MusicPlayer(Game game, SoundManager<?> soundManager, int channels) {
        player = new GenericMusicPlayer<>(game, soundManager, channels);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public boolean isPlaying(Object key) {
        return player.isPlaying(key);
    }

    public boolean stop(float fadeOutTime) {
        return player.stop(fadeOutTime);
    }

    public boolean pause(float fadeOutTime) {
        return player.pause(fadeOutTime);
    }

    public boolean resume(Object key, float volume, boolean looping, float fadeInTime) {
        return player.resume(key, volume, looping, fadeInTime);
    }

    public boolean resume(float volume, boolean looping, float fadeInTime) {
        return player.resume(volume, looping, fadeInTime);
    }

    public float getVolume() {
        return player.getVolume();
    }

    public void setVolume(float volume) {
        player.setVolume(volume);
    }

    public boolean isLooping() {
        return player.isLooping();
    }

    public void setLooping(boolean looping) {
        player.setLooping(looping);
    }

    public void play(Object key, float volume, boolean looping, float fadeInTime, boolean pausePrevious) {
        player.play(key, volume, looping, fadeInTime, pausePrevious);
    }

    private static class GenericMusicPlayer<T extends SoundBuffer> {
        private final SoundManager<T> soundManager;
        private final SoundSource<T>[] sources;
        private final SequentialUpdater[] updaters;
        private final int channels;
        private Frame frame;
        private int channel;

        private T activeBuffer;

        GenericMusicPlayer(Game game, SoundManager<T> soundManager, int channels) {
            this.soundManager = soundManager;
            this.sources = soundManager.createSources(channels);
            this.channels = channels;
            updaters = new SequentialUpdater[channels];
            for (int i = 0; i < channels; ++i) {
                updaters[i] = new SequentialUpdater();
            }

            setFrame(game.getFrame());
            game.addFrameChangedListener(this::setFrame);
        }

        private void setFrame(Frame frame) {
            if (this.frame != frame) {
                this.frame = frame;
                if (frame != null) {
                    for (Updater updater : updaters) {
                        if (!updater.isFinished()) {
                            frame.runUpdater(updater, false);
                        }
                    }
                }
            }
        }

        boolean isPlaying() {
            return activeBuffer != null;
        }

        boolean isPlaying(Object key) {
            return isPlaying() && soundManager.getBuffer(key) == activeBuffer;
        }

        public boolean stop(float fadeOutTime) {
            if (!isPlaying()) {
                return false;
            }

            SequentialUpdater updater = getUpdater();

            abort(updater);

            // Ensure that the sound buffer has been set
            sources[channel].setSoundBuffer(activeBuffer);
            activeBuffer = null;

            updater.add(SoundUpdaterFactory.createFadeOutUpdater(sources[channel], fadeOutTime, false));

            frame.runUpdater(updater, false);

            return true;
        }

        public boolean pause(float fadeOutTime) {
            if (!isPlaying()) {
                return false;
            }

            SequentialUpdater updater = getUpdater();

            abort(updater);

            // Ensure that the sound buffer has been set
            sources[channel].setSoundBuffer(activeBuffer);
            activeBuffer = null;

            updater.add(SoundUpdaterFactory.createFadeOutUpdater(sources[channel], fadeOutTime, true));

            frame.runUpdater(updater, false);

            moveToNextChannel();

            return true;
        }

        boolean resume(Object key, float volume, boolean looping, float fadeInTime) {
            return canResume(key) && resume(volume, looping, fadeInTime);
        }

        boolean resume(float volume, boolean looping, float fadeInTime) {
            if (!canResume()) {
                return false;
            }

            boolean stopped = stop(fadeInTime / 2);

            moveToPreviousChannel();

            SoundSource<T> source = sources[channel];
            SequentialUpdater updater = getUpdater();

            abort(updater);

            activeBuffer = source.getSoundBuffer();

            source.setLooping(looping);

            if (stopped) {
                fadeInTime /= 2;
                updater.add(new DurationUpdater(fadeInTime, new EmptyUpdate()));
            }

            updater.add(SoundUpdaterFactory.createFadeInUpdater(source, volume, fadeInTime));

            frame.runUpdater(updater, false);

            return true;
        }

        public void play(Object key, float volume, boolean looping, float fadeInTime, boolean pausePrevious) {
            boolean stopped = pausePrevious ? pause(fadeInTime / 2) : stop(fadeInTime / 2);

            SequentialUpdater updater = getUpdater();

            activeBuffer = soundManager.getBuffer(key);
            SoundSource<T> source = sources[channel];
            source.setLooping(looping);
            updater.add(new InstantUpdater(dt -> source.setSoundBuffer(activeBuffer)));

            updater.add(SoundUpdaterFactory.createFadeInUpdater(sources[channel], volume,
                    stopped ? fadeInTime / 2 : fadeInTime));

            frame.runUpdater(updater, false);
        }

        private boolean canResume() {
            moveToPreviousChannel();
            SoundSource source = sources[channel];
            SoundBuffer buffer = source.getSoundBuffer();
            moveToNextChannel();
            return buffer != null;
        }

        private boolean canResume(Object key) {
            SoundBuffer soundBuffer = soundManager.getBuffer(key);
            moveToPreviousChannel();
            SoundSource source = sources[channel];
            SoundBuffer buffer = source.getSoundBuffer();
            moveToNextChannel();
            return buffer != null && buffer == soundBuffer;
        }

        private SoundSource<T> getActiveSource() {
            return activeBuffer != null ? sources[channel] : null;
        }

        public float getVolume() {
            return getActiveSource().getVolume();
        }

        public void setVolume(float volume) {
            SequentialUpdater updater = getUpdater();
            abort(updater);
            getActiveSource().setVolume(volume);
        }

        public void setVolume(float volume, float fadeInTime) {
            SequentialUpdater updater = getUpdater();
            abort(updater);
            updater.add(SoundUpdaterFactory.createVolumeUpdater(getActiveSource(), volume, fadeInTime));
            frame.runUpdater(updater, false);
        }

        public boolean isLooping() {
            return getActiveSource().isLooping();
        }

        public void setLooping(boolean looping) {
            getActiveSource().setLooping(looping);
        }

        private SequentialUpdater getUpdater() {
            return updaters[channel];
        }

        private void moveToNextChannel() {
            channel = (channel + 1) % channels;
        }

        private void moveToPreviousChannel() {
            channel = (channel + channels - 1) % channels;
        }

        private void abort(SequentialUpdater updater) {
            updater.clear();
        }
    }
}