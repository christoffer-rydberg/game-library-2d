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
    public int size() {
        return disposables.size();
    }

    @Override
    public void register(Disposable disposable) {
        disposables.push(disposable);
    }

    @Override
    public void dispose() {
        isDisposed = true;
        while (!disposables.isEmpty()) {
            Disposable disposable = disposables.pop();
            disposable.dispose();
        }
        onDispose();
    }

    protected abstract void onDispose();
}