package com.gamelibrary2d.common.event;

public class SynchronizedEventPublisher<T> extends AbstractEventPublisher<T> {

    public SynchronizedEventPublisher() {

    }

    public SynchronizedEventPublisher(int size) {
        super(size);
    }

    @Override
    public synchronized void publish(T event) {
        super.publish(event);
    }

    @Override
    public synchronized void addListener(EventListener<T> listener) {
        super.addListener(listener);
    }

    @Override
    public synchronized void removeListener(EventListener<T> listener) {
        super.removeListener(listener);
    }

}