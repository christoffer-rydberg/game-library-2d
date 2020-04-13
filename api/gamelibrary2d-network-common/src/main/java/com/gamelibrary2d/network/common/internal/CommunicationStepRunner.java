package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationStep;

import java.io.IOException;

public interface CommunicationStepRunner {

    /**
     * Runs a communication step.
     *
     * @param communicator The communicator.
     * @param step         The communication step.
     * @return True if the step runs to completion. False if the communicator must
     * await more data.
     */
    boolean run(CommunicationContext context, Communicator communicator, CommunicationStep step) throws IOException;

}