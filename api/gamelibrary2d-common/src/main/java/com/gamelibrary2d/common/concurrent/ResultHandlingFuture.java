package com.gamelibrary2d.common.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResultHandlingFuture<T, TD> implements Future<TD> {

    private final Future<T> future;
    private final CompletionHandler<T, TD> onCompletion;
    private final ExceptionHandler<TD> onException;
    private boolean completed;
    private TD result;

    public ResultHandlingFuture(Future<T> future, CompletionHandler<T, TD> onCompletion,
                                ExceptionHandler<TD> onException) {
        this.future = future;
        this.onCompletion = onCompletion;
        this.onException = onException;
    }

    public ResultHandlingFuture(Future<T> future, CompletionHandler<T, TD> onCompletion) {
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
    public TD get() throws InterruptedException, ExecutionException {
        if (completed) {
            return result;
        }

        try {
            var res = future.get();
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
    public TD get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (completed) {
            return result;
        }

        try {
            var res = future.get(timeout, unit);
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