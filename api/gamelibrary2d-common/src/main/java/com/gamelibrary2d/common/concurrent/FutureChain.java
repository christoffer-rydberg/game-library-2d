package com.gamelibrary2d.common.concurrent;

import com.gamelibrary2d.common.functional.Func;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureChain<T> {
    private final ArrayDeque<Func<T, Future<T>>> chain;
    private Future<T> current;

    public FutureChain(Future<T> current) {
        this.current = current;
        this.chain = new ArrayDeque<>();
    }

    public FutureChain(Future<T> current, ArrayDeque<Func<T, Future<T>>> chain) {
        this.current = current;
        this.chain = chain;
    }

    public void addToChain(Func<T, Future<T>> func) {
        chain.addLast(func);
    }

    public boolean hasNext() {
        return !chain.isEmpty();
    }

    public void moveNext() throws InterruptedException, ExecutionException {
        var result = current.get();
        var next = chain.pollFirst();
        current = next.invoke(result);
    }

    public void moveNext(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        var result = current.get(timeout, unit);
        var next = chain.pollFirst();
        current = next.invoke(result);
    }

    public boolean isCurrentDone() {
        return current.isDone();
    }

    public boolean complete() throws InterruptedException, ExecutionException {
        if (isCurrentDone()) {
            if (hasNext()) {
                moveNext();
            } else {
                getCurrentResult();
                return true;
            }
        }
        return false;
    }

    public T getCurrentResult() throws InterruptedException, ExecutionException {
        return current.get();
    }

    public T getCurrentResult(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return current.get(timeout, unit);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return current.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return current.isCancelled();
    }
}