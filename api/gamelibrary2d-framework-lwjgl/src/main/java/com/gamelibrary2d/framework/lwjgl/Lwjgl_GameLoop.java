package com.gamelibrary2d.framework.lwjgl;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.common.updating.Timer;
import com.gamelibrary2d.common.updating.UpdateAction;
import com.gamelibrary2d.framework.GameLoop;
import com.gamelibrary2d.framework.Runtime;
import com.gamelibrary2d.framework.Window;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

public class Lwjgl_GameLoop implements GameLoop {
    private final Timer timer = GLFW::glfwGetTime;

    private final AtomicBoolean running = new AtomicBoolean();
    private UpdateAction updateAction;
    private Action disposeAction;
    private Window window;
    private volatile Action onExit;

    @Override
    public void initialize(UpdateAction updateAction, Action disposeAction, Window window) {
        this.updateAction = updateAction;
        this.disposeAction = disposeAction;
        this.window = window;
        running.set(true);
    }

    @Override
    public void start(Action onExit) {
        double prevTime = timer.getTime();
        while (running.get() && !window.isCloseRequested()) {
            double startTime = timer.getTime();
            double deltaTime = startTime - prevTime;
            updateAction.perform((float) deltaTime);
            prevTime = startTime;
        }

        running.set(false);

        onExit.perform();

        disposeAction.perform();

        Runtime.dispose();
    }

    @Override
    public void stop() {
        running.set(false);
    }
}
