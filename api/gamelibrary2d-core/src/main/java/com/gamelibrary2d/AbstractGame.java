package com.gamelibrary2d;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.disposal.AbstractDisposer;
import com.gamelibrary2d.common.event.DefaultEventPublisher;
import com.gamelibrary2d.common.event.EventPublisher;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.frames.Frame;
import com.gamelibrary2d.frames.FrameDisposal;
import com.gamelibrary2d.frames.LoadingFrame;
import com.gamelibrary2d.framework.Runtime;
import com.gamelibrary2d.framework.*;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.ShaderType;
import com.gamelibrary2d.markers.KeyAware;
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
     * The current frame.
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
     * The game loop is responsible for maintaining a steady frame rate.
     */
    private GameLoop gameLoop;

    /**
     * Speed factor each update, applied to the delta-time
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
    public void start(Window window) throws InitializationException {
        this.window = window;

        window.initialize();
        window.create();
        window.createCallBacks(this);

        initializeOpenGLSettings();
        createDefaultShaderProgram();

        invokeLater = new ArrayDeque<>();
        gameLoop = new GameLoop(this, window);

        gameLoop.run(() -> {
            onStart();
            window.show();
        });

        onExit();

        // Dispose objects registered to this game
        dispose();

        Runtime.dispose();
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
        defaultShaderProgram.initializeMvp(window.width(), window.height());
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
        pointParticleShaderProgram.initializeMvp(window.width(), window.height());
        ShaderProgram.setPointParticleShaderProgram(pointParticleShaderProgram);

        ShaderProgram quadParticleShaderProgram = ShaderProgram.create(this);
        quadParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/QuadParticle.geometry", ShaderType.GEOMETRY, this));
        quadParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/QuadParticle.vertex", ShaderType.VERTEX, this));
        quadParticleShaderProgram
                .attachShader(Shader.fromFile("Shaders/QuadParticle.fragment", ShaderType.FRAGMENT, this));
        quadParticleShaderProgram.initialize();
        quadParticleShaderProgram.initializeMvp(window.width(), window.height());
        ShaderProgram.setQuadParticleShaderProgram(quadParticleShaderProgram);

        ShaderProgram pointShaderProgram = ShaderProgram.create(this);
        pointShaderProgram
                .attachShader(Shader.fromFile("Shaders/Point.vertex", ShaderType.VERTEX, this));
        pointShaderProgram
                .attachShader(Shader.fromFile("Shaders/Point.fragment", ShaderType.FRAGMENT, this));
        pointShaderProgram.initialize();
        pointShaderProgram.initializeMvp(window.width(), window.height());
        ShaderProgram.setPointShaderProgram(pointShaderProgram);

        ShaderProgram quadShaderProgram = ShaderProgram.create(this);
        quadShaderProgram
                .attachShader(Shader.fromFile("Shaders/Quad.geometry", ShaderType.GEOMETRY, this));
        quadShaderProgram
                .attachShader(Shader.fromFile("Shaders/Quad.vertex", ShaderType.VERTEX, this));
        quadShaderProgram
                .attachShader(Shader.fromFile("Shaders/Quad.fragment", ShaderType.FRAGMENT, this));
        quadShaderProgram.initialize();
        quadShaderProgram.initializeMvp(window.width(), window.height());
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

    @Override
    public void exit() {
        gameLoop.stop();
    }

    @Override
    public float getFPS() {
        return gameLoop.getFPS();
    }

    protected float getSpeedFactor() {
        return speedFactor;
    }

    protected void setSpeedFactor(float speedFactor) {
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

    private void update(Frame frame, float deltaTime) {
        if (frame != null) {
            frame.update(frameNotUpdated ? 0 : deltaTime);
            frameNotUpdated = false;
        }
    }

    protected void renderFrame() {
        renderFrame(frame);
    }

    private void renderFrame(Frame frame) {
        window.render(frame, 1.0f);
    }

    @Override
    public void loadFrame(Frame frame, FrameDisposal previousFrameDisposal) throws InitializationException {
        if (loadingFrame == null) {
            throw new InitializationException("No loading frame has been set.");
        }

        if (!frame.isInitialized()) {
            frame.initialize(this);
        }

        var previousFrame = this.frame;
        setFrame(loadingFrame, FrameDisposal.NONE);
        loadingFrame.load(frame, previousFrame, previousFrameDisposal);
    }

    @Override
    public void setFrame(Frame frame, FrameDisposal previousFrameDisposal) throws InitializationException {
        if (frame != null) {
            if (!frame.isInitialized()) {
                frame.initialize(this);
            }

            if (!frame.isLoaded()) {
                try {
                    var context = frame.load();
                    frame.loaded(context);
                } catch (InitializationException e) {
                    frame.dispose(FrameDisposal.UNLOAD);
                    throw e;
                }
            }
        }

        disposeFrame(previousFrameDisposal);

        if (updating) {
            invokeLater(() -> beginFrame(frame));
        } else {
            beginFrame(frame);
        }
    }

    private void beginFrame(Frame frame) {
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
        frame.begin();
    }

    private void disposeFrame(FrameDisposal frameDisposal) {
        if (frame == null)
            return;
        frame.end();
        frame.dispose(frameDisposal);
    }

    @Override
    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) throws InitializationException {
        setFrame(frame, FrameDisposal.NONE);
    }

    @Override
    public LoadingFrame getLoadingFrame() {
        return loadingFrame;
    }

    @Override
    public void setLoadingFrame(LoadingFrame frame) {
        loadingFrame = frame;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public void invokeLater(Runnable runnable) {
        invokeLater.addLast(runnable);
    }

    @Override
    public void onKeyCallback(int key, int scancode, int action, int mods) {
        var frame = getFrame();
        if (action == Keyboard.instance().actionPressed()) {
            if (frame instanceof KeyAware)
                ((KeyAware) frame).keyDown(key, scancode, false, mods);
            FocusManager.keyDownEvent(key, scancode, false, mods);
        } else if (action == Keyboard.instance().actionRepeat()) {
            if (frame instanceof KeyAware)
                ((KeyAware) frame).keyDown(key, scancode, true, mods);
            FocusManager.keyDownEvent(key, scancode, true, mods);
        } else {
            if (frame instanceof KeyAware)
                ((KeyAware) frame).keyReleased(key, scancode, mods);
            FocusManager.keyReleaseEvent(key, scancode, mods);
        }
    }

    @Override
    public void onCharCallback(char charInput) {
        if (frame instanceof KeyAware)
            ((KeyAware) frame).charInput(charInput);
        FocusManager.charInputEvent(charInput);
    }

    @Override
    public void onCursorPosCallback(double xpos, double ypos) {
        cursorPosX = (float) xpos;
        cursorPosY = (float) ypos;
        var frame = getFrame();
        if (frame != null) {
            frame.mouseMove(cursorPosX, cursorPosY, cursorPosX, cursorPosY);
        }
    }

    @Override
    public void onCursorEnterCallback(boolean cursorEnter) {
        this.cursorFocus = cursorEnter;
    }

    @Override
    public void onMouseButtonCallback(int button, int action, int mods) {
        if (action == Mouse.instance().actionPressed()) {
            getFrame().mouseButtonDown(button, mods, cursorPosX, cursorPosY, cursorPosX, cursorPosY);
            FocusManager.mouseButtonDownFinished(button, mods);
        } else if (action == Mouse.instance().actionReleased()) {
            getFrame().mouseButtonReleased(button, mods, cursorPosX, cursorPosY, cursorPosX, cursorPosY);
            FocusManager.mouseButtonReleasedFinished(button, mods);
        }
    }

    @Override
    public void onScrollCallback(double xoffset, double yoffset) {

    }

    protected abstract void onStart() throws InitializationException;

    protected abstract void onExit();

    private static class GameLoop {
        private final Game game;
        private final Window window;
        private final Timer timer;

        private volatile boolean running;

        GameLoop(Game game, Window window) {
            this.game = game;
            this.window = window;
            timer = Timer.create();
        }

        void run(StartAction onStart) throws InitializationException {
            running = true;

            onStart.invoke();

            timer.init();
            while (running && !window.isCloseRequested()) {
                game.update((float) timer.update());
            }

            running = false;
        }

        void stop() {
            running = false;
        }

        float getFPS() {
            return (float) timer.getUPS();
        }

        private interface StartAction {
            void invoke() throws InitializationException;
        }
    }
}