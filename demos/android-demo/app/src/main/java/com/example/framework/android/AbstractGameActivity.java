package com.example.framework.android;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.functional.Func;
import com.gamelibrary2d.exceptions.InitializationException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class AbstractGameActivity extends Activity {
    private final Func<Activity, Game> gameFactory;

    protected AbstractGameActivity(Game game) {
        gameFactory = a -> game;
    }

    protected AbstractGameActivity(Func<Activity, Game> gameFactory) {
        this.gameFactory = gameFactory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.lockOrientation(this);
        Game game = gameFactory.invoke(this);
        Android_Window window = new Android_Window(this);
        window.setRenderer(new GLSurfaceViewRenderer(game, window));
        setContentView(window);
    }

    private static class GLSurfaceViewRenderer implements GLSurfaceView.Renderer {
        private final Game game;
        private final Android_Window window;
        private final Android_GameLoop gameLoop;

        GLSurfaceViewRenderer(Game game, Android_Window window) {
            this.game = game;
            this.window = window;
            this.gameLoop = new Android_GameLoop();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            try {
                Android_OpenGL.createInstance(window.getSupportedOpenGLVersion());
                game.start(window, gameLoop);
            } catch (InitializationException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceChanged(GL10 unused, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (gameLoop.isInitialized()) {
                if (gameLoop.isRunning()) {
                    gameLoop.triggerUpdate();
                } else {
                    gameLoop.getDisposeAction().perform();
                    gameLoop.deinitialize();
                    // TODO: Exit game?
                }
            }
        }
    }
}
