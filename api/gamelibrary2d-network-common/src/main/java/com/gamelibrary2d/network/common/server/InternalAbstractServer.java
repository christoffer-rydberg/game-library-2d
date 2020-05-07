package com.gamelibrary2d.network.common.server;

import com.gamelibrary2d.common.functional.Factory;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.Message;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedEvent;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.initialization.*;
import com.gamelibrary2d.network.common.internal.CommunicatorInitializer;
import com.gamelibrary2d.network.common.internal.CommunicatorInitializer.InitializationResult;
import com.gamelibrary2d.network.common.internal.ConditionalCommunicationStep;
import com.gamelibrary2d.network.common.initialization.ConsumerStep;
import com.gamelibrary2d.network.common.internal.DefaultCommunicationSteps;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class InternalAbstractServer implements Server {
    private final Factory<Integer> communicatorIdFactory = RandomInstance.get()::nextInt;
    private final List<PendingCommunicator> pendingCommunicators;
    private final DataBuffer outgoingBuffer;
    private final DataBuffer incomingBuffer;
    private final List<Communicator> communicators;
    private final DelayedMonitor delayedMonitor = new DelayedMonitor();
    private final CommunicatorDisconnectedListener disconnectedEventListener = this::onDisonnectedEvent;
    private final AtomicBoolean running = new AtomicBoolean();

    InternalAbstractServer() {
        incomingBuffer = new DynamicByteBuffer();
        incomingBuffer.flip();
        outgoingBuffer = new DynamicByteBuffer();
        communicators = new ArrayList<>();
        pendingCommunicators = new ArrayList<>();
    }

    void addConnectedCommunicator(Communicator communicator) {
        var steps = new DefaultCommunicationSteps();
        steps.add(new IdentityProducer(communicatorIdFactory));
        steps.add(this::connectedStep);
        communicator.configureAuthentication(steps);
        steps.add(this::authenticatedStep);
        configureClientInitialization(steps);

        pendingCommunicators.add(new PendingCommunicator(
                communicator,
                new DefaultCommunicationContext(),
                new CommunicatorInitializer(steps.getAll())));

        communicator.addDisconnectedListener(disconnectedEventListener);
    }

    private void connectedStep(CommunicationContext context, Communicator communicator) {
        onConnected(communicator);
    }

    private void authenticatedStep(CommunicationContext context, Communicator communicator) {
        communicator.onAuthenticated();
        onClientAuthenticated(context, communicator);
    }

    @Override
    public void deinitialize(Communicator communicator) {
        communicators.remove(communicator);
        var steps = new DefaultCommunicationSteps();
        try {
            configureClientInitialization(steps);
        } finally {
            pendingCommunicators.add(new PendingCommunicator(
                    communicator,
                    new DefaultCommunicationContext(),
                    new CommunicatorInitializer(steps.getAll())));
        }
    }

    private void initialized(CommunicationContext context, Communicator communicator) {
        removePending(communicator);
        communicators.add(communicator);
        communicator.onAuthenticated();
        onClientInitialized(context, communicator);
    }

    private void onDisonnectedEvent(CommunicatorDisconnectedEvent event) {
        var communicator = event.getCommunicator();
        invokeLater(() -> onDisconnected(communicator));
    }

    private PendingCommunicator removePending(Communicator communicator) {
        for (int i = 0; i < pendingCommunicators.size(); ++i) {
            var pending = pendingCommunicators.get(i);
            if (pending.communicator == communicator) {
                return pendingCommunicators.remove(i);
            }
        }

        return null;
    }

    private void onDisconnected(Communicator communicator) {
        if (communicators.remove(communicator)) {
            // Read final messages
            readAndHandleMessages(communicator);
            onDisconnected(communicator, false);
        } else {
            var pending = removePending(communicator);
            if (pending != null) {
                onDisconnected(communicator, true);
            } else {
                throw new IllegalStateException(String.format(
                        "Faulty state! Disconnected communicator '%s' is neither active nor pending. Endpoint: %s.",
                        communicator.getId(),
                        communicator.getEndpoint()));
            }
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
     * This method is triggered after reading incoming data, but before sending outgoing.
     *
     * @param deltaTime Time since the last update, in seconds.
     */
    protected abstract void onUpdate(float deltaTime);

    synchronized void invokeLater(Runnable runnable) {
        delayedMonitor.delay(runnable);
    }

    private void initializePending() {
        // Iterating backwards allows deletion of the current
        // element when connection has been established.
        for (int i = pendingCommunicators.size() - 1; i >= 0; --i) {
            var pending = pendingCommunicators.get(i);
            try {
                runCommunicationSteps(pending.communicator, pending.context, pending.initializer);
            } catch (IOException e) {
                pending.communicator.disconnect(e);
            }
        }
    }

    private void runCommunicationSteps(Communicator communicator, CommunicationContext context, CommunicatorInitializer initializer)
            throws IOException {
        InitializationResult result;
        do {
            result = initializer.runCommunicationStep(context, communicator, this::runCommunicationStep);
            if (result == InitializationResult.AWAITING_DATA && communicator.readIncoming(incomingBuffer)) {
                result = initializer.runCommunicationStep(context, communicator, this::runCommunicationStep);
            }
        } while (result == InitializationResult.PENDING);

        if (result == InitializationResult.FINISHED) {
            initialized(context, communicator);
            handleMesages(communicator);
        }
    }

    private boolean runCommunicationStep(CommunicationContext context, Communicator communicator,
                                         ConditionalCommunicationStep conditionalStep) throws IOException {
        if (!conditionalStep.condition.evaluate(context, communicator)) {
            return true;
        }

        var step = conditionalStep.step;
        if (step instanceof ConsumerStep) {
            while (true) {
                int remaining = incomingBuffer.remaining();
                if (remaining <= 0) {
                    return false;
                }

                var hasCompleted = ((ConsumerStep) step).run(context, communicator, incomingBuffer);
                if (hasCompleted) {
                    return true;
                }

                if (remaining == incomingBuffer.remaining()) {
                    throw new IOException("Unexpected message");
                }
            }
        } else if (step instanceof ProducerStep) {
            ((ProducerStep) step).run(context, communicator);
            return true;
        } else {
            throw new IOException("Unknown communication step");
        }
    }

    private void readIncoming() {
        for (int i = 0; i < communicators.size(); ++i) {
            var communicator = communicators.get(i);
            readAndHandleMessages(communicator);
        }
    }

    private void readAndHandleMessages(Communicator communicator) {
        if (communicator.readIncoming(incomingBuffer)) {
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
            sendMessages(pendingCommunicators.get(i).communicator);

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

    @Override
    public final void start() throws IOException {
        if (running.compareAndSet(false, true)) {
            onStart();
        }
    }

    @Override
    public final void stop() throws IOException, InterruptedException {
        if (running.compareAndSet(true, false)) {
            onStop();
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    protected abstract void onStart() throws IOException;

    protected abstract void onStop() throws IOException, InterruptedException;

    protected abstract void onConnected(Communicator communicator);

    protected abstract void configureClientInitialization(CommunicationSteps steps);

    protected abstract void onClientAuthenticated(CommunicationContext context, Communicator communicator);

    protected abstract void onClientInitialized(CommunicationContext context, Communicator communicator);

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

    private static class PendingCommunicator {
        final Communicator communicator;
        final CommunicationContext context;
        final CommunicatorInitializer initializer;

        PendingCommunicator(Communicator communicator, CommunicationContext context, CommunicatorInitializer initializer) {
            this.communicator = communicator;
            this.context = context;
            this.initializer = initializer;
        }
    }
}