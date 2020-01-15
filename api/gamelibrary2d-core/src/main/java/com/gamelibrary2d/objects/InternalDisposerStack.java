package com.gamelibrary2d.objects;

import com.gamelibrary2d.common.disposal.Disposable;

import java.util.ArrayDeque;
import java.util.Deque;

class InternalDisposerStack {

    private final static Disposable breakMark = () -> {
    };

    private final Deque<Disposable> stack = new ArrayDeque<>();

    public void push(Disposable disposable) {
        stack.addLast(disposable);
    }

    public void pushBreak() {
        stack.addLast(breakMark);
    }

    public void remove(Disposable disposable) {
        stack.removeLastOccurrence(disposable);
    }

    public void disposeUntilBreak() {
        while (!stack.isEmpty()) {
            Disposable e = stack.pollLast();
            if (e == breakMark) {
                stack.addLast(e);
                return;
            }
            e.dispose();
        }
    }

    public void dispose() {
        while (!stack.isEmpty()) {
            stack.pollLast().dispose();
        }
    }
}
