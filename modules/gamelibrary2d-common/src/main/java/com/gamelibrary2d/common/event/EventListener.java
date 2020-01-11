package com.gamelibrary2d.common.event;

public interface EventListener<T> {
    void onEvent(T event);
}