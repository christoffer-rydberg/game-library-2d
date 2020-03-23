package com.gamelibrary2d.network.common.internal;

import com.gamelibrary2d.common.event.DefaultEventPublisher;
import com.gamelibrary2d.common.event.EventPublisher;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.DataReader;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnected;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationStep;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class CommunicatorWrapper implements Communicator {
    private final Deque<CommunicationStep> communicationSteps = new ArrayDeque<>();
    private final EventPublisher<CommunicatorDisconnected> disconnectedPublisher = new DefaultEventPublisher<>();
    private final CommunicatorDisconnectedListener internalDisconnectedListener = this::onDisconnectedEvent;
    private Communicator wrappedCommunicator;
    private boolean communicationStepIsRunning;

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
    public void onAuthenticated() {
        wrappedCommunicator.onAuthenticated();
    }

    @Override
    public boolean isAuthenticated() {
        return wrappedCommunicator.isAuthenticated();
    }

    @Override
    public Communicator unwrap() {
        return wrappedCommunicator.unwrap();
    }

    @Override
    public void configureAuthentication(CommunicationSteps steps) {
        wrappedCommunicator.configureAuthentication(steps);
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

    public void clearCommunicationSteps() throws InitializationException {
        if (communicationStepIsRunning)
            throw new InitializationException("Cannot clear communication steps while a step is running");

        communicationSteps.clear();
    }

    public void addCommunicationSteps(Iterable<CommunicationStep> steps) {
        for (var step : steps) {
            communicationSteps.addLast(step);
        }
    }

    public InitializationResult runCommunicationStep(CommunicationStepRunner runner)
            throws InitializationException {

        if (communicationStepIsRunning)
            throw new InitializationException("An communication step is already running");

        if (communicationSteps.isEmpty()) {
            return InitializationResult.FINISHED;
        }

        communicationStepIsRunning = true;

        try {
            var next = communicationSteps.peekFirst();
            if (runner.run(this, next)) {
                communicationSteps.pollFirst();
                return communicationSteps.isEmpty() ? InitializationResult.FINISHED : InitializationResult.PENDING;
            }
            return InitializationResult.AWAITING_DATA;
        } finally {
            communicationStepIsRunning = false;
        }
    }

    public enum InitializationResult {

        /**
         * All communication steps has finished.
         */
        FINISHED,

        /**
         * The current communication step requires more data.
         */
        AWAITING_DATA,

        /**
         * The next communication step is ready to run.
         */
        PENDING
    }

}