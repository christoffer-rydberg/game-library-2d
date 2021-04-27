package com.gamelibrary2d.sound;

public class SoundPlayer {
    private final GenericSoundPlayer player;

    public SoundPlayer(SoundManager<?> soundManager, int channels) {
        player = new GenericSoundPlayer<>(soundManager, channels);
    }

    public void play(Object key, float volume) {
        player.play(key, volume);
    }

    private static class GenericSoundPlayer<T extends SoundBuffer> {
        private final SoundManager<T> soundManager;
        private final SoundSource<T>[] sources;

        GenericSoundPlayer(SoundManager<T> soundManager, int channels) {
            this.soundManager = soundManager;
            this.sources = soundManager.createSources(channels);
        }

        public void play(Object key, float volume) {
            T soundBuffer = soundManager.getBuffer(key);
            for (int i = 0; i < sources.length; ++i) {
                SoundSource<T> soundSource = sources[i];
                if (soundSource.isStopped()) {
                    soundSource.setVolume(volume);
                    soundSource.setSoundBuffer(soundBuffer);
                    soundSource.play();
                    break;
                }
            }
        }
    }
}
