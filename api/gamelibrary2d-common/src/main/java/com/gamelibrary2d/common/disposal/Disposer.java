package com.gamelibrary2d.common.disposal;

public interface Disposer extends Disposable {
    int size();

    void register(Disposable disposable);
}