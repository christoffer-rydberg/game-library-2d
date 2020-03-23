package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.initialization.ConsumerStep;
import com.gamelibrary2d.network.common.initialization.CommunicationStep;
import com.gamelibrary2d.network.common.initialization.ProducerStep;

import java.util.ArrayList;
import java.util.List;

public class InternalCommunicationSteps implements CommunicationSteps {

    private final List<CommunicationStep> steps = new ArrayList<>();

    @Override
    public void add(ConsumerStep step) {
        steps.add(step);
    }

    @Override
    public void add(ProducerStep step) {
        steps.add(step);
    }

    public Iterable<CommunicationStep> getAll() {
        return steps;
    }
}