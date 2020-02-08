package com.gamelibrary2d;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.disposal.AbstractDisposer;
import com.gamelibrary2d.common.event.DefaultEventPublisher;
import com.gamelibrary2d.common.event.EventPublisher;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.eventlisteners.FrameChangedListener;
import com.gamelibrary2d.exceptions.LoadInterruptedException;
import com.gamelibrary2d.frames.Frame;
import com.gamelibrary2d.frames.FrameDisposal;
import com.gamelibrary2d.frames.LoadingFrame;
import com.gamelibrary2d.framework.Runtime;
import com.gamelibrary2d.framework.*;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.ShaderType;
import com.gamelibrary2d.input.KeyAction;
import com.gamelibrary2d.resources.Shader;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An abstract implementation of the Game interface. This is a general
 * implementation and can be used as base class for all games.
 */
public abstract class AbstractGame extends AbstractDisposer implements Game, CallbackHandler {

    private final EventPublisher<Frame> frameChangedPublisher = new DefaultEventPublisher<>();

    /**
     * The OpenGL window used for rendering.
     */
    private Window window;

    /**
     * Last known X coordinate of mouse cursor.
     */
    private float cursorPosX;

    /**
     * Last known Y coordinate of mouse cursor.
     */
    private float cursorPosY;

    /**
     * True whenever the game window has cursor focus.
     */
    private boolean cursorFocus;

    /**
     * True while inside an update cycle. Used to determine if some actions, such as
     * changing frame, can be done instantly or if it should be delayed until after
     * the current cycle.
     */
    private boolean updating;

    /**
     * The currently active frame.
     */
    private Frame frame;

    /**
     * True if the current frame has not yet been updated, used so that the
     * deltaTime can be set to 0 for the first update (to avoid a very big initial
     * delta time).
     */
    private boolean frameNotUpdated;

    /**
     * The loading frame is displayed when loading a new frame.
     */
    private LoadingFrame loadingFrame;

    /**
     * The main loop is responsible for maintaining a steady frame rate.
     */
    private GameLoop mainLoop;

    /**
     * Speed factor each update, applied to deltatime
     */
    private float speedFactor = 1;

    /**
     * Queue for code that will be invoked after the current update.
     */
    private Deque<Runnable> invokeLater;

    protected AbstractGame(Framework framework) {
        Runtime.initialize(framework);
    }

    @Override
    public void start(Window window) {

        this.window = window;

        window.initialize();
        window.createWindow();
        window.createCallBacks(this);

        initializeOpenGLSettings();
        createDefaultShaderProgram();

        invokeLater = new ArrayDeque<>();
        mainLoop = new GameLoop(this, window);

        mainLoop.start(this::onLoopStarted);

        onExit();

        // Dispose objects registered to this game
        dispose();

        Runtime.dispose();
    }

    private void onLoopStarted() {
        onStart();
        window.show();
    }

    private void initializeOpenGLSettings() {
        OpenGL.instance().glDisable(OpenGL.GL_DEPTH_TEST);
        // OpenGL.instance().glEnable(OpenGL.GL_CULL_FACE);
        // OpenGL.instance().glCullFace(OpenGL.GL_BACK);
    }

    private void createDefaultShaderProgram() {
        ShaderProgram defaultShaderProgram = ShaderProgram.create(this);
        defaultShaderProgram.attachShader(Shader.fromFile("Shaders/Default.vertex", ShaderType.VERTEX, this));
        defaultShaderProgram.attachShader(Shader.fromFile("Shaders/Default.fragment", ShaderType.FRAGMENT, this));
        defaultShaderProgram.bindFragDataLocation(0, "fragColor"); // Optional, the shader only has one "out" variable
        defaultShaderProgram.initialize();
        defaultShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setDefaultShaderProgram(defaultShaderProgram);

        ShaderProgram particleUpdaterProgram = ShaderProgram.create(this);
        particleUpdaterProgram
                .attachShader(Shader.fromFile("Shaders/ParticleUpdater.compute", ShaderType.COMPUTE, this));
        particleUpdaterProgram.initialize();
        ShaderProgram.setDefaultParticleUpdaterProgram(particleUpdaterProgram);

        ShaderProgram pointParticleShaderProgram = ShaderProgram.create(this);
        pointParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/PointParticle.vertex", ShaderType.VERTEX, this));
        pointParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/PointParticle.fragment", ShaderType.FRAGMENT, this));
        pointParticleShaderProgram.initialize();
        pointParticleShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setPointParticleShaderProgram(pointParticleShaderProgram);

        ShaderProgram quadParticleShaderProgram = ShaderProgram.create(this);
        quadParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/QuadParticle.geometry", ShaderType.GEOMETRY, this));
        quadParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/QuadParticle.vertex", ShaderType.VERTEX, this));
        quadParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/QuadParticle.fragment", ShaderType.FRAGMENT, this));
        quadParticleShaderProgram.initialize();
        quadParticleShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setQuadParticleShaderProgram(quadParticleShaderProgram);

        ShaderProgram pointShaderProgram = ShaderProgram.create(this);
        pointShaderProgram
                .attachShader(Shader.fromFile("Shaders/Point.vertex", ShaderType.VERTEX, this));
        pointShaderProgram
                .attachShader(Shader.fromFile("Shaders/Point.fragment", ShaderType.FRAGMENT, this));
        pointShaderProgram.initialize();
        pointShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setPointShaderProgram(pointShaderProgram);

        ShaderProgram quadShaderProgram = ShaderProgram.create(this);
        quadShaderProgram
                .attachShader(Shader.fromFile("Shaders/Quad.geometry", ShaderType.GEOMETRY, this));
        quadShaderProgram
                .attachShader(Shader.fromFile("Shaders/Quad.vertex", ShaderType.VERTEX, this));
        quadShaderProgram
                .attachShader(Shader.fromFile("Shaders/Quad.fragment", ShaderType.FRAGMENT, this));
        quadShaderProgram.initialize();
        quadShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setQuadShaderProgram(quadShaderProgram);
    }

    @Override
    public void setViewPort(int x, int y, int width, int height) {
        OpenGL.instance().glViewport(x, y, width, height);
    }

    @Override
    public void setBackgroundColor(Color color) {
        OpenGL.instance().glClearColor(color.getR(), color.getG(), color.getB(), color.getA());
    }

    public boolean hasCursorFocus() {
        return cursorFocus;
    }

    @Override
    protected void onDispose() {
        window.dispose();
    }

    public void exit() {
        mainLoop.stop();
    }

    public boolean isRunning() {
        return mainLoop != null && mainLoop.isRunning();
    }

    public float getFPS() {
        return mainLoop.getFPS();
    }

    public float getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
    }

    public void update(float deltaTime) {

        // Update cycle begins
        updating = true;

        window.pollEvents();

        Frame currentFrame = frame;

        update(currentFrame, deltaTime * speedFactor);

        OpenGL.instance().glClear(OpenGL.GL_COLOR_BUFFER_BIT);
        renderFrame(currentFrame);

        // Update cycle ends
        updating = false;

        while (!invokeLater.isEmpty()) {
            invokeLater.pollFirst().run();
        }
    }

    protected void renderFrame() {
        renderFrame(frame);
    }

    private void renderFrame(Frame frame) {
        window.render(frame, 1.0f);
    }

    protected void loadFrame(Frame frame) {
        loadFrame(frame, FrameDisposal.NONE);
    }

    /**
     * Changes frame while showing a loading frame. The loading frame is displayed
     * while the load-method of the new frame is running. If the game is in the
     * middle of an update cycle, the call to this method will be delayed and
     * invoked at the end of the cycle.
     *
     * @param frame                 - New frame.
     * @param previousFrameDisposal - Disposal of previous frame.
     */
    protected void loadFrame(Frame frame, FrameDisposal previousFrameDisposal) {

        if (loadingFrame == null) {
            throw new GameLibrary2DRuntimeException("No loading frame has been set.");
        }

        if (updating) {
            // Invoke at the end of update cycle
            invokeLater(() -> loadFrame(frame, previousFrameDisposal));
            return;
        }

        if (!frame.isPrepared()) {
            frame.prepare();
        }
        loadingFrame.setPreviousFrame(this.frame);
        loadingFrame.setNextFrame(frame);
        setFrame(loadingFrame, previousFrameDisposal);
        loadingFrame.loadNextFrame();
    }

    public void setFrame(Frame frame, FrameDisposal previousFrameDisposal) {
        if (updating) {
            // Invoke at the end of update cycle
            invokeLater(() -> setFrame(frame, previousFrameDisposal));
            return;
        }

        disposeFrame(previousFrameDisposal);

        if (frame != null) {

            if (!frame.isPrepared()) {
                frame.prepare();
            }

            if (!frame.isLoaded()) {
                try {
                    frame.load();
                } catch (LoadInterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }

            if (!frame.isFinished()) {
                frame.finish();
            }
        }

        this.frame = frame;
        frameNotUpdated = true;

        if (frame != null) {
            onFrameBegin(frame);
        }
    }

    @Override
    public void addFrameChangedListener(FrameChangedListener listener) {
        frameChangedPublisher.addListener(listener);
    }

    @Override
    public void removeFrameChangedListener(FrameChangedListener listener) {
        frameChangedPublisher.removeListener(listener);
    }

    private void onFrameBegin(Frame frame) {
        frameChangedPublisher.publish(frame);
        frame.onBegin();
    }

    private void disposeFrame(FrameDisposal frameDisposal) {
        if (frame == null)
            return;

        frame.onEnd();

        switch (frameDisposal) {

            case NONE:
                break;

            case RESET:
                frame.reset();
                break;

            case DISPOSE:
                frame.dispose();
                break;
        }
    }

    @Override
    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        setFrame(frame, FrameDisposal.NONE);
    }

    protected LoadingFrame getLoadingFrame() {
        return loadingFrame;
    }

    protected void setLoadingFrame(LoadingFrame frame) {
        loadingFrame = frame;
    }

    public Window getWindow() {
        return window;
    }

    public void invokeLater(Runnable runnable) {
        invokeLater.addLast(runnable);
    }

    private void update(Frame frame, float deltaTime) {
        if (frame == null || !frame.isLoaded()) {
            return;
        }

        if (!frame.isFinished()) {
            frame.finish();
        } else {
            frame.update(frameNotUpdated ? 0 : deltaTime);
            frameNotUpdated = false;
        }
    }

    @Override
    public void onKeyCallback(int key, int scancode, int action, int mods) {
        if (action == Keyboard.instance().actionPress()) {
            getFrame().onKeyDown(key, scancode, false, mods);
            FocusManager.keyDownEvent(key, scancode, false, mods);
        } else if (action == Keyboard.instance().actionRepeat()) {
            getFrame().onKeyDown(key, scancode, true, mods);
            FocusManager.keyDownEvent(key, scancode, true, mods);
        } else {
            getFrame().onKeyRelease(key, scancode, mods);
            FocusManager.keyReleaseEvent(key, scancode, mods);
        }
    }

    @Override
    public void onCharCallback(char charInput) {
        getFrame().onCharInput(charInput);
        FocusManager.charInputEvent(charInput);
    }

    @Override
    public void onCursorPosCallback(double xpos, double ypos) {
        cursorPosX = (float) xpos;
        cursorPosY = (float) ypos;
        var frame = getFrame();
        if (frame != null) {
            frame.onMouseMove(cursorPosX, cursorPosY);
        }
    }

    @Override
    public void onCursorEnterCallback(boolean cursorEnter) {
        this.cursorFocus = cursorEnter;
    }

    @Override
    public void onMouseButtonCallback(int button, int action, int mods) {
        if (action == Mouse.instance().actionPress()) {
            getFrame().onMouseButtonDown(button, mods, cursorPosX, cursorPosY);
            FocusManager.mouseButtonEventFinished(button, KeyAction.PRESSED, mods);
        } else if (action == Mouse.instance().actionRelease()) {
            getFrame().onMouseButtonRelease(button, mods, cursorPosX, cursorPosY);
            FocusManager.mouseButtonEventFinished(button, KeyAction.RELEASED, mods);
        }
    }

    @Override
    public void onScrollCallback(double xoffset, double yoffset) {

    }

    protected abstract void onStart();

    protected abstract void onExit();

    private static class GameLoop {

        private final Game game;
        private final Window window;
        private final Timer timer;

        private boolean running;

        GameLoop(Game game, Window window) {
            this.game = game;
            this.window = window;
            timer = Timer.create();
        }

        public void start(Action onStart) {
            running = true;

            timer.init();

            onStart.invoke();

            while (running && !window.isCloseRequested()) {
                game.update((float) timer.update());
            }

            running = false;
        }

        public void stop() {
            running = false;
        }

        boolean isRunning() {
            return running;
        }

        float getFPS() {
            return (float) timer.getUPS();
        }
    }
}