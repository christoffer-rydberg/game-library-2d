package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationStep;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public class CommunicatorInitializer {
    private final Deque<CommunicationStep> initializationSteps;

    public CommunicatorInitializer(Collection<CommunicationStep> initializationSteps) {
        this.initializationSteps = new ArrayDeque<>(initializationSteps);
    }

    public InitializationResult runCommunicationStep(CommunicationContext context, Communicator communicator, CommunicationStepRunner runner)
            throws InitializationException {

        if (initializationSteps.isEmpty()) {
            return InitializationResult.FINISHED;
        }

        var next = initializationSteps.peekFirst();
        if (runner.run(context, communicator, next)) {
            initializationSteps.pollFirst();
            return initializationSteps.isEmpty() ? InitializationResult.FINISHED : InitializationResult.PENDING;
        }
        return InitializationResult.AWAITING_DATA;
    }

    public enum InitializationResult {
        /**
         * All communication steps has finished.
         */
        FINISHED,

        /**
         * The current communication step requires more data.
         */
        AWAITING_DATA,

        /**
         * The next communication step is ready to run.
         */
        PENDING
    }
}