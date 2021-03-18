package com.gamelibrary2d.framework;

public interface Framework {

    Joystick getJoystick();

    Keyboard getKeyboard();

    Mouse getMouse();

    OpenGL getOpenGL();

    GameLoop createDefaultGameLoop();

    ImageReader createDefaultImageReader();
}