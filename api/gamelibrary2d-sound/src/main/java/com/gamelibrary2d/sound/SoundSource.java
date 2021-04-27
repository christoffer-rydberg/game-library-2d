package com.gamelibrary2d.sound;

import com.gamelibrary2d.common.disposal.Disposable;

public interface SoundSource<T extends SoundBuffer> extends Disposable {

    boolean isStopped();

    void play();

    void pause();

    void stop();

    float getVolume();

    void setVolume(float volume);

    T getSoundBuffer();

    void setSoundBuffer(T soundBuffer);

    void setLooping(boolean looping);

    boolean isLooping();
}
