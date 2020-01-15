package com.gamelibrary2d.common.concurrent;

public interface ExceptionHandler<T> {
    T handle(Throwable e) throws NotHandledException;
}
