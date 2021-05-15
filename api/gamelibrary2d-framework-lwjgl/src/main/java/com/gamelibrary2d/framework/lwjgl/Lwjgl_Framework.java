package com.gamelibrary2d.framework.lwjgl;

import com.gamelibrary2d.framework.*;

public class Lwjgl_Framework implements Framework {
    private final Mouse mouse = new Lwjgl_Mouse();
    private final Keyboard keyboard = new Lwjgl_Keyboard();

    @Override
    public Joystick getJoystick() {
        return Lwjgl_Joystick.instance();
    }

    @Override
    public Keyboard getKeyboard() {
        return keyboard;
    }

    @Override
    public Mouse getMouse() {
        return mouse;
    }

    @Override
    public OpenGL getOpenGL() {
        return Lwjgl_OpenGL.instance();
    }

    @Override
    public GameLoop createDefaultGameLoop() {
        return new Lwjgl_GameLoop();
    }

    @Override
    public ImageReader createDefaultImageReader() {
        return new Lwjgl_ImageReader();
    }
}