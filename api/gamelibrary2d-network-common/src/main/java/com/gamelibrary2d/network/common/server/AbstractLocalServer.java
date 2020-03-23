package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.initialization.IdentityProducer;
import com.gamelibrary2d.network.common.internal.CommunicatorWrapper;
import com.gamelibrary2d.network.common.internal.InternalCommunicationSteps;

public abstract class AbstractLocalServer extends InternalAbstractServer implements LocalServer {

    private final Factory<Integer> communicatorIdFactory = () -> RandomInstance.get().nextInt();

    @Override
    public void addCommunicator(Communicator communicator) throws InitializationException {
        super.addCommunicator(new CommunicatorWrapper(communicator));
    }

    private boolean initialize(Communicator communicator, DataBuffer inbox) throws InitializationException {
        var reconnected = inbox.getBool();
        if (reconnected) {
            throw new InitializationException("Unexpected reconnect to local server");
        }

        onInitialize();

        var communicatorWrapper = (CommunicatorWrapper) communicator;
        var initializer = new InternalCommunicationSteps();
        configureClientAuthentication(initializer);
        onConfigureClientInitialization(initializer);
        communicatorWrapper.addCommunicationSteps(initializer.getAll());

        return true;
    }

    @Override
    protected final void configureClientInitialization(CommunicationSteps steps) {
        steps.add(this::initialize);
    }

    private void configureClientAuthentication(CommunicationSteps steps) throws InitializationException {
        steps.add(new IdentityProducer(communicatorIdFactory));
        onConfigureClientAuthentication(steps);
        steps.add(this::onAuthenticated);
    }

    private void onAuthenticated(Communicator communicator) {
        communicator.onAuthenticated();
        communicator.getOutgoing().putBool(false); // Instruct client to not reconnect after authentication.
    }

    protected abstract void onInitialize() throws InitializationException;

    protected abstract void onConfigureClientAuthentication(CommunicationSteps steps)
            throws InitializationException;

    protected abstract void onConfigureClientInitialization(CommunicationSteps steps)
            throws InitializationException;
}