package com.gamelibrary2d.util.sound;

import com.gamelibrary2d.sound.SoundManager;
import com.gamelibrary2d.sound.SoundSource;
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
     * @param soundManager The sound manager.
     * @param soundSource  The sound source.
     * @param duration     The "fade out"-duration in seconds.
     */
    public static Updater createFadeOutUpdater(SoundManager soundManager, SoundSource soundSource, float duration,
                                               boolean pause) {
        SequentialUpdater updater = new SequentialUpdater(2);
        updater.add(createVolumeUpdater(soundSource, 0, duration));
        if (pause) {
            updater.add(new InstantUpdater((x, y) -> soundManager.pause(soundSource)));
        } else {
            updater.add(new InstantUpdater((x, y) -> soundManager.stop(soundSource)));
        }

        return updater;
    }

    /**
     * Creates an updater that fades in a {@link SoundSource}.
     *
     * @param soundManager The sound manager.
     * @param soundSource  The sound source.
     * @param duration     The "fade in"-duration in seconds.
     * @param volume       The volume when faded in.
     */
    public static Updater createFadeInUpdater(SoundManager soundManager, SoundSource soundSource, float volume,
                                              float duration) {
        SequentialUpdater updater = new SequentialUpdater(2);
        updater.add(new InstantUpdater((x, y) -> {
            soundSource.setVolume(0);
            soundManager.play(soundSource);
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