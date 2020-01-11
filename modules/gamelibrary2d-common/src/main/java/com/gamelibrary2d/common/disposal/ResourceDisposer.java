package com.gamelibrary2d.common.disposal;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <p>
 * Disposable resources are typically registered to a parent disposer, e.g. a
 * frame. When the parent is disposed, so are all registered resources. Sometimes,
 * however, resources have a shorter life span than the parent.
 * </p>
 *
 * <p>
 * This class acts as a container for these kind of short-lived resources.
 * The class itself is registered to the parent disposer, to ensure that the
 * resources are disposed when the parent is. Additionally, it offers the
 * possibility to dispose the resources at any time.
 * </p>
 *
 * <p>
 * You can dispose resources without this class, however, disposing a resource
 * does not remove it from the parent disposer's internal list. This means that
 * the list will keep growing as you dispose old and create new objects. Another
 * benefit of this class is that different instances can be used to group
 * related resources. When resources are no longer needed, simply dispose
 * the whole group. It is also possible to dispose a subset of the group.
 * </p>
 */
public class ResourceDisposer implements Disposable, Disposer {

    private final Disposer parentDisposer;
    private final Deque<Disposable> registeredResources = new ArrayDeque<>();

    /**
     * All registered disposable resources will be disposed when the specified
     * disposer is.
     *
     * @param disposer The parent disposer.
     */
    public ResourceDisposer(Disposer disposer) {
        parentDisposer = disposer;
        disposer.register(this);
    }

    /**
     * Gets the size, i.e. the number of registered disposable resources.
     */
    @Override
    public int size() {
        return registeredResources.size();
    }

    /**
     * Registers the specified disposable resource.
     */
    @Override
    public void register(Disposable disposable) {
        registeredResources.add(disposable);
    }

    /**
     * Disposes all registered resources.
     */
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

    public Disposer getParentDisposer() {
        return parentDisposer;
    }
}