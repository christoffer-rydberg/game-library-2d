package com.gamelibrary2d.common;

import java.util.Arrays;

public class FloatArrayList {

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 10;

    private int size;
    private float[] data;

    public FloatArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public FloatArrayList(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: " +
                    initialCapacity);
        data = new float[size];
    }

    public FloatArrayList(float[] data) {
        setInternalArray(data, data.length);
    }

    public FloatArrayList(float[] data, int size) {
        setInternalArray(data, size);
    }

    /**
     * Returns the internal array used by this list.
     * Note that the array is not a copy. Changes to the array will affect the list,
     * unless the internal array has been replaced due to a resize of the list.
     */
    public float[] internalArray() {
        return data;
    }

    public void setInternalArray(float[] data, int size) {
        this.data = data;
        this.size = size;
    }

    public float get(int index) {
        rangeCheck(index);
        return data[index];
    }

    public void set(int index, float element) {
        rangeCheck(index);
        data[index] = element;
    }

    public void add(float element) {
        if (size == data.length) {
            resize();
        }
        data[size] = element;
        ++size;
    }

    public float remove(int index) {
        rangeCheck(index);

        var value = data[index];

        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }

        return value;
    }

    public void add(int index, float element) {
        rangeCheck(index);
        ensureCapacity(size + 1);
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        ++size;
    }

    public int size() {
        return size;
    }

    public void trimToSize() {
        if (size < data.length) {
            data = Arrays.copyOf(data, size);
        }
    }

    private void resize() {
        ensureCapacity(size + 1);
    }

    public void ensureCapacity(int minCapacity) {
        int oldCapacity = data.length;
        if (oldCapacity < minCapacity) {
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            data = Arrays.copyOf(data, newCapacity);
        }
    }

    private void rangeCheck(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    public void addAll(float[] values) {
        addAll(0, values, 0, values.length);
    }

    public void addAll(float[] values, int offset, int len) {
        addAll(0, values, offset, len);
    }

    public void addAll(int index, float[] values) {
        addAll(index, values, 0, values.length);
    }

    public void addAll(int index, float[] values, int offset, int len) {
        ensureCapacity(size + len);
        System.arraycopy(values, offset, data, index, len);
    }
}