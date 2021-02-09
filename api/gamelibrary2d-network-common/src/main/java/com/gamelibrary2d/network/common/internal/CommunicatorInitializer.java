package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public class CommunicatorInitializer {
    private final Deque<ConditionalCommunicationStep> initializationSteps;

    public CommunicatorInitializer(Collection<ConditionalCommunicationStep> initializationSteps) {
        this.initializationSteps = new ArrayDeque<>(initializationSteps);
    }

    public InitializationResult runCommunicationStep(CommunicationContext context, Communicator communicator, CommunicationStepRunner runner)
            throws IOException {

        if (initializationSteps.isEmpty()) {
            return InitializationResult.FINISHED;
        }

        ConditionalCommunicationStep next = initializationSteps.peekFirst();
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