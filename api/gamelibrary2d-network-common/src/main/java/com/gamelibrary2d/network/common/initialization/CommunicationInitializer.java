package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.network.common.Communicator;

/**
 * Configures the initialization process of a {@link Communicator}.
 */
public interface CommunicationInitializer {

    /**
     * Adds a {@link ConsumerPhase} to the initialization process.
     */
    void add(ConsumerPhase phase);

    /**
     * Adds a {@link ProducerPhase} to the initialization process.
     */
    void add(ProducerPhase phase);

}
