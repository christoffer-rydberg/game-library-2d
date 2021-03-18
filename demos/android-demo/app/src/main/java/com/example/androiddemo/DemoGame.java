package com.example.androiddemo;

import android.content.res.AssetManager;
import com.example.framework.android.Android_Framework;
import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.exceptions.InitializationException;

public class DemoGame extends AbstractGame {

    private final AssetManager assets;

    DemoGame(AssetManager assets) {
        super(new Android_Framework());
        this.assets = assets;
    }

    @Override
    protected void onStart() throws InitializationException {
        setFrame(new DemoFrame(this, assets));
        setBackgroundColor(Color.PINK);
    }

    @Override
    protected void onExit() {

    }
}
