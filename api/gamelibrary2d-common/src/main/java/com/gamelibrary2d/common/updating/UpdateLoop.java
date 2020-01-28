package com.gamelibrary2d.common.updating;

public class UpdateLoop {

    private UpdateTarget updateTarget;
    private double ups;
    private Timer timer;

    private boolean running;

    public UpdateLoop(UpdateTarget updateTarget, int ups) {
        this.updateTarget = updateTarget;
        this.ups = ups;
        timer = new Timer();
    }

    public void run() {
        running = true;

        timer.init();

        while (!Thread.currentThread().isInterrupted() && running) {
            updateTarget.update((float) timer.update());
            synch();
        }

        running = false;
    }

    private void synch() {
        double timePassed = timer.getTime() - timer.getPreviousLoopTime();
        double timeRequired = 1.0 / ups;
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