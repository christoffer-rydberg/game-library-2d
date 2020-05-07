package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.initialization.StepCondition;

public class ConditionalCommunicationStep {

    public static final StepCondition TRUE = (cc, c) -> true;

    public final CommunicationStep step;
    public final StepCondition condition;

    public ConditionalCommunicationStep(CommunicationStep step, StepCondition condition) {
        this.step = step;
        this.condition = condition;
    }
}
