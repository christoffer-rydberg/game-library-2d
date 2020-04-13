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
import com.gamelibrary2d.network.common.Communicator;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    private void loadDemoFrame(Future<Communicator> futureCommunicator) throws InitializationException {
        loadingFrame.setLoadingAction(() -> {
            try {
                // TODO: Set communicator is not thread-safe
                demoFrame.getClient().setCommunicator(
                        futureCommunicator.get(10, TimeUnit.SECONDS));
            } catch (Exception e) {
                throw new InitializationException("Failed to get server communicator", e);
            }
        });

        loadFrame(demoFrame);
    }

    public void goToMenu() {
        try {
            setFrame(menuFrame, FrameDisposal.UNLOAD);
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    public void startLocalGame() {
        try {
            loadDemoFrame(serverManager.hostLocalServer());
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    public void hostNetworkGame(int port) {
        try {
            loadDemoFrame(serverManager.hostNetworkServer(port));
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    public void joinNetworkGame(String ip, int port) {
        try {
            loadDemoFrame(serverManager.joinNetworkServer(ip, port));
        } catch (InitializationException e) {
            e.printStackTrace();
        }
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
        loadingFrame.initialize();

        menuFrame = new MenuFrame(this);
        menuFrame.initialize();

        demoFrame = new DemoFrame(this);
        demoFrame.initialize();
    }

    @Override
    protected void onExit() {
        demoFrame.getClient().disconnect();
        serverManager.stopHostedServer();
    }


}