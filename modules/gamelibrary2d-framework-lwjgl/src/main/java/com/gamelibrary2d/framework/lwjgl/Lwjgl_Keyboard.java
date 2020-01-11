package com.gamelibrary2d.framework.lwjgl;

import com.gamelibrary2d.framework.Keyboard;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;

public class Lwjgl_Keyboard implements Keyboard {

    private static Lwjgl_Keyboard instance;

    private Lwjgl_Keyboard() {
        instance = this;
    }

    public static Lwjgl_Keyboard instance() {
        return instance != null ? instance : new Lwjgl_Keyboard();
    }

    @Override
    public int actionPress() {
        return GLFW.GLFW_PRESS;
    }

    @Override
    public int actionRelease() {
        return GLFW.GLFW_RELEASE;
    }

    @Override
    public int actionRepeat() {
        return GLFW.GLFW_REPEAT;
    }

    @Override
    public int keyEnter() {
        return GLFW.GLFW_KEY_ENTER;
    }

    @Override
    public int keyPadEnter() {
        return GLFW.GLFW_KEY_KP_ENTER;
    }

    @Override
    public int keyTab() {
        return GLFW.GLFW_KEY_TAB;
    }

    @Override
    public int keyBackspace() {
        return GLFW.GLFW_KEY_BACKSPACE;
    }

    @Override
    public int keyEscape() {
        return GLFW.GLFW_KEY_ESCAPE;
    }

    @Override
    public int keySpace() {
        return GLFW.GLFW_KEY_SPACE;
    }

    @Override
    public int keyLeftControl() {
        return GLFW.GLFW_KEY_LEFT_CONTROL;
    }

    @Override
    public int keyRightControl() {
        return GLFW.GLFW_KEY_RIGHT_CONTROL;
    }

    @Override
    public int keyLeft() {
        return GLFW.GLFW_KEY_LEFT;
    }

    @Override
    public int keyRight() {
        return GLFW.GLFW_KEY_RIGHT;
    }

    @Override
    public int keyUp() {
        return GLFW.GLFW_KEY_UP;
    }

    @Override
    public int keyDown() {
        return GLFW.GLFW_KEY_DOWN;
    }

    @Override
    public int keyA() {
        return GLFW.GLFW_KEY_A;
    }

    @Override
    public int keyB() {
        return GLFW.GLFW_KEY_B;
    }

    @Override
    public int keyC() {
        return GLFW.GLFW_KEY_C;
    }

    @Override
    public int keyD() {
        return GLFW.GLFW_KEY_D;
    }

    @Override
    public int keyE() {
        return GLFW.GLFW_KEY_E;
    }

    @Override
    public int keyF() {
        return GLFW.GLFW_KEY_F;
    }

    @Override
    public int keyG() {
        return GLFW.GLFW_KEY_G;
    }

    @Override
    public int keyH() {
        return GLFW.GLFW_KEY_H;
    }

    @Override
    public int keyI() {
        return GLFW.GLFW_KEY_I;
    }

    @Override
    public int keyJ() {
        return GLFW.GLFW_KEY_J;
    }

    @Override
    public int keyK() {
        return GLFW.GLFW_KEY_K;
    }

    @Override
    public int keyL() {
        return GLFW.GLFW_KEY_L;
    }

    @Override
    public int keyM() {
        return GLFW.GLFW_KEY_M;
    }

    @Override
    public int keyN() {
        return GLFW.GLFW_KEY_N;
    }

    @Override
    public int keyO() {
        return GLFW.GLFW_KEY_O;
    }

    @Override
    public int keyP() {
        return GLFW.GLFW_KEY_P;
    }

    @Override
    public int keyQ() {
        return GLFW.GLFW_KEY_Q;
    }

    @Override
    public int keyR() {
        return GLFW.GLFW_KEY_R;
    }

    @Override
    public int keyS() {
        return GLFW.GLFW_KEY_S;
    }

    @Override
    public int keyT() {
        return GLFW.GLFW_KEY_T;
    }

    @Override
    public int keyU() {
        return GLFW.GLFW_KEY_U;
    }

    @Override
    public int keyV() {
        return GLFW.GLFW_KEY_V;
    }

    @Override
    public int keyW() {
        return GLFW.GLFW_KEY_W;
    }

    @Override
    public int keyX() {
        return GLFW.GLFW_KEY_X;
    }

    @Override
    public int keyY() {
        return GLFW.GLFW_KEY_Y;
    }

    @Override
    public int keyZ() {
        return GLFW.GLFW_KEY_Z;
    }

    @Override
    public boolean isKeyDown(int key) {
        return GLFW.glfwGetKey(glfwGetCurrentContext(), key) == actionPress();
    }

    @Override
    public int keyF1() {
        return GLFW.GLFW_KEY_F1;
    }

    @Override
    public int keyF2() {
        return GLFW.GLFW_KEY_F2;
    }

    @Override
    public int keyF3() {
        return GLFW.GLFW_KEY_F3;
    }

    @Override
    public int keyF4() {
        return GLFW.GLFW_KEY_F4;
    }

    @Override
    public int keyF5() {
        return GLFW.GLFW_KEY_F5;
    }

    @Override
    public int keyF6() {
        return GLFW.GLFW_KEY_F6;
    }

    @Override
    public int keyF7() {
        return GLFW.GLFW_KEY_F7;
    }

    @Override
    public int keyF8() {
        return GLFW.GLFW_KEY_F8;
    }

    @Override
    public int keyF9() {
        return GLFW.GLFW_KEY_F9;
    }

    @Override
    public int keyF10() {
        return GLFW.GLFW_KEY_F10;
    }

    @Override
    public int keyF11() {
        return GLFW.GLFW_KEY_F11;
    }

    @Override
    public int keyF12() {
        return GLFW.GLFW_KEY_F12;
    }

    @Override
    public int keyLeftShift() {
        return GLFW.GLFW_KEY_LEFT_SHIFT;
    }

    @Override
    public int keyRightShift() {
        return GLFW.GLFW_KEY_RIGHT_SHIFT;
    }

}
