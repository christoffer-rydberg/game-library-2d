package com.gamelibrary2d.network.common.client;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.TcpConnector;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.initialization.IdentityConsumer;
import com.gamelibrary2d.network.common.initialization.IdentityProducer;
import com.gamelibrary2d.network.common.internal.InternalCommunicatorInitializer;

import java.util.concurrent.ExecutionException;

public abstract class AbstractSecureClient extends AbstractClient {

    public void authenticate() throws InitializationException {
        if (!communicator.isAuthenticated()) {
            var initializer = new InternalCommunicatorInitializer();
            configureAuthentication(initializer);
            communicator.clearInitializationPhases();
            communicator.addInitializationPhases(initializer.getInitializationPhases());
            runInitializationPhases();
        }
    }

    @Override
    protected void configureInitialization(CommunicationInitializer initializer) {
        onConfigureInitialization(initializer);
    }

    protected void configureAuthentication(CommunicationInitializer initializer) {
        initializer.add(x -> initializeConnection(x, false));
        initializer.add(new IdentityConsumer());
        onConfigureAuthentication(initializer);
        initializer.add(this::onAuthenticated);
    }

    private void initializeConnection(Communicator communicator, boolean isReconnect) {
        communicator.getOutgoing().putBool(isReconnect);
    }

    private boolean onAuthenticated(Communicator communicator, DataBuffer inbox) throws InitializationException {
        var reconnect = inbox.getBool();

        if (reconnect) {
            int port = inbox.getInt();
            var wrappedCommunicator = communicator.unwrap();
            if (wrappedCommunicator instanceof TcpConnector) {
                var tcpConnector = (TcpConnector) wrappedCommunicator;
                tcpConnector.setTcpConnectionSettings(
                        new TcpConnectionSettings(wrappedCommunicator.getEndpoint(), port, false));
            }
        }

        communicator.setAuthenticated();

        if (reconnect) {
            var wrappedCommunicator = communicator.unwrap();
            if (wrappedCommunicator instanceof Reconnectable) {
                try {
                    ((Reconnectable) wrappedCommunicator).reconnect().get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new InitializationException(e);
                }
            }
            initializeConnection(communicator, true);
            var identityProducer = new IdentityProducer(communicator::getId);
            identityProducer.run(communicator);
        }

        return true;
    }

    protected abstract void onConfigureAuthentication(CommunicationInitializer initializer);

    protected abstract void onConfigureInitialization(CommunicationInitializer initializer);

    protected abstract void onMessage(DataBuffer buffer);
}