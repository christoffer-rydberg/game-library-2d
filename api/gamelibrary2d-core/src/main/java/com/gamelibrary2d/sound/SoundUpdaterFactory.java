package com.gamelibrary2d.sound;

import com.gamelibrary2d.updaters.InstantUpdater;
import com.gamelibrary2d.updaters.SequentialUpdater;
import com.gamelibrary2d.updaters.Updater;
import com.gamelibrary2d.updaters.ValueUpdater;

/**
 * The class contains static methods to create various sound {@link Updater
 * updaters}.
 */
public class SoundUpdaterFactory {

    /**
     * Creates an updater that fades out a {@link SoundSource}.
     *
     * @param soundSource The sound source.
     * @param duration    The "fade out"-duration in seconds.
     */
    public static Updater createFadeOutUpdater(SoundSource soundSource, float duration,
                                               boolean pause) {
        SequentialUpdater updater = new SequentialUpdater(2);
        updater.add(createVolumeUpdater(soundSource, 0, duration));
        if (pause) {
            updater.add(new InstantUpdater(dt -> soundSource.pause()));
        } else {
            updater.add(new InstantUpdater(dt -> soundSource.stop()));
        }

        return updater;
    }

    /**
     * Creates an updater that fades in a {@link SoundSource}.
     *
     * @param soundSource The sound source.
     * @param duration    The "fade in"-duration in seconds.
     * @param volume      The volume when faded in.
     */
    public static Updater createFadeInUpdater(SoundSource soundSource, float volume, float duration) {
        SequentialUpdater updater = new SequentialUpdater(2);
        updater.add(new InstantUpdater(dt -> {
            soundSource.setVolume(0);
            soundSource.play();
        }));
        updater.add(createVolumeUpdater(soundSource, volume, duration));
        return updater;
    }

    /**
     * Creates an updater that changes the volume of a {@link SoundSource}.
     *
     * @param soundSource The sound source.
     * @param goalVolume  The goal volume.
     * @param duration    The duration to reach the goal volume, in seconds.
     */
    public static Updater createVolumeUpdater(SoundSource soundSource, float goalVolume, float duration) {
        return ValueUpdater.fromDuration(soundSource::getVolume, soundSource::setVolume, goalVolume, duration);
    }
}