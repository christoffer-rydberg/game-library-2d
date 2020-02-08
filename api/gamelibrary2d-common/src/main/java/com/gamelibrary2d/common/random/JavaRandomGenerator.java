package com.gamelibrary2d.common.random;

import java.util.Random;

/**
 * This is the default random generator which wraps the {@link java.util.Random
 * java.util.Random} class.
 */
public class JavaRandomGenerator implements RandomGenerator {

    private final Random random;

    public JavaRandomGenerator() {
        random = new Random();
    }

    public JavaRandomGenerator(long seed) {
        random = new Random(seed);
    }

    @Override
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    @Override
    public void nextBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }

    @Override
    public float nextFloat() {
        return random.nextFloat();
    }

    @Override
    public double nextGaussian() {
        return random.nextGaussian();
    }

    @Override
    public int nextInt() {
        return random.nextInt();
    }

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    @Override
    public long nextLong() {
        return random.nextLong();
    }

    @Override
    public void setSeed(long seed) {
        random.setSeed(seed);
    }
}