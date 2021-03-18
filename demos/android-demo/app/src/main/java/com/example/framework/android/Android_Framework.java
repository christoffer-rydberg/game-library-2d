package com.example.framework.android;

import com.gamelibrary2d.framework.*;

public class Android_Framework implements Framework {

    @Override
    public Joystick getJoystick() {
        return null;
    }

    @Override
    public Keyboard getKeyboard() {
        return null;
    }

    @Override
    public Mouse getMouse() {
        return Android_Mouse.instance();
    }

    @Override
    public OpenGL getOpenGL() {
        return Android_OpenGL.instance();
    }

    @Override
    public GameLoop createDefaultGameLoop() {
        throw new RuntimeException("No default game loop is registered for the Android Framework");
    }

    @Override
    public ImageReader createDefaultImageReader() {
        return new Android_ImageReader();
    }
}
