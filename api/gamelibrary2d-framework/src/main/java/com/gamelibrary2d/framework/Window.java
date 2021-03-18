package com.gamelibrary2d.framework;

public interface Window {

    void initialize();

    void createCallBacks(CallbackHandler game);

    void show();

    void setTitle(String string);

    int getHeight();

    int getWidth();

    void pollEvents();

    void render(Renderable renderable, float alpha);

    void dispose();

    boolean isCloseRequested();

    void focus();
}