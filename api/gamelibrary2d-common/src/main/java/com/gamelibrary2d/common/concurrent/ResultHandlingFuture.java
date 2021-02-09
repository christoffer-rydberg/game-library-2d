package com.gamelibrary2d.common.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResultHandlingFuture<T1, T2> implements Future<T2> {

    private final Future<T1> future;
    private final CompletionHandler<T1, T2> onCompletion;
    private final ExceptionHandler<T2> onException;
    private boolean completed;
    private T2 result;

    public ResultHandlingFuture(Future<T1> future, CompletionHandler<T1, T2> onCompletion,
                                ExceptionHandler<T2> onException) {
        this.future = future;
        this.onCompletion = onCompletion;
        this.onException = onException;
    }

    public ResultHandlingFuture(Future<T1> future, CompletionHandler<T1, T2> onCompletion) {
        this.future = future;
        this.onCompletion = onCompletion;
        this.onException = e -> {
            throw new NotHandledException();
        };
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public T2 get() throws InterruptedException, ExecutionException {
        if (completed) {
            return result;
        }

        try {
            T1 res = future.get();
            result = onCompletion.handle(res);
            completed = true;
            return result;
        } catch (InterruptedException | ExecutionException e) {
            try {
                return onException.handle(e);
            } catch (NotHandledException nhe) {
                throw e;
            }
        }
    }

    @Override
    public T2 get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (completed) {
            return result;
        }

        try {
            T1 res = future.get(timeout, unit);
            result = onCompletion.handle(res);
            completed = true;
            return result;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            try {
                return onException.handle(e);
            } catch (NotHandledException nhe) {
                throw e;
            }
        }
    }
}