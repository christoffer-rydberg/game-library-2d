package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.components.frames.Frame;
import com.gamelibrary2d.components.frames.FrameDisposal;
import com.gamelibrary2d.demos.networkgame.client.frames.LoadingFrame;
import com.gamelibrary2d.demos.networkgame.client.frames.SplashFrame;
import com.gamelibrary2d.demos.networkgame.client.frames.game.GameFrame;
import com.gamelibrary2d.demos.networkgame.client.frames.menu.MenuFrame;
import com.gamelibrary2d.demos.networkgame.client.input.ControllerFactory;
import com.gamelibrary2d.demos.networkgame.client.objects.network.decoration.SoundMap;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.demos.networkgame.client.settings.Dimensions;
import com.gamelibrary2d.demos.networkgame.client.urls.Music;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.framework.Framework;
import com.gamelibrary2d.network.common.client.CommunicatorFactory;
import com.gamelibrary2d.sound.MusicPlayer;
import com.gamelibrary2d.sound.SoundManager;
import com.gamelibrary2d.sound.SoundPlayer;

import java.io.IOException;
import java.io.InputStream;

public class DemoGame extends AbstractGame {
    private final ControllerFactory controllerFactory;
    private final ServerManager serverManager;
    private final SoundManager soundManager;
    private final ResourceManager resourceManager;

    private Frame menuFrame;
    private LoadingFrame loadingFrame;
    private GameFrame gameFrame;

    public DemoGame(Framework framework, ControllerFactory controllerFactory, ResourceManager resourceManager, ServerManager serverManager, SoundManager soundManager) {
        super(framework);
        this.controllerFactory = controllerFactory;
        this.resourceManager = resourceManager;
        this.serverManager = serverManager;
        this.soundManager = soundManager;
    }

    @Override
    protected void onStart() throws InitializationException, IOException {
        showSplashScreen();
        createGlobalResources();
        initializeFrames();
        setLoadingFrame(loadingFrame);
        setFrame(menuFrame, FrameDisposal.DISPOSE);
    }

    private void loadDemoFrame(CommunicatorFactory communicatorFactory) {
        gameFrame.getClient().setCommunicatorFactory(communicatorFactory);
        try {
            loadFrame(gameFrame, FrameDisposal.NONE);
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    public void goToMenu() {
        try {
            setFrame(menuFrame, FrameDisposal.UNLOAD);
        } catch (InitializationException e) {
            e.printStackTrace();
            gameFrame.end();
        } finally {
            serverManager.stopHostedServer();
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
        SplashFrame splashFrame = new SplashFrame(this);
        setFrame(splashFrame);
        getWindow().show();
        render();
    }

    private void createGlobalResources() throws IOException {
        Dimensions dimensions = new Dimensions(getWindow());
        Fonts.create(resourceManager, this);
        Surfaces.create(dimensions, this);
        Textures.create(dimensions, this);
    }

    private void loadSoundBuffer(String resource, String format) throws IOException {
        try (InputStream stream = resourceManager.open(resource)) {
            soundManager.loadBuffer(resource, stream, format);
        }
    }

    private void initializeFrames() throws InitializationException, IOException {
        loadSoundBuffer(Music.MENU, "ogg");
        loadSoundBuffer(Music.GAME, "ogg");

        MusicPlayer musicPlayer = new MusicPlayer(
                this,
                soundManager,
                10);

        SoundPlayer soundPlayer = new SoundPlayer(
                soundManager,
                10);

        loadingFrame = new LoadingFrame(this, resourceManager);
        loadingFrame.initialize(this);

        menuFrame = new MenuFrame(this, resourceManager, musicPlayer, soundPlayer);
        menuFrame.initialize(this);

        gameFrame = new GameFrame(this, controllerFactory, resourceManager, musicPlayer, soundPlayer, new SoundMap(soundManager, resourceManager));
        gameFrame.initialize(this);
    }

    @Override
    protected void onExit() {
        gameFrame.disconnect();
    }
}