package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.InitializationPhase;

public interface InitializationPhaseRunner {

    /**
     * Runs an initialization phase.
     *
     * @param communicator The communicator.
     * @param phase        The initialization phase.
     * @return True if the phase runs to completion. False if the communicator must
     * await more data.
     * @throws InitializationException Occurs when initialization fails.
     */
    boolean run(Communicator communicator, InitializationPhase phase) throws InitializationException;

}