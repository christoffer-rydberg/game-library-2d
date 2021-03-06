package com.gamelibrary2d;

import com.gamelibrary2d.common.Color;
import com.gamelibrary2d.common.disposal.AbstractDisposer;
import com.gamelibrary2d.common.event.DefaultEventPublisher;
import com.gamelibrary2d.common.event.EventPublisher;
import com.gamelibrary2d.exceptions.InitializationException;
import com.gamelibrary2d.components.frames.Frame;
import com.gamelibrary2d.components.frames.FrameDisposal;
import com.gamelibrary2d.components.frames.InitializationContext;
import com.gamelibrary2d.components.frames.LoadingFrame;
import com.gamelibrary2d.framework.Runtime;
import com.gamelibrary2d.framework.*;
import com.gamelibrary2d.glUtil.ShaderProgram;
import com.gamelibrary2d.glUtil.ShaderType;
import com.gamelibrary2d.components.denotations.InputAware;
import com.gamelibrary2d.components.denotations.KeyAware;
import com.gamelibrary2d.resources.DefaultShader;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An abstract implementation of the Game interface. This is a general
 * implementation and can be used as base class for all games.
 */
public abstract class AbstractGame extends AbstractDisposer implements Game {

    private final EventPublisher<Frame> frameChangedPublisher = new DefaultEventPublisher<>();
    /**
     * Queue for code that will be invoked after the current update.
     */
    private final Deque<Runnable> invokeLater;
    /**
     * The OpenGL window used for rendering.
     */
    private Window window;

    /**
     * True whenever the game window has cursor focus, each index represents a pointer id.
     */
    private boolean[] pointerFocus = new boolean[10];
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

    protected AbstractGame(Framework framework) {
        Runtime.initialize(framework);
        invokeLater = new ArrayDeque<>();
    }

    @Override
    public void start(Window window, GameLoop gameLoop) throws InitializationException {
        this.gameLoop = gameLoop;
        this.window = window;
        window.initialize();
        window.setEventListener(new InternalWindowEventListener());
        initializeOpenGLSettings();
        createDefaultShaderPrograms();
        gameLoop.initialize(this::update, this::dispose, window);
        try {
            onStart();
        } catch (IOException e) {
            throw new InitializationException(e);
        }
        window.show();
        gameLoop.start(this::onExit);
    }

    private void initializeOpenGLSettings() {
        OpenGL.instance().glDisable(OpenGL.GL_DEPTH_TEST);
        // OpenGL.instance().glEnable(OpenGL.GL_CULL_FACE);
        // OpenGL.instance().glCullFace(OpenGL.GL_BACK);
    }

    private void createComputeShaderPrograms() {
        ShaderProgram particleUpdaterProgram = ShaderProgram.create(this);
        particleUpdaterProgram
                .attachShader(DefaultShader.fromFile("shaders/ParticleUpdater.compute", ShaderType.COMPUTE, this));
        particleUpdaterProgram.initialize();
        ShaderProgram.setDefaultParticleUpdaterProgram(particleUpdaterProgram);
    }

    private void createGeometryShaderPrograms() {
        ShaderProgram quadParticleShaderProgram = ShaderProgram.create(this);
        quadParticleShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/QuadParticle.geometry", ShaderType.GEOMETRY, this));
        quadParticleShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/QuadParticle.vertex", ShaderType.VERTEX, this));
        quadParticleShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/QuadParticle.fragment", ShaderType.FRAGMENT, this));
        quadParticleShaderProgram.initialize();
        quadParticleShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setQuadParticleShaderProgram(quadParticleShaderProgram);

        ShaderProgram quadShaderProgram = ShaderProgram.create(this);
        quadShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/Quad.geometry", ShaderType.GEOMETRY, this));
        quadShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/Quad.vertex", ShaderType.VERTEX, this));
        quadShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/Quad.fragment", ShaderType.FRAGMENT, this));
        quadShaderProgram.initialize();
        quadShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setQuadShaderProgram(quadShaderProgram);
    }

    private void createVersionSpecificShaderPrograms() {
        OpenGL.OpenGLVersion supportedVersion = Runtime.getFramework().getOpenGL().getSupportedVersion();
        switch (supportedVersion) {
            case OPENGL_ES_3:
                break;
            case OPENGL_ES_3_1:
                try {
                    createGeometryShaderPrograms();
                } catch (Exception e) {
                    System.err.println("Failed to create one or more shader programs. The device might not support the OpenGL ES 3.1 geometry shader extension.");
                    e.printStackTrace();
                }
            case OPENGL_ES_3_2:
                createGeometryShaderPrograms();
                break;
            case OPENGL_CORE_430:
                createGeometryShaderPrograms();
                createComputeShaderPrograms();
                break;
        }
    }

    private void createDefaultShaderPrograms() {
        ShaderProgram defaultShaderProgram = ShaderProgram.create(this);
        defaultShaderProgram.attachShader(DefaultShader.fromFile("shaders/Default.vertex", ShaderType.VERTEX, this));
        defaultShaderProgram.attachShader(DefaultShader.fromFile("shaders/Default.fragment", ShaderType.FRAGMENT, this));
        defaultShaderProgram.initialize();
        defaultShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setDefaultShaderProgram(defaultShaderProgram);

        ShaderProgram pointParticleShaderProgram = ShaderProgram.create(this);
        pointParticleShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/PointParticle.vertex", ShaderType.VERTEX, this));
        pointParticleShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/PointParticle.fragment", ShaderType.FRAGMENT, this));
        pointParticleShaderProgram.initialize();
        pointParticleShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setPointParticleShaderProgram(pointParticleShaderProgram);

        ShaderProgram pointShaderProgram = ShaderProgram.create(this);
        pointShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/Point.vertex", ShaderType.VERTEX, this));
        pointShaderProgram
                .attachShader(DefaultShader.fromFile("shaders/Point.fragment", ShaderType.FRAGMENT, this));
        pointShaderProgram.initialize();
        pointShaderProgram.initializeMvp(window.getWidth(), window.getHeight());
        ShaderProgram.setPointShaderProgram(pointShaderProgram);

        createVersionSpecificShaderPrograms();
    }

    @Override
    public void setViewPort(int x, int y, int width, int height) {
        OpenGL.instance().glViewport(x, y, width, height);
    }

    @Override
    public void setBackgroundColor(Color color) {
        OpenGL.instance().glClearColor(color.getR(), color.getG(), color.getB(), color.getA());
    }

    @Override
    public boolean hasPointerFocus(int id) {
        return id < pointerFocus.length && pointerFocus[id];
    }

    @Override
    protected void onDispose() {
        window.dispose();
    }

    @Override
    public void exit() {
        gameLoop.stop();
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

        render(currentFrame);

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

    @Override
    public void render() {
        render(frame);
    }

    private void render(Frame frame) {
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

        Frame previousFrame = this.frame;
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
                    InitializationContext context = frame.load();
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

    protected abstract void onStart() throws InitializationException, IOException;

    protected abstract void onExit();

    private class InternalWindowEventListener implements WindowEventListener {

        @Override
        public void onKeyAction(int key, KeyAction action) {
            Frame frame = getFrame();
            switch (action) {
                case DOWN:
                    if (frame instanceof KeyAware)
                        ((KeyAware) frame).keyDown(key, false);
                    FocusManager.keyDownEvent(key, false);
                    break;
                case DOWN_REPEAT:
                    if (frame instanceof KeyAware)
                        ((KeyAware) frame).keyDown(key, true);
                    FocusManager.keyDownEvent(key, true);
                    break;
                case UP:
                    if (frame instanceof KeyAware)
                        ((KeyAware) frame).keyUp(key);
                    FocusManager.keyUpEvent(key);
                    break;
            }
        }

        @Override
        public void onCharInput(char charInput) {
            if (frame instanceof InputAware)
                ((InputAware) frame).charInput(charInput);
            FocusManager.charInputEvent(charInput);
        }

        @Override
        public void onPointerMove(int id, float posX, float posY) {
            try {
                FocusManager.onPointerActive();
                Frame frame = getFrame();
                if (frame != null) {
                    frame.pointerMove(id, posX, posY, posX, posY);
                }
            } finally {
                FocusManager.onPointerInactive();
            }
        }

        @Override
        public void onPointerEnter(int id) {
            if (id < pointerFocus.length) {
                pointerFocus[id] = true;
            }
        }

        @Override
        public void onPointerLeave(int id) {
            if (id < pointerFocus.length) {
                pointerFocus[id] = false;
            }
        }

        @Override
        public void onPointerAction(int id, int button, float posX, float posY, PointerAction action) {
            try {
                FocusManager.onPointerActive();
                switch (action) {
                    case DOWN:
                        getFrame().pointerDown(id, button, posX, posY, posX, posY);
                        FocusManager.pointerDownFinished(id, button);
                        break;
                    case UP:
                        getFrame().pointerUp(id, button, posX, posY, posX, posY);
                        FocusManager.pointerUpFinished(id, button);
                        break;
                }
            } finally {
                FocusManager.onPointerInactive();
            }
        }

        @Override
        public void onScroll(int id, float xOffset, float yOffset) {

        }
    }
}