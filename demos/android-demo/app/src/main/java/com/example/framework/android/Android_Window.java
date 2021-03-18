package com.example.framework.android;

import android.app.Activity;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import com.gamelibrary2d.framework.*;

class Android_Window extends GLSurfaceView implements Window {
    private final Activity activity;
    private final MotionEventStash motionEventStash = new MotionEventStash(100);

    private CallbackHandler callbackHandler;

    Android_Window(Activity activity) {
        super(activity);
        this.activity = activity;
        setEGLContextClientVersion(3);
    }

    OpenGL.OpenGLVersion getSupportedOpenGLVersion() {
        int[] vers = new int[2];
        GLES30.glGetIntegerv(GLES30.GL_MAJOR_VERSION, vers, 0);
        GLES30.glGetIntegerv(GLES30.GL_MINOR_VERSION, vers, 1);
        int major = vers[0];
        int minor = vers[1];
        if (major > 3) {
            return OpenGL.OpenGLVersion.OPENGL_ES_3_2;
        } else if (major == 3 && minor == 0) {
            return OpenGL.OpenGLVersion.OPENGL_ES_3;
        } else if (major == 3 && minor == 1) {
            return OpenGL.OpenGLVersion.OPENGL_ES_3_1;
        } else if (major == 3 && minor >= 2) {
            return OpenGL.OpenGLVersion.OPENGL_ES_3_2;
        } else {
            throw new RuntimeException(String.format("Unsupported OpenGL version: %d.%d", major, minor));
        }
    }

    @Override
    public void setTitle(String title) {
        activity.setTitle(title);
    }

    @Override
    public void render(Renderable renderable, float alpha) {
        if (renderable != null) {
            renderable.render(alpha);
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void show() {

    }

    @Override
    public boolean isCloseRequested() {
        return false;
    }

    @Override
    public void focus() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void createCallBacks(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (callbackHandler == null) {
            return false;
        }

        motionEventStash.stashEvent(e);
        return true;
    }

    @Override
    public void pollEvents() {
        motionEventStash.pollEvents();
        motionEventStash.triggerEvents(callbackHandler);
    }

    private class MotionEventStash {
        private final int[] stashedEvents;
        private final int[] polledEvents;

        private int stashPointer = 0;
        private int polledPointer = 0;

        MotionEventStash(int capacity) {
            stashedEvents = new int[3 * capacity];
            polledEvents = new int[3 * capacity];
        }

        synchronized void stashEvent(MotionEvent e) {
            stashedEvents[stashPointer] = e.getAction();
            stashedEvents[stashPointer + 1] = (int) e.getX();
            stashedEvents[stashPointer + 2] = (int) e.getY();
            stashPointer += 3;
        }

        synchronized void pollEvents() {
            System.arraycopy(stashedEvents, 0, polledEvents, polledPointer, stashPointer);
            polledPointer += stashPointer;
            stashPointer = 0;
        }

        private int getAction(int offset) {
            return polledEvents[offset];
        }

        private float getX(int offset) {
            return polledEvents[offset + 1];
        }

        private float getY(int offset) {
            return getHeight() - polledEvents[offset + 2];
        }

        void triggerEvents(CallbackHandler callbackHandler) {
            for (int i = 0; i < polledPointer; i += 3) {
                switch (getAction(i)) {
                    case MotionEvent.ACTION_DOWN:
                        callbackHandler.onCursorPosCallback(getX(i), getY(i));
                        callbackHandler.onMouseButtonCallback(0, Mouse.instance().actionPressed(), 0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        callbackHandler.onCursorPosCallback(getX(i), getY(i));
                        break;
                    case MotionEvent.ACTION_UP:
                        callbackHandler.onMouseButtonCallback(0, Mouse.instance().actionReleased(), 0);
                        break;
                }
            }

            polledPointer = 0;
        }
    }
}