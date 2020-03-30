package com.gamelibrary2d.common.disposal;

import java.util.ArrayDeque;
import java.util.Deque;

public class DefaultDisposer implements Disposable, Disposer {
    private final Disposer parentDisposer;
    private final Deque<Disposable> registeredResources = new ArrayDeque<>();

    public DefaultDisposer() {
        parentDisposer = null;
    }

    public DefaultDisposer(Disposer disposer) {
        parentDisposer = disposer;
        disposer.registerDisposal(this);
    }

    public int size() {
        return registeredResources.size();
    }

    @Override
    public void registerDisposal(Disposable disposable) {
        registeredResources.add(disposable);
    }

    @Override
    public void dispose() {
        dispose(0, registeredResources.size());
    }

    /**
     * Disposes all registered resources within the specified interval. Resources
     * are disposed in the same order as they were registered.
     *
     * @param off The first resource to dispose.
     * @param len The number of resources to dispose.
     */
    public void dispose(int off, int len) {
        final int last = off + len;
        final int size = registeredResources.size();
        for (int i = 0; i < size; ++i) {
            Disposable disposable = registeredResources.pop();
            if (i < off || i >= last)
                registeredResources.add(disposable);
            else
                disposable.dispose();
        }
    }

    /**
     * Clears registered resources without disposing.
     */
    public void clear() {
        registeredResources.clear();
    }

    public boolean hasParentDisposer() {
        return parentDisposer != null;
    }

    public Disposer getParentDisposer() {
        return parentDisposer;
    }
}