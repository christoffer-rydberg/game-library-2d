package com.gamelibrary2d.input;

public abstract class AbstractInputBinding implements InputBinding {
    private final float activeThreshold;
    private final float inactiveThreshold;

    private InputState state = InputState.INACTIVE;

    private float value;

    /**
     * @param activeThreshold   The value threshold going from inactive to active.
     * @param inactiveThreshold The value threshold going from active to inactive.
     */
    protected AbstractInputBinding(float activeThreshold, float inactiveThreshold) {
        this.activeThreshold = activeThreshold;
        this.inactiveThreshold = inactiveThreshold;
    }

    @Override
    public void update(float deltaTime) {
        value = getValueFromSource();
        switch (state) {
            case ACTIVE:
                if (Math.abs(value) >= inactiveThreshold) {
                    onStateUnchanged(state, deltaTime);
                } else {
                    state = InputState.INACTIVE;
                    onStateChanged(state, deltaTime);
                }
            case INACTIVE:
                if (Math.abs(value) >= activeThreshold) {
                    state = InputState.ACTIVE;
                    onStateChanged(state, deltaTime);
                } else {
                    onStateUnchanged(state, deltaTime);
                }
        }
    }

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public InputState getState() {
        return state;
    }

    /**
     * Resets the {@link #getState state} to the specified value.
     */
    protected void setState(InputState state) {
        this.state = state;
    }

    /**
     * Reads the input value from the input source.
     */
    protected abstract float getValueFromSource();

    /**
     * Invoked when the current {@link InputState} is unchanged since the last update.
     *
     * @param state     The current input state.
     * @param deltaTime The time in seconds since the last update.
     */
    protected abstract void onStateUnchanged(InputState state, float deltaTime);

    /**
     * Invoked when the current {@link InputState} is changed since the last update.
     *
     * @param newState  The new input state.
     * @param deltaTime The time in seconds since the last update.
     */
    protected abstract void onStateChanged(InputState newState, float deltaTime);
}
