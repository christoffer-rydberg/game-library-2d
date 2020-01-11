package com.gamelibrary2d.framework;

public interface Timer {

    static Timer create() {
        return Runtime.getFramework().createTimer();
    }

    void init();

    double update();

    int getUPS();
}
