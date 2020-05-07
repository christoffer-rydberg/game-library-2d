package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.NetworkCommunicator;

public interface StepCondition {

    StepCondition IS_NETWORK_COMMUNICATOR = (cc, c) -> c instanceof NetworkCommunicator;

    boolean evaluate(CommunicationContext context, Communicator communicator);

}
