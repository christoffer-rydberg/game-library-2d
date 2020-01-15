package com.gamelibrary2d.common.event;

public interface EventPublisher<T> {

    boolean publish(T event);

    void addListener(EventListener<T> listener);

    void removeListener(EventListener<T> listener);

}