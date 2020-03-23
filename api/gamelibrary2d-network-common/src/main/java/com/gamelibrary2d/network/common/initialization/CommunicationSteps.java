package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.network.common.Communicator;

/**
 * Configures a communication pipeline for a {@link Communicator}.
 */
public interface CommunicationSteps {

    /**
     * Adds a {@link ConsumerStep} to the pipeline.
     */
    void add(ConsumerStep step);

    /**
     * Adds a {@link ProducerStep} to the pipeline.
     */
    void add(ProducerStep step);

}
