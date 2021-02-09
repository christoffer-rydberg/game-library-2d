package com.gamelibrary2d.util.sound;

import com.gamelibrary2d.sound.SoundManager;
import com.gamelibrary2d.sound.SoundSource;

import java.net.URL;

public class SoundEffectPlayer {

    private final SoundManager soundManager;

    private final SoundSource[] soundEffects;

    // TODO: When are sound sources disposed? Do we need to add a Disposer?
    private SoundEffectPlayer(SoundManager soundManager, int channels) {
        this.soundManager = soundManager;
        soundEffects = new SoundSource[channels];
        for (int i = 0; i < soundEffects.length; ++i) {
            soundEffects[i] = soundManager.createSoundSource(false, true);
        }
    }

    public static SoundEffectPlayer create(SoundManager soundManager, int channels) {
        return new SoundEffectPlayer(soundManager, channels);
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public void play(URL url, float volume) {
        for (int i = 0; i < soundEffects.length; ++i) {
            SoundSource soundEffect = soundEffects[i];
            if (soundEffect.isStopped()) {
                soundEffect.setVolume(volume);
                soundEffect.setSoundBuffer(soundManager.getSoundBuffer(url));
                soundManager.play(soundEffect);
                break;
            }
        }
    }

}
