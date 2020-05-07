package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;

import java.io.IOException;

public interface CommunicationStepRunner {

    /**
     * Runs a conditional communication step.
     *
     * @param communicator    The communicator.
     * @param conditionalStep The communication step.
     * @return True if the step runs to completion. False if the communicator must
     * await more data.
     */
    boolean run(CommunicationContext context, Communicator communicator, ConditionalCommunicationStep conditionalStep) throws IOException;

}