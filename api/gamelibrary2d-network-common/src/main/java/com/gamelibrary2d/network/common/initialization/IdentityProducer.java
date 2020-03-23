package com.gamelibrary2d.network.common.initialization;

import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.network.common.Communicator;

public class IdentityProducer implements ProducerStep {

    private final Factory<Integer> idFactory;

    public IdentityProducer(Factory<Integer> idFactory) {
        this.idFactory = idFactory;
    }

    @Override
    public void run(Communicator communicator) {
        int id = idFactory.create();
        communicator.setId(id);
        communicator.getOutgoing().putInt(id);
    }
}