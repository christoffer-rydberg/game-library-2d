package com.gamelibrary2d.objects;

/**
 * The purpose of this class is to keep track of active buttons. It uses an
 * internal array to save the button states, where each button id is used as an
 * index in the array. This requires the button id's to be sequential and low,
 * preferably starting at 0. Otherwise the array risk getting very big, which
 * can affect memory usage and efficiency.
 *
 * @author Christoffer Rydberg
 */
class InternalButtonStateArray {

    private int activeButtons;

    private boolean[] buttons;

    private int arraySize = 0;

    public InternalButtonStateArray(int initialArraySize) {
        this.buttons = new boolean[initialArraySize];
    }

    public void clear() {
        for (int i = 0; i < arraySize; ++i) {
            buttons[i] = false;
        }
        arraySize = 0;
    }

    public void setActive(int button, boolean active) {

        arraySize = Math.max(arraySize, button + 1);

        if (button > buttons.length) {
            buttons = new boolean[arraySize];
        }

        if (active != buttons[button]) {
            activeButtons += active ? 1 : -1;
            buttons[button] = active;
        }
    }

    public boolean hasActiveButtons() {
        return activeButtons > 0;
    }

    public boolean isActive(int button) {
        return button < buttons.length ? buttons[button] : false;
    }
}