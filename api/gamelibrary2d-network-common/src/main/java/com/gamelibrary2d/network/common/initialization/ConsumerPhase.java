package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

/**
 * Defines a phase that consumes messages during the initialization pipeline.
 */
public interface ConsumerPhase extends InitializationPhase {

    /**
     * Runs the initialization phase. This method is invoked only when data is
     * available in the inbox buffer. It will be invoked repeatedly until the phase
     * has completed, which is dictated by the return value.
     *
     * @param communicator The communicator.
     * @param inbox        The inbox buffer.
     * @return True if the phase has completed, false otherwise.
     * @throws InitializationException Occurs if the initialization phase fails.
     */
    boolean run(Communicator communicator, DataBuffer inbox) throws InitializationException;

}
