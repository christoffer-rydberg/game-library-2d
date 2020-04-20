package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.AbstractGame;
import com.gamelibrary2d.demos.networkgame.client.frames.DemoFrame;
import com.gamelibrary2d.demos.networkgame.client.frames.LoadingFrame;
import com.gamelibrary2d.demos.networkgame.client.frames.MenuFrame;
import com.gamelibrary2d.demos.networkgame.client.frames.SplashFrame;
import com.gamelibrary2d.demos.networkgame.client.resources.Fonts;
import com.gamelibrary2d.demos.networkgame.client.resources.Surfaces;
import com.gamelibrary2d.demos.networkgame.client.resources.Textures;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.Frame;
import com.gamelibrary2d.frames.FrameDisposal;
import com.gamelibrary2d.framework.Window;
import com.gamelibrary2d.framework.lwjgl.Lwjgl_Framework;
import com.gamelibrary2d.network.common.client.CommunicatorFactory;

import java.io.IOException;

public class DemoGame extends AbstractGame {
    private final ServerManager serverManager = new ServerManager();
    private Frame menuFrame;
    private LoadingFrame loadingFrame;
    private DemoFrame demoFrame;

    public DemoGame() {
        super(new Lwjgl_Framework());
    }

    @Override
    public void start(Window window) throws InitializationException {
        super.start(window);
    }

    @Override
    protected void onStart() {
        try {
            showSplashScreen();
            createGlobalResources();
            initializeFrames();
            setLoadingFrame(loadingFrame);
            setFrame(menuFrame, FrameDisposal.DISPOSE);
        } catch (Exception e) {
            System.err.println("Failed to start game");
            e.printStackTrace();
        }
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

    public void startLocalGame() {
        loadDemoFrame(serverManager::hostLocalServer);
    }

    public void hostNetworkGame(int port) {
        loadDemoFrame(() -> serverManager.hostNetworkServer(port));
    }

    public void joinNetworkGame(String ip, int port) {
        loadDemoFrame(() -> serverManager.connectToServer(ip, port));
    }

    private void showSplashScreen() throws InitializationException {
        var splashFrame = new SplashFrame(this);
        setFrame(splashFrame);
        getWindow().show();
        renderFrame();
    }

    private void createGlobalResources() throws IOException {
        Fonts.create(this);
        Surfaces.create(this);
        Textures.create(this);
    }

    private void initializeFrames() throws InitializationException {
        loadingFrame = new LoadingFrame(this);
        loadingFrame.initialize(this);

        menuFrame = new MenuFrame(this);
        menuFrame.initialize(this);

        demoFrame = new DemoFrame(this);
        demoFrame.initialize(this);
    }

    @Override
    protected void onExit() {
        serverManager.stopHostedServer();
    }
}