package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.internal.CommunicationStep;

import java.io.IOException;

/**
 * Defines a communication step that reads one or more messages.
 * You can also send messages from this step but if you only want to send,
 * without reading, you should use a {@link ProducerStep}.
 */
public interface ConsumerStep extends CommunicationStep {

    /**
     * Runs the communication step. This method is invoked only when data is
     * available in the inbox buffer. It will be invoked repeatedly until the step
     * is completed, which is indicated by the return value.
     *
     * @param context      Accumulated through each step.
     * @param communicator The communicator.
     * @param inbox        The inbox buffer.
     * @return True if the step has completed, false otherwise.
     */
    boolean run(CommunicationContext context, Communicator communicator, DataBuffer inbox) throws IOException;

}
