package com.gamelibrary2d.lightning;

import java.util.Arrays;

class InternalDynamicLight {

    private static float NOT_SET = -Float.MAX_VALUE;

    private final int cols;
    private final int rows;
    private final float[] light;
    private final int[] lightIndices;
    private final int extend;

    private int indexSize;
    private int currentIndex;

    InternalDynamicLight(int cols, int rows, int extend) {
        this.cols = cols + (extend * 2);
        this.rows = rows + (extend * 2);
        this.extend = extend;
        int size = this.cols * this.rows;
        light = new float[size];
        lightIndices = new int[size];
        Arrays.fill(light, NOT_SET);
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getIndex(int col, int row) {
        return row * cols + col;
    }

    public void add(int col, int row, float value) {
        int internalCol = col + extend;
        if (internalCol < 0 || internalCol >= cols)
            return;
        int internalRow = row + extend;
        if (internalRow < 0 || internalRow >= rows)
            return;
        add(getIndex(internalCol, internalRow), value);
    }

    private void add(int index, float value) {
        if (light[index] == NOT_SET) {
            light[index] = value;
            lightIndices[indexSize] = index;
            ++indexSize;
        } else {
            light[index] += value;
        }
        currentIndex = indexSize;
    }

    public boolean moveNext() {
        if (currentIndex == 0) {
            return false;
        }
        --currentIndex;
        return true;
    }

    public int currentSourceX() {
        return currentIndex() % cols - extend;
    }

    public int currentSourceY() {
        return currentIndex() / cols - extend;
    }

    public float currentValue() {
        return light[currentIndex()];
    }

    private int currentIndex() {
        return lightIndices[currentIndex];
    }

    void reset() {
        for (int i = 0; i < indexSize; ++i) {
            light[lightIndices[i]] = NOT_SET;
        }
        indexSize = 0;
        currentIndex = 0;
    }
}