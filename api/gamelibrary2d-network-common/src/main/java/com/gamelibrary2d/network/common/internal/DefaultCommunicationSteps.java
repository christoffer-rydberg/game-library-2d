package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.initialization.ConsumerStep;
import com.gamelibrary2d.network.common.initialization.ProducerStep;
import com.gamelibrary2d.network.common.initialization.StepCondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultCommunicationSteps implements CommunicationSteps {

    private final List<ConditionalCommunicationStep> steps = new ArrayList<>();

    @Override
    public void add(ConsumerStep step) {
        add(ConditionalCommunicationStep.TRUE, step);
    }

    @Override
    public void add(StepCondition condition, ConsumerStep step) {
        steps.add(new ConditionalCommunicationStep(step, condition));
    }

    @Override
    public void add(ProducerStep step) {
        add(ConditionalCommunicationStep.TRUE, step);
    }

    @Override
    public void add(StepCondition condition, ProducerStep step) {
        steps.add(new ConditionalCommunicationStep(step, condition));
    }

    public Collection<ConditionalCommunicationStep> getAll() {
        return steps;
    }
}