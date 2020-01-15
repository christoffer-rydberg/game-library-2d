package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.initialization.ConsumerPhase;
import com.gamelibrary2d.network.common.initialization.InitializationPhase;
import com.gamelibrary2d.network.common.initialization.ProducerPhase;

import java.util.ArrayList;
import java.util.List;

public class InternalCommunicatorInitializer implements CommunicationInitializer {

    private final List<InitializationPhase> phases = new ArrayList<>();

    @Override
    public void add(ConsumerPhase phase) {
        phases.add(phase);
    }

    @Override
    public void add(ProducerPhase phase) {
        phases.add(phase);
    }

    public Iterable<InitializationPhase> getInitializationPhases() {
        return phases;
    }
}