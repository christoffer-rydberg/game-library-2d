package com.gamelibrary2d;

import com.gamelibrary2d.common.functional.Action;
import com.gamelibrary2d.framework.Timer;
import com.gamelibrary2d.framework.Window;

class InternalGameLoop {

    private final Game game;
    private final Window window;
    private final Timer timer;

    private boolean running;

    InternalGameLoop(Game game, Window window) {
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