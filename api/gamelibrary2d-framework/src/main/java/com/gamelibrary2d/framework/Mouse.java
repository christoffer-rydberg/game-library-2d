package com.gamelibrary2d.framework;

public interface Mouse {

    static Mouse instance() {
        return Runtime.getFramework().getMouse();
    }

    int actionPress();

    int actionRelease();

    int mouseButton1();

    int mouseButton2();

    boolean isButtonDown(int button);
}