package com.gamelibrary2d.common.updating;

public class Timer {

    private int ups;

    /**
     * System time since previous loop.
     */
    private double previousUpdateTime;

    /**
     * Used for FPS and UPS calculation.
     */
    private double elapsedTime;

    /**
     * Counter for the FPS calculation.
     */
    private int updateCounter;

    /**
     * Initializes the timer.
     */
    public void init() {
        previousUpdateTime = getTime();
    }

    /**
     * Returns the time elapsed since <code>glfwInit()</code> in seconds.
     *
     * @return System time in seconds
     */
    public double getTime() {
        return System.nanoTime() / 1000000000.0;
    }

    /**
     * Updates the timer and returns time passed since the last update.
     *
     * @return Time since last update (in seconds).
     */
    public double update() {

        // Get current time
        double time = getTime();

        // Get time since last update (delta)
        double delta = time - previousUpdateTime;

        // Update elapsed time
        elapsedTime += delta;

        if (elapsedTime >= 1d) {
            // Store previous second's update count
            ups = updateCounter;
            updateCounter = 0;
            elapsedTime -= 1d;
        }

        // Increment update counter
        ++updateCounter;

        // Update last update time
        previousUpdateTime = time;

        // Return delta
        return delta;
    }

    /**
     * Getter for the UPS (Updates Per Second)
     */
    public int getUPS() {
        return ups;
    }

    /**
     * Getter for the last loop time.
     *
     * @return System time of the last loop
     */
    public double getPreviousLoopTime() {
        return previousUpdateTime;
    }
}
