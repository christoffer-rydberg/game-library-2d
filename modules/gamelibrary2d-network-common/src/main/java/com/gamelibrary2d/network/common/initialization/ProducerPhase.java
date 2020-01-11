package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

/**
 * Defines a phase that produces messages or changes in the initialization
 * pipeline.
 *
 * @author Christoffer Rydberg
 */
public interface ProducerPhase extends InitializationPhase {

    /**
     * Runs the initialization phase.
     *
     * @param communicator The communicator.
     */
    void run(Communicator communicator) throws InitializationException;

}