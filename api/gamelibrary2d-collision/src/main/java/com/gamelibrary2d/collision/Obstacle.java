package com.gamelibrary2d.collision;

public interface Obstacle extends Collidable {

    /**
     * The mass is used to determine the acceleration
     * when two obstacles {@link #onPushed push} each other.
     */
    float getMass();

    /**
     * Invoked when another {@link Obstacle} pushes this object.
     *
     * @param pusher        The pusher.
     * @param accelerationX The acceleration along the X-axis.
     * @param accelerationY The acceleration along the Y-axis.
     */
    void onPushed(Obstacle pusher, float accelerationX, float accelerationY);
}
