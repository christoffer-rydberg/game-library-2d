package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.initialization.IdentityConsumer;
import com.gamelibrary2d.network.common.initialization.IdentityProducer;
import com.gamelibrary2d.network.common.internal.CommunicatorWrapper;
import com.gamelibrary2d.network.common.internal.InternalCommunicatorInitializer;

public abstract class AbstractSecureLocalServer extends InternalAbstractServer implements LocalServer {

    private final Factory<Integer> communicatorIdFactory = () -> RandomInstance.get().nextInt();

    @Override
    public void addCommunicator(Communicator communicator) throws InitializationException {
        super.addCommunicator(new CommunicatorWrapper(communicator));
    }

    @Override
    protected void configureInitialization(CommunicationInitializer initializer) {
        initializer.add(this::initializeConnection);
    }

    private void configureAuthentication(CommunicationInitializer initializer) throws InitializationException {
        initializer.add(new IdentityProducer(communicatorIdFactory));
        onConfigureAuthentication(initializer);
        initializer.add(this::onAuthenticated);
    }

    private boolean initializeConnection(Communicator communicator, DataBuffer inbox) throws InitializationException {
        var reconnected = inbox.getBool();
        if (reconnected) {
            new IdentityConsumer().run(communicator, inbox);
        }
        onConnected(communicator);
        return true;
    }

    private void onConnected(Communicator communicator) throws InitializationException {
        var communicatorWrapper = (CommunicatorWrapper) communicator;
        var initializer = new InternalCommunicatorInitializer();
        configureAuthentication(initializer);
        onConfigureInitialization(initializer);
        communicatorWrapper.addInitializationPhases(initializer.getInitializationPhases());
    }

    protected void onAuthenticated(Communicator communicator) throws InitializationException {
        communicator.setAuthenticated();
        communicator.getOutgoing().putBool(false);
    }

    protected abstract void onConfigureAuthentication(CommunicationInitializer initializer)
            throws InitializationException;

}