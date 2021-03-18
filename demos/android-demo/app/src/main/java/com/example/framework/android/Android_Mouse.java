package com.example.framework.android;

import com.gamelibrary2d.framework.Mouse;

public class Android_Mouse implements Mouse {

    private static Android_Mouse instance;

    private Android_Mouse() {
        instance = this;
    }

    public static Android_Mouse instance() {
        return instance != null ? instance : new Android_Mouse();
    }

    @Override
    public int actionPressed() {
        return 1;
    }

    @Override
    public int actionReleased() {
        return 0;
    }

    @Override
    public int mouseButton1() {
        return 0;
    }

    @Override
    public int mouseButton2() {
        return 1;
    }

    @Override
    public boolean isButtonDown(int button) {
        return false;
    }
}
