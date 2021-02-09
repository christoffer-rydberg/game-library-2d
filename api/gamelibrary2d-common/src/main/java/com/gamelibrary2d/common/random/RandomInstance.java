package com.gamelibrary2d.common.random;

import java.util.Collections;
import java.util.List;

/**
 * This class acts as a singleton container for a
 * {@link com.gamelibrary2d.common.random.RandomGenerator RandomGenerator}. The
 * purpose is to gain easy access to random numbers from anywhere, without the
 * need to create or inject object instances. Sometimes it is preferable to use
 * dependency injection for testability. In those cases you should do that.
 * Other times it might be sufficient to specify the random generator that is
 * used by this class. This can be done using the
 * {@link #initialize(RandomGenerator) initialize} method. This class also
 * contains some utility methods related to random numbers.
 * <p>
 * This class is threadsafe and the implementation of the
 * {@link com.gamelibrary2d.common.random.RandomGenerator RandomGenerator} is also
 * expected to be threadsafe.
 */
public class RandomInstance {

    private static RandomGenerator instance;

    /**
     * Gets the random generator instance. If this is the first time this method is
     * called called, and no instance has been set using the
     * {@link #initialize(RandomGenerator) initialize} method, a new
     * {@link com.gamelibrary2d.common.random.JavaRandomGenerator JavaRandomGenerator}
     * will be created and returned.
     */
    public static RandomGenerator get() {
        return instance != null ? instance : create();
    }

    /**
     * Initializes this class with a random generator instance. This method must be
     * called before any other methods of this class, otherwise it will be
     * initialized with a {@link com.gamelibrary2d.common.random.JavaRandomGenerator
     * JavaRandomGenerator}.
     *
     * @param instance The random generator instance.
     */
    public static synchronized void initialize(RandomGenerator instance) {
        if (RandomInstance.instance != null)
            throw new IllegalStateException("RandomInstance has already been initialized.");
        RandomInstance.instance = instance;
    }

    private static synchronized RandomGenerator create() {
        if (instance != null)
            return instance;
        return instance = new JavaRandomGenerator(System.currentTimeMillis());
    }

    /**
     * Creates a random float in the interval [-1, 1]
     */
    public static float random11() {
        return 2 * get().nextFloat() - 1;
    }

    public static <E> List<E> pickNRandomElements(List<E> list, int n) {
        int length = list.size();

        if (length < n)
            return null;

        for (int i = length - 1; i >= length - n; --i)
            Collections.swap(list, i, get().nextInt(i + 1));

        return list.subList(length - n, length);
    }

    /**
     * Picks a number of random elements from the input array and places them in the
     * beginning of the output array. Note that the input array and the output array
     * can be the same instance, in which case the array will be shuffled.
     *
     * @param input  The input array.
     * @param output The output array.
     * @param n      The number of random elements to pick.
     */
    public static <E> void pickNRandomElements(E[] input, E[] output, int n) {
        int length = input.length;
        for (int i = 0; i < n; ++i) {
            int index = get().nextInt(length - i);
            E tmp = input[i];
            output[i] = input[index];
            output[index] = tmp;
        }
    }
}