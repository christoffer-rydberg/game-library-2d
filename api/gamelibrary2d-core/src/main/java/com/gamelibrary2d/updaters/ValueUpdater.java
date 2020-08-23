package com.gamelibrary2d.updaters;

/**
 * The purpose of this {@link Updater} is to update a float value until it
 * reaches a specified goal. If you want to apply updates for a certain
 * duration, without a known goal, consider using the {@link DurationUpdater}
 * instead. The {@link ValueUpdater} is created by invoking one of its two
 * factory methods: {@link #fromDelta} or {@link #fromDuration}.
 */
public class ValueUpdater implements Updater {

    private static final float NOT_SET = -1;

    private final ValueGetter getter;

    private final ValueSetter setter;

    private final float goalValue;

    private final boolean initFromDuration;

    private float duration;

    private float deltaValue;

    private float timer;

    private ValueUpdater(ValueGetter getter, ValueSetter setter, float goalValue, float duration) {
        this.getter = getter;
        this.setter = setter;
        this.goalValue = goalValue;
        this.duration = duration;
        initFromDuration = duration != NOT_SET;
    }

    /**
     * Creates a {@link ValueUpdater} by specifying a goal- and delta value.
     *
     * @param getter     Getter for the value that will be updated.
     * @param setter     Setter for the value that will be updated.
     * @param goalValue  The goal value.
     * @param deltaValue The change in value per second.
     */
    public static ValueUpdater fromDelta(ValueGetter getter, ValueSetter setter, float goalValue, float deltaValue) {

        deltaValue = Math.abs(deltaValue);
        if (deltaValue == 0)
            throw new IllegalArgumentException("Delta value cannot be 0.");

        ValueUpdater updater = new ValueUpdater(getter, setter, goalValue, NOT_SET);
        updater.deltaValue = Math.abs(deltaValue);
        return updater;
    }

    /**
     * Creates a {@link ValueUpdater} by specifying a goal value and a duration.
     *
     * @param getter    Getter for the value that will be updated.
     * @param setter    Setter for the value that will be updated.
     * @param goalValue The goal value.
     * @param duration  The time in seconds it will take to reach the goal value.
     */
    public static ValueUpdater fromDuration(ValueGetter getter, ValueSetter setter, float goalValue, float duration) {

        if (duration <= 0)
            throw new IllegalArgumentException("Duration must be greater than 0.");

        return new ValueUpdater(getter, setter, goalValue, duration);
    }

    /**
     * @return The goal value of the updater.
     */
    public float getGoalValue() {
        return goalValue;
    }

    /**
     * @return The change in value per second. This value is available if the
     * {@link ValueUpdater} was created with the {@link #fromDelta} method,
     * otherwise -1 is returned.
     */
    public float getDeltaValue() {
        return duration == NOT_SET ? deltaValue : NOT_SET;
    }

    /**
     * @return The time in seconds it will take to reach the goal value. This value
     * is available if the {@link ValueUpdater} was created with the
     * {@link #fromDuration} method, otherwise -1 is returned.
     */
    public float getDuration() {
        return duration;
    }

    @Override
    public void reset() {
        timer = 0;
    }

    @Override
    public boolean isFinished() {
        return timer > 0 && timer >= duration;
    }

    @Override
    public float update(float deltaTime) {
        if (deltaTime <= 0) {
            return 0f;
        }

        float value = getter.get();

        float remaining = Math.abs(goalValue - value);

        if (timer == 0) {
            if (initFromDuration) {
                deltaValue = remaining / duration;
            } else {
                duration = remaining / deltaValue;
            }
        }

        float delta = deltaTime * deltaValue;
        float updatedValue = goalValue > value ? value + delta : value - delta;

        timer += deltaTime;
        if (timer >= duration) {
            setter.set(goalValue);
            return (remaining / delta) * deltaTime;
        }

        setter.set(updatedValue);

        return deltaTime;
    }
}