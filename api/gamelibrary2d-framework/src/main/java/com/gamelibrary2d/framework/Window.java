package com.gamelibrary2d.framework;

public interface Window {

    void setTitle(String string);

    MouseCursorMode getMouseCursorMode();

    void setMouseCursorMode(MouseCursorMode mouseCursorMode);

    int height();

    int width();

    void pollEvents();

    void render(Renderable renderable, float alpha);

    void dispose();

    void initialize();

    void create();

    void show();

    void hide();

    void createCallBacks(CallbackHandler game);

    boolean isCloseRequested();

    void focus();

    long getWindowHandle();
}