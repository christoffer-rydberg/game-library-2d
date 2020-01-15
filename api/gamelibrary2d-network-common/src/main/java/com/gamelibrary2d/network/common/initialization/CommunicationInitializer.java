package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.network.common.Communicator;

/**
 * Configures the initialization process of a {@link Communicator}.
 *
 * @author Christoffer Rydberg
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
