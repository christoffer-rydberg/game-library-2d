package com.example.androiddemo;

import android.app.Activity;
import com.example.framework.android.AbstractGameActivity;
import com.example.framework.android.Android_Framework;
import com.example.sound.android.DefaultSoundManager;
import com.gamelibrary2d.common.disposal.DefaultDisposer;
import com.gamelibrary2d.demos.networkgame.client.DemoGame;
import com.gamelibrary2d.demos.networkgame.client.ServerManager;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AbstractGameActivity {

    private final static DefaultDisposer disposer = new DefaultDisposer();

    public MainActivity() {
        super(MainActivity::createGame);
    }

    private static KeyPair createKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    private static DemoGame createGame(Activity activity) {
        try {
            return new DemoGame(
                    new Android_Framework(),
                    VirtualController::new,
                    new AndroidResourceManager(activity.getAssets()),
                    ServerManager.create(createKeyPair(), disposer),
                    DefaultSoundManager.create(disposer)
            );
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
