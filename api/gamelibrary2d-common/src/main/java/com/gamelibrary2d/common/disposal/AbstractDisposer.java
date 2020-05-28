package com.gamelibrary2d.common.disposal;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstractDisposer implements Disposer {

    private final Deque<Disposable> disposables = new ArrayDeque<>();

    private boolean isDisposed = false;

    public boolean isDisposed() {
        return isDisposed;
    }

    @Override
    public void registerDisposal(Disposable disposable) {
        if (isDisposed) {
            throw new IllegalStateException("Disposer has been disposed");
        }
        disposables.push(disposable);
    }

    @Override
    public void dispose() {
        if (!isDisposed) {
            isDisposed = true;
            while (!disposables.isEmpty()) {
                Disposable disposable = disposables.pop();
                disposable.dispose();
            }
            onDispose();
        }
    }

    protected abstract void onDispose();
}