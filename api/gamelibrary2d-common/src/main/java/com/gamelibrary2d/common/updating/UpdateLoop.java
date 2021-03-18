package com.gamelibrary2d.common.updating;

public class UpdateLoop {
    private UpdateAction updateAction;
    private double ups;
    private Timer timer;

    private volatile boolean running;

    public UpdateLoop(UpdateAction updateAction, double ups) {
        this.updateAction = updateAction;
        this.ups = ups;
        timer = new DefaultTimer();
    }

    public void run() {
        running = true;

        double prevTime = timer.getTime();
        while (!Thread.currentThread().isInterrupted() && running) {
            double startTime = timer.getTime();
            double deltaTime = startTime - prevTime;
            updateAction.perform((float) (deltaTime));
            synch(startTime);
            prevTime = startTime;
        }

        running = false;
    }

    private void synch(double startTime) {
        double timeRequired = 1.0 / ups;
        double timePassed = timer.getTime() - startTime;
        if (timePassed < timeRequired) {
            try {
                Thread.sleep((long) ((timeRequired - timePassed) * 1000));
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}