package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

/**
 * Defines a communication step that doesn't read messages.
 * Typically used to send messages. If you want to read messages,
 * use a {@link ConsumerStep}.
 */
public interface ProducerStep extends CommunicationStep {

    /**
     * Runs the communication step.
     *
     * @param communicator The communicator.
     */
    void run(Communicator communicator) throws InitializationException;

}