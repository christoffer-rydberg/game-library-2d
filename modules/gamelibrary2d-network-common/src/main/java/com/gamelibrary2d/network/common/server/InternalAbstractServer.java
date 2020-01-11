package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.Message;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnected;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;
import com.gamelibrary2d.network.common.initialization.ConsumerPhase;
import com.gamelibrary2d.network.common.initialization.InitializationPhase;
import com.gamelibrary2d.network.common.initialization.ProducerPhase;
import com.gamelibrary2d.network.common.internal.CommunicatorWrapper;
import com.gamelibrary2d.network.common.internal.CommunicatorWrapper.InitializationResult;
import com.gamelibrary2d.network.common.internal.InternalCommunicatorInitializer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

abstract class InternalAbstractServer implements Server {

    final List<CommunicatorWrapper> pendingCommunicators;
    private final DataBuffer outgoingBuffer;
    private final DataBuffer incomingBuffer;
    private final List<Communicator> communicators;
    private final List<Communicator> iteratorList;
    private final DelayedMonitor delayedMonitor = new DelayedMonitor();
    final CommunicatorDisconnectedListener disconnectedEventListener = this::onDisonnectedEvent;

    InternalAbstractServer() {

        incomingBuffer = new DynamicByteBuffer();

        incomingBuffer.flip();

        outgoingBuffer = new DynamicByteBuffer();

        communicators = new ArrayList<>();

        pendingCommunicators = new ArrayList<>();

        iteratorList = new ArrayList<>();
    }

    void addCommunicator(CommunicatorWrapper communicator) throws InitializationException {
        if (!communicator.isConnected()) {
            throw new InitializationException("Communicator is not connected");
        }
        pendingCommunicators.add(communicator);
        communicator.addDisconnectedListener(disconnectedEventListener);
        configureInitialization(communicator);
    }

    protected void configureInitialization(CommunicatorWrapper communicator) {
        var initializer = new InternalCommunicatorInitializer();
        configureInitialization(initializer);
        communicator.addInitializationPhases(initializer.getInitializationPhases());
    }

    protected void configureInitialization(CommunicationInitializer initializer) {
        onConfigureInitialization(initializer);
    }

    private void initialized(Communicator communicator) {
        pendingCommunicators.remove(communicator);
        communicators.add(communicator);
        communicator.setAuthenticated();
        onInitialized(communicator);
    }

    private void onDisonnectedEvent(CommunicatorDisconnected event) {
        var communicator = event.getCommunicator();
        communicator.removeDisconnectedListener(disconnectedEventListener);
        delayedMonitor.delay(() -> onDisconnected(communicator));
    }

    private void onDisconnected(Communicator communicator) {
        if (communicators.remove(communicator)) {
            readAndHandleMessages(communicator);
            onDisconnected(communicator, false);
        } else if (pendingCommunicators.remove(communicator)) {
            try {
                runInitializationPhases((CommunicatorWrapper) communicator);
            } catch (InitializationException e) {
                e.printStackTrace();
            }
            onDisconnected(communicator, true);
        }
    }

    @Override
    public void update(float deltaTime) {
        readIncoming();
        initializePending();
        onUpdate(deltaTime);
        delayedMonitor.run();
        sendOutGoing();
    }

    /**
     * Override to handle updates. This method is triggered after reading incoming
     * data, but before sending outgoing.
     *
     * @param deltaTime Time since the last update, in seconds.
     */
    protected void onUpdate(float deltaTime) {

    }

    synchronized void invokeLater(Runnable runnable) {
        delayedMonitor.delay(runnable);
    }

    private void initializePending() {
        // Iterating backwards allows deletion of the current
        // element when connection has been established.
        for (int i = pendingCommunicators.size() - 1; i >= 0; --i) {
            var communicator = pendingCommunicators.get(i);
            try {
                runInitializationPhases(communicator);
            } catch (InitializationException e) {
                communicator.disconnect(e);
            }
        }
    }

    void runInitializationPhases(CommunicatorWrapper communicator) throws InitializationException {

        try {
            InitializationResult result;
            do {
                result = communicator.runInitializationPhase(this::runInitializationPhase);
                if (result == InitializationResult.AWAITING_DATA && communicator.readIncoming(incomingBuffer)) {
                    result = communicator.runInitializationPhase(this::runInitializationPhase);
                }
            } while (result == InitializationResult.PENDING);

            if (result == InitializationResult.FINISHED && pendingCommunicators.contains(communicator)) {
                initialized(communicator);
                handleMesages(communicator);
            }
        } catch (InitializationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InitializationException("Internal server error", e);
        }
    }

    private boolean runInitializationPhase(Communicator communicator, InitializationPhase phase)
            throws InitializationException {

        if (phase instanceof ConsumerPhase) {

            while (true) {

                int remaining = incomingBuffer.remaining();
                if (remaining <= 0)
                    return false;

                var hasCompleted = ((ConsumerPhase) phase).run(communicator, incomingBuffer);
                if (hasCompleted)
                    return true;

                if (remaining == incomingBuffer.remaining())
                    throw new InitializationException("Unexpected message");
            }

        } else if (phase instanceof ProducerPhase) {
            ((ProducerPhase) phase).run(communicator);
            return true;
        } else {
            throw new InitializationException("Unknown initialization phase");
        }
    }

    private void readIncoming() {
        iteratorList.addAll(communicators);
        for (int i = 0; i < iteratorList.size(); ++i) {
            var communicator = iteratorList.get(i);
            readAndHandleMessages(communicator);
        }
        iteratorList.clear();
    }

    private void readAndHandleMessages(Communicator communicator) {

        boolean hasIncoming;

        try {
            hasIncoming = communicator.readIncoming(incomingBuffer);
        } catch (IOException e) {
            communicator.disconnect(e);
            return;
        }

        if (hasIncoming) {
            handleMesages(communicator);
        }
    }

    private void handleMesages(Communicator communicator) {
        try {
            while (incomingBuffer.remaining() > 0) {
                onMessage(communicator, incomingBuffer);
            }
        } catch (Exception e) {
            communicator.disconnect(e);
        }
    }

    private void sendOutGoing() {
        sendIndividualMessages();
        sendOutgoingBufferToAll();
    }

    private void sendIndividualMessages() {

        for (int i = 0; i < pendingCommunicators.size(); ++i)
            sendMessages(pendingCommunicators.get(i));

        for (int i = 0; i < communicators.size(); ++i)
            sendMessages(communicators.get(i));
    }

    private void sendMessages(Communicator communicator) {
        try {
            communicator.sendOutgoing();
        } catch (IOException e) {
            communicator.disconnect(e);
        }
    }

    private void sendOutgoingBufferToAll() {
        if (outgoingBuffer.position() > 0) {
            outgoingBuffer.flip();
            sendToAll(communicators, outgoingBuffer);
            outgoingBuffer.clear();
        }
    }

    protected void sendToAll(Iterable<Communicator> communicators, DataBuffer outgoingBuffer) {
        for (var communicator : communicators) {
            try {
                outgoingBuffer.position(0);
                communicator.sendUpdate(outgoingBuffer);
            } catch (IOException e) {
                communicator.disconnect(e);
            }
        }
    }

    @Override
    public void sendToAll(int message, boolean stream) {
        if (stream) {
            outgoingBuffer.putInt(message);
        } else {
            for (int i = 0; i < communicators.size(); ++i) {
                send(communicators.get(i), message);
            }
        }
    }

    @Override
    public void sendToAll(float message, boolean stream) {
        if (stream) {
            outgoingBuffer.putFloat(message);
        } else {
            for (int i = 0; i < communicators.size(); ++i) {
                send(communicators.get(i), message);
            }
        }
    }

    @Override
    public void sendToAll(double message, boolean stream) {
        if (stream) {
            outgoingBuffer.putDouble(message);
        } else {
            for (int i = 0; i < communicators.size(); ++i) {
                send(communicators.get(i), message);
            }
        }
    }

    @Override
    public void sendToAll(byte message, boolean stream) {
        if (stream) {
            outgoingBuffer.put(message);
        } else {
            for (int i = 0; i < communicators.size(); ++i) {
                send(communicators.get(i), message);
            }
        }
    }

    @Override
    public void sendToAll(byte[] message, int off, int len, boolean stream) {
        if (stream) {
            outgoingBuffer.put(message, off, len);
        } else {
            for (int i = 0; i < communicators.size(); ++i) {
                send(communicators.get(i), message, off, len);
            }
        }
    }

    @Override
    public void sendToAll(Message message, boolean stream) {
        if (stream) {
            message.serializeMessage(outgoingBuffer);
        } else {
            for (int i = 0; i < communicators.size(); ++i) {
                send(communicators.get(i), message);
            }
        }
    }

    @Override
    public void send(Communicator communicator, int message) {
        communicator.getOutgoing().putInt(message);
    }

    @Override
    public void send(Communicator communicator, float message) {
        communicator.getOutgoing().putFloat(message);
    }

    @Override
    public void send(Communicator communicator, double message) {
        communicator.getOutgoing().putDouble(message);
    }

    @Override
    public void send(Communicator communicator, byte message) {
        communicator.getOutgoing().put(message);
    }

    @Override
    public void send(Communicator communicator, byte[] message, int off, int len) {
        communicator.getOutgoing().put(message, off, len);
    }

    @Override
    public void send(Communicator communicator, Message message) {
        message.serializeMessage(communicator.getOutgoing());
    }

    protected abstract void onConfigureInitialization(CommunicationInitializer initializer);

    protected abstract void onInitialized(Communicator communicator);

    protected abstract void onDisconnected(Communicator communicator, boolean pending);

    protected abstract void onMessage(Communicator communicator, DataBuffer buffer);

    private static class DelayedMonitor {
        private final Deque<Runnable> delayed = new ArrayDeque<>();

        public synchronized void run() {
            while (!delayed.isEmpty()) {
                delayed.poll().run();
            }
        }

        synchronized void delay(Runnable runnable) {
            delayed.addLast(runnable);
        }
    }
}