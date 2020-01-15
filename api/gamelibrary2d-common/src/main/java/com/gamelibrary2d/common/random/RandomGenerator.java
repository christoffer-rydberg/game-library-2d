package com.gamelibrary2d.common.random;

/**
 * Interface to allow dependency injection of random generators and/or to
 * specify which random generator is used by the
 * {@link com.gamelibrary2d.common.random.RandomInstance RandomInstance} class.
 */
public interface RandomGenerator {

    boolean nextBoolean();

    void nextBytes(byte[] bytes);

    double nextDouble();

    float nextFloat();

    double nextGaussian();

    int nextInt();

    int nextInt(int n);

    long nextLong();

    void setSeed(long seed);
}