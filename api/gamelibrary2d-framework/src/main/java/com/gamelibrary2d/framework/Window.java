package com.gamelibrary2d.framework;

public interface Window {

    void initialize();

    void setEventListener(WindowEventListener eventListener);

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