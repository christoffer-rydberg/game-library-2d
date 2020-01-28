package com.gamelibrary2d.common.disposal;

public interface Disposer extends Disposable {
    void register(Disposable disposable);
}