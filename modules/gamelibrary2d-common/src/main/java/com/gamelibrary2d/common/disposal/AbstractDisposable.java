package com.gamelibrary2d.common.disposal;

public abstract class AbstractDisposable implements Disposable {

    private boolean isDisposed = false;

    @Override
    public void dispose() {
        onDispose();
        isDisposed = true;
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    protected abstract void onDispose();
}
