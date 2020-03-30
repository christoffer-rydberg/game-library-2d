package com.gamelibrary2d.common.disposal;

import com.gamelibrary2d.common.functional.Func;

/**
 * Manages a resource and its disposal.
 *
 * @param <T>
 */
public class ResourceContainer<T> {
    private T resource;
    private Func<Disposer, T> resourceFactory;
    private DefaultDisposer resourceDisposer;

    /**
     * Creates an empty instance of {@link ResourceContainer}.
     * Invoke {@link #createResource} in order to create a resource.
     */
    public ResourceContainer() {
    }

    /**
     * Creates a {@link #getResource() resource} using the specified resource factory.
     * If the container already has a resource, it will be disposed.
     *
     * @param resourceFactory The resource factory.
     * @param disposer        The disposer.
     */
    public void createResource(Func<Disposer, T> resourceFactory, Disposer disposer) {
        createResource(resourceFactory, false, disposer);
    }

    /**
     * Creates a {@link #getResource() resource} using the specified resource factory.
     * If the container already has a resource, it will be disposed.
     *
     * @param resourceFactory The resource factory.
     * @param lazy            If true, the resource won't be created until it is requested.
     * @param disposer        The disposer.
     */
    public void createResource(Func<Disposer, T> resourceFactory, boolean lazy, Disposer disposer) {
        dispose();
        if (resourceDisposer == null || disposer != resourceDisposer.getParentDisposer()) {
            resourceDisposer = new DefaultDisposer(disposer);
        }
        if (lazy) {
            this.resourceFactory = resourceFactory;
        } else {
            this.resource = resourceFactory.invoke(resourceDisposer);
        }
    }

    /**
     * @return True if the container holds a resource, false otherwise.
     */
    public boolean hasResource() {
        return resource != null || resourceFactory != null;
    }

    /**
     * @return The container's resource.
     */
    public T getResource() {
        if (resourceFactory != null) {
            resource = resourceFactory.invoke(resourceDisposer);
            resourceFactory = null;
        }
        return resource;
    }

    /**
     * Disposes the container's {@link #getResource resource}.
     */
    public void dispose() {
        if (hasResource()) {
            resourceDisposer.dispose();
            resource = null;
            resourceFactory = null;
        }
    }
}
