package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.common.event.DefaultEventPublisher;
import com.gamelibrary2d.common.event.EventPublisher;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.DataReader;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnected;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.InitializationPhase;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class CommunicatorWrapper implements Communicator {

    private final Deque<InitializationPhase> initializationPhases = new ArrayDeque<>();
    private final EventPublisher<CommunicatorDisconnected> disconnectedPublisher = new DefaultEventPublisher<>();
    private final CommunicatorDisconnectedListener internalDisconnectedListener = this::onDisconnectedEvent;
    private Communicator wrappedCommunicator;
    private boolean initializationRunning;

    public CommunicatorWrapper() {

    }

    public CommunicatorWrapper(Communicator wrappedCommunicator) {
        setWrappedCommunicator(wrappedCommunicator);
    }

    @Override
    public String getEndpoint() {
        return wrappedCommunicator.getEndpoint();
    }

    @Override
    public int getId() {
        return wrappedCommunicator.getId();
    }

    @Override
    public void setId(int id) {
        wrappedCommunicator.setId(id);
    }

    @Override
    public boolean isConnected() {
        return wrappedCommunicator.isConnected();
    }

    @Override
    public void disconnect() {
        wrappedCommunicator.disconnect();
    }

    @Override
    public void disconnect(Throwable cause) {
        wrappedCommunicator.disconnect(cause);
    }

    @Override
    public void addIncoming(DataReader dataReader, int channel) throws IOException {
        wrappedCommunicator.addIncoming(dataReader, channel);
    }

    @Override
    public boolean readIncoming(DataBuffer outputBuffer) throws IOException {
        return wrappedCommunicator.readIncoming(outputBuffer);
    }

    @Override
    public DataBuffer getOutgoing() {
        return wrappedCommunicator.getOutgoing();
    }

    @Override
    public void sendOutgoing() throws IOException {
        wrappedCommunicator.sendOutgoing();
    }

    @Override
    public void sendUpdate(DataBuffer updateBuffer) throws IOException {
        wrappedCommunicator.sendUpdate(updateBuffer);
    }

    @Override
    public void addDisconnectedListener(CommunicatorDisconnectedListener listener) {
        disconnectedPublisher.addListener(listener);
    }

    @Override
    public void removeDisconnectedListener(CommunicatorDisconnectedListener listener) {
        disconnectedPublisher.removeListener(listener);
    }

    @Override
    public void setAuthenticated() {
        wrappedCommunicator.setAuthenticated();
    }

    @Override
    public boolean isAuthenticated() {
        return wrappedCommunicator.isAuthenticated();
    }

    @Override
    public Communicator unwrap() {
        return wrappedCommunicator.unwrap();
    }

    public Communicator getWrappedCommunicator() {
        return wrappedCommunicator;
    }

    public void setWrappedCommunicator(Communicator wrappedCommunicator) {

        if (this.wrappedCommunicator != null)
            this.wrappedCommunicator.removeDisconnectedListener(internalDisconnectedListener);

        this.wrappedCommunicator = wrappedCommunicator;

        if (wrappedCommunicator != null)
            wrappedCommunicator.addDisconnectedListener(internalDisconnectedListener);
    }

    private void onDisconnectedEvent(CommunicatorDisconnected event) {
        disconnectedPublisher.publish(new CommunicatorDisconnected(this, event.getCause()));
    }

    public void clearInitializationPhases() throws InitializationException {

        if (initializationRunning)
            throw new InitializationException("Cannot clear initialization phases during initialization");

        initializationPhases.clear();
    }

    public void addInitializationPhases(Iterable<InitializationPhase> phases) {
        for (var phase : phases)
            initializationPhases.addLast(phase);
    }

    public InitializationResult runInitializationPhase(InitializationPhaseRunner runner)
            throws InitializationException {

        if (initializationRunning)
            throw new InitializationException("An initialization phase is already running");

        if (initializationPhases.isEmpty()) {
            return InitializationResult.FINISHED;
        }

        initializationRunning = true;

        try {
            var next = initializationPhases.peekFirst();
            if (runner.run(this, next)) {
                initializationPhases.pollFirst();
                return initializationPhases.isEmpty() ? InitializationResult.FINISHED : InitializationResult.PENDING;
            }
            return InitializationResult.AWAITING_DATA;
        } finally {
            initializationRunning = false;
        }
    }

    public enum InitializationResult {

        /**
         * All initialization phases has finished.
         */
        FINISHED,

        /**
         * The current initialization phase requires more data.
         */
        AWAITING_DATA,

        /**
         * The next communication phase is ready to run.
         */
        PENDING
    }

}