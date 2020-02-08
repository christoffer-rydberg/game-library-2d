package com.gamelibrary2d.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Pool to store and reuse objects in order to avoid unnecessary allocations and garbage collection.
 *
 * @param <T> The generic type of the objects in the pool.
 */
public class Pool<T> {

    private final List<T> stored;

    private int pointer;

    public Pool() {
        stored = new ArrayList<>();
    }

    public Pool(int size) {
        stored = new ArrayList<T>(size);
    }

    /**
     * @return True if there are available objects in the pool, false otherwise.
     */
    public boolean canGet() {
        return pointer < stored.size();
    }

    /**
     * Call this method to get the pool pointer before storing/getting objects, and
     * use the pointer when calling reset to make these object available again in
     * the pool.
     *
     * @return The pointer to the current location in the pool.
     */
    public int getPointer() {
        return pointer;
    }

    /**
     * Stores an object in the pool.
     *
     * @param obj The object to store.
     * @return A reference to the stored object.
     */
    public T store(T obj) {
        stored.add(obj);
        ++pointer;
        return obj;
    }

    /**
     * @return An object from the pool.
     */
    public T get() {
        T obj = stored.get(pointer);
        ++pointer;
        return obj;
    }

    /**
     * Resets the pool, i.e. makes all objects after the pointer available to get.
     *
     * @param pointer Points at the first available object in the pool.
     */
    public void reset(int pointer) {
        this.pointer = pointer;
    }

    /**
     * Clears the pool, i.e. releases all objects so that they can be garbage
     * collected.
     */
    public void clear() {
        reset(0);
        stored.clear();
    }
}