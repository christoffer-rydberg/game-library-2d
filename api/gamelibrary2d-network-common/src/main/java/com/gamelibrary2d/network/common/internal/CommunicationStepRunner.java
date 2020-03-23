package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationStep;

public interface CommunicationStepRunner {

    /**
     * Runs a communication step.
     *
     * @param communicator The communicator.
     * @param step         The communication step.
     * @return True if the step runs to completion. False if the communicator must
     * await more data.
     * @throws InitializationException Occurs when initialization fails.
     */
    boolean run(Communicator communicator, CommunicationStep step) throws InitializationException;

}