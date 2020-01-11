package com.gamelibrary2d.lightning;

public class DefaultLightSpreadMatrix implements LightSpreadMatrix {

    private final float[][] lightSpreadMatrix;

    public DefaultLightSpreadMatrix(int range) {
        lightSpreadMatrix = new float[range][range];
        double maxDistSqrt = Math.sqrt(lightSpreadMatrix.length);
        for (int col = 0; col < lightSpreadMatrix.length; ++col) {
            for (int row = 0; row < lightSpreadMatrix.length; ++row) {
                double dist = Math.sqrt(col * col + row * row);
                double gauss = gauss(dist, 1, 0, maxDistSqrt);
                lightSpreadMatrix[col][row] = (float) gauss;
            }
        }
    }

    @Override
    public float getLightStrengthFactor(int distX, int distY) {
        return lightSpreadMatrix[Math.abs(distX)][Math.abs(distY)];
    }

    @Override
    public int getRange() {
        return lightSpreadMatrix.length;
    }

    /**
     * Computes the gaussian value, f(x).
     *
     * @param x                 The gaussian input parameter
     * @param peak              The height of the curve's peak
     * @param center            The position of the center of the peak
     * @param standardDeviation The standard deviation
     * @return The gaussian value.
     */
    private double gauss(double x, double peak, double center, double standardDeviation) {
        return peak * Math.exp(-(Math.pow(x - center, 2) / (2 * Math.pow(standardDeviation, 2))));
    }
}