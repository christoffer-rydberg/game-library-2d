package com.gamelibrary2d.framework;

public interface Window {

    void setTitle(String string);

    MouseCursorMode getMouseCursorMode();

    void setMouseCursorMode(MouseCursorMode mouseCursorMode);

    int getHeight();

    int getWidth();

    void pollEvents();

    void render(Renderable renderable, float alpha);

    void dispose();

    void initialize();

    void createWindow();

    void show();

    void hide();

    void createCallBacks(CallbackHandler game);

    boolean isCloseRequested();

    void focus();

    long getWindowHandle();
}