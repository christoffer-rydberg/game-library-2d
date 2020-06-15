package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.Message;

import java.io.IOException;

/**
 * Configures a communication pipeline for a {@link Communicator}.
 */
public interface CommunicationSteps {

    /**
     * Adds a {@link ConsumerStep} to the pipeline.
     */
    void add(ConsumerStep step);

    /**
     * Adds a {@link ConsumerStep} to the pipeline if the given condition is met.
     */
    void add(StepCondition condition, ConsumerStep step);

    /**
     * Adds a {@link ProducerStep} to the pipeline.
     */
    void add(ProducerStep step);

    /**
     * Adds a {@link ProducerStep} to the pipeline if the given condition is met.
     */
    void add(StepCondition condition, ProducerStep step);

    /**
     * Adds a {@link ConsumerStep} to the pipeline that reads an object of the
     * specified generic type and registers it to the pipeline's {@link CommunicationContext}
     * with the specified key.
     */
    default <T> void read(Object key, ConsumerFunction<T> consumerStep) {
        add((context, communicator, buffer) -> {
            context.register(key, consumerStep.invoke(buffer));
            return true;
        });
    }

    /**
     * Adds a {@link ConsumerStep} to the pipeline that reads an object of the
     * specified generic type and registers it to the pipeline's {@link CommunicationContext}
     * with the specified key, if the given condition is met.
     */
    default <T> void read(Object key, StepCondition condition, ConsumerFunction<T> consumerStep) {
        add(condition, (context, communicator, buffer) -> {
            context.register(key, consumerStep.invoke(buffer));
            return true;
        });
    }

    /**
     * Adds a {@link ProducerStep} to the pipeline that produces an object of the
     * specified generic type and serializes it to the communicator.
     */
    default <T extends Message> void write(Factory<T> objFactory) {
        add((context, communicator) -> {
            objFactory.create().serializeMessage(communicator.getOutgoing());
        });
    }

    /**
     * Adds a {@link ProducerStep} to the pipeline that produces an object of the
     * specified generic type and serializes it to the communicator, if the given condition is met.
     */
    default <T extends Message> void write(StepCondition condition, Factory<T> objFactory) {
        add(condition, (context, communicator) -> {
            objFactory.create().serializeMessage(communicator.getOutgoing());
        });
    }

    /**
     * Adds a {@link ProducerStep} to the pipeline that produces an object of the
     * specified generic type and serializes it to the communicator.
     */
    default <T extends Message> void writeInstance(T obj) {
        add((context, communicator) -> {
            obj.serializeMessage(communicator.getOutgoing());
        });
    }

    /**
     * Adds a {@link ProducerStep} to the pipeline that produces an object of the
     * specified generic type and serializes it to the communicator, if the given condition is met.
     */
    default <T extends Message> void writeInstance(StepCondition condition, T obj) {
        add(condition, (context, communicator) -> {
            obj.serializeMessage(communicator.getOutgoing());
        });
    }

    interface ConsumerFunction<T> {
        T invoke(DataBuffer buffer) throws IOException;
    }
}
