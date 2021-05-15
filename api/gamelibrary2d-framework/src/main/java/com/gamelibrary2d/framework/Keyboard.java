package com.gamelibrary2d.framework;

public interface Keyboard {

    static Keyboard instance() {
        return Runtime.getFramework().getKeyboard();
    }

    int keyEnter();

    int keyTab();

    int keyBackspace();

    int keyEscape();

    int keySpace();

    int keyLeftControl();

    int keyRightControl();

    int keyLeftShift();

    int keyRightShift();

    int keyPadEnter();

    int keyLeft();

    int keyRight();

    int keyUp();

    int keyDown();

    int keyF1();

    int keyF2();

    int keyF3();

    int keyF4();

    int keyF5();

    int keyF6();

    int keyF7();

    int keyF8();

    int keyF9();

    int keyF10();

    int keyF11();

    int keyF12();

    int keyA();

    int keyB();

    int keyC();

    int keyD();

    int keyE();

    int keyF();

    int keyG();

    int keyH();

    int keyI();

    int keyJ();

    int keyK();

    int keyL();

    int keyM();

    int keyN();

    int keyO();

    int keyP();

    int keyQ();

    int keyR();

    int keyS();

    int keyT();

    int keyU();

    int keyV();

    int keyW();

    int keyX();

    int keyY();

    int keyZ();

    boolean isKeyDown(int key);
}