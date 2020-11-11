package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.demos.networkgame.client.frames.*;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.demos.networkgame.client.urls.Music;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.Frame;
import com.gamelibrary2d.frames.FrameDisposal;
import com.gamelibrary2d.framework.Framework;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.network.common.client.CommunicatorFactory;
import com.gamelibrary2d.sound.SoundManager;
import com.gamelibrary2d.sound.decoders.AudioDecoder;
import com.gamelibrary2d.sound.decoders.VorbisDecoder;
import com.gamelibrary2d.util.sound.MusicPlayer;
import com.gamelibrary2d.util.sound.SoundEffectPlayer;

import java.io.IOException;

public class DemoGame extends AbstractGame {
    private final ServerManager serverManager;

    private Frame menuFrame;
    private LoadingFrame loadingFrame;
    private DemoFrame demoFrame;
    private CreditsFrame creditsFrame;

    public DemoGame(Framework framework, ServerManager serverManager) {
        super(framework);
        this.serverManager = serverManager;
    }

    @Override
    public void start(Window window) throws InitializationException {
        super.start(window);
    }

    @Override
    protected void onStart() {
        try {
            showSplashScreen();

            var soundManager = initializeAudio();
            var musicPlayer = MusicPlayer.create(soundManager, 10, this);
            var soundPlayer = SoundEffectPlayer.create(soundManager, 10);

            createGlobalResources();
            initializeFrames(musicPlayer, soundPlayer);
            setLoadingFrame(loadingFrame);
            setFrame(menuFrame, FrameDisposal.DISPOSE);
        } catch (Exception e) {
            System.err.println("Failed to start game");
            e.printStackTrace();
        }
    }

    private SoundManager initializeAudio() throws IOException {
        var soundManager = SoundManager.create(this);
        AudioDecoder decoder = new VorbisDecoder();
        soundManager.loadSoundBuffer(Music.MENU, decoder);
        soundManager.loadSoundBuffer(Music.GAME, decoder);
        return soundManager;
    }

    private void loadDemoFrame(CommunicatorFactory communicatorFactory) {
        demoFrame.getClient().setCommunicatorFactory(communicatorFactory);
        try {
            loadFrame(demoFrame, FrameDisposal.NONE);
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    public void goToMenu() {
        try {
            setFrame(menuFrame, FrameDisposal.UNLOAD);
            serverManager.stopHostedServer();
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    public void goToCredits() {
        try {
            setFrame(creditsFrame, FrameDisposal.UNLOAD);
            serverManager.stopHostedServer();
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    public void startLocalGame() {
        loadDemoFrame(serverManager::hostLocalServer);
    }

    public void hostNetworkGame(int port, int localUdpPort) {
        loadDemoFrame(() -> serverManager.hostNetworkServer(port, localUdpPort));
    }

    public void joinNetworkGame(String ip, int port, int localUdpPort) {
        loadDemoFrame(() -> serverManager.connectToServer(ip, port, localUdpPort));
    }

    private void showSplashScreen() throws InitializationException {
        var splashFrame = new SplashFrame(this);
        setFrame(splashFrame);
        getWindow().show();
        renderFrame();
    }

    private void createGlobalResources() {
        Fonts.create(this);
        Surfaces.create(this);
        Textures.create(this);
    }

    private void initializeFrames(MusicPlayer musicPlayer, SoundEffectPlayer soundPlayer) throws InitializationException {
        loadingFrame = new LoadingFrame(this);
        loadingFrame.initialize(this);

        menuFrame = new MenuFrame(this, musicPlayer, soundPlayer);
        menuFrame.initialize(this);

        demoFrame = new DemoFrame(this, musicPlayer, soundPlayer);
        demoFrame.initialize(this);

        creditsFrame = new CreditsFrame(this);
        creditsFrame.initialize(this);
    }

    @Override
    protected void onExit() {
        serverManager.stopHostedServer();
        demoFrame.disconnect();
    }
}