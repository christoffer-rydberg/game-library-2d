package com.gamelibrary2d.common.concurrent;

import java.util.concurrent.ExecutionException;

public interface CompletionHandler<T, TD> {
    TD handle(T t) throws ExecutionException;
}