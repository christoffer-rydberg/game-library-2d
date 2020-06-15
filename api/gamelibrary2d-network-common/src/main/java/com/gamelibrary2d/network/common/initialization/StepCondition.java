package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.network.common.Communicator;

public interface StepCondition {
    boolean evaluate(CommunicationContext context, Communicator communicator);
}
