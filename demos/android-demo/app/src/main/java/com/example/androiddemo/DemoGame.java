package com.example.androiddemo;

import android.content.res.AssetManager;
import com.example.framework.android.Android_Framework;
import com.example.sound.android.DefaultSoundManager;
import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.sound.SoundManager;
import com.gamelibrary2d.sound.SoundPlayer;

import java.io.IOException;
import java.io.InputStream;

public class DemoGame extends AbstractGame {

    private final AssetManager assets;

    DemoGame(AssetManager assets) {
        super(new Android_Framework());
        this.assets = assets;
    }

    @Override
    protected void onStart() throws InitializationException {
        setBackgroundColor(Color.PINK);

        SoundManager soundManager = new DefaultSoundManager();

        String cowSound = "sounds/cow.ogg";
        try (InputStream stream = assets.open(cowSound)) {
            soundManager.loadBuffer(cowSound, stream, "ogg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        SoundPlayer soundPlayer = new SoundPlayer(
                soundManager,
                10);

        setFrame(new DemoFrame(this, soundPlayer, assets));
    }

    @Override
    protected void onExit() {

    }
}
