package com.gamelibrary2d.network.common;

import com.gamelibrary2d.common.event.DefaultEventPublisher;
import com.gamelibrary2d.common.event.EventPublisher;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedEvent;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedListener;
import com.gamelibrary2d.network.common.security.EncryptionReader;
import com.gamelibrary2d.network.common.security.EncryptionWriter;

import java.io.IOException;

public abstract class AbstractCommunicator implements Communicator {

    private final IncomingBufferMonitor[] incomingBufferMonitor;
    private final EventPublisher<CommunicatorDisconnectedEvent> disconnectedPublisher = new DefaultEventPublisher<>();

    private volatile int id;
    private volatile boolean connected = true;
    private volatile boolean authenticated;
    private volatile Throwable disconnectionCause;
    private volatile EncryptionWriter encryptionWriter;
    private volatile EncryptionReader encryptionReader;

    private DataBuffer outgoingBuffer;

    protected AbstractCommunicator(int incomingChannels) {
        incomingBufferMonitor = new IncomingBufferMonitor[incomingChannels];
        for (int i = 0; i < incomingChannels; ++i) {
            incomingBufferMonitor[i] = new IncomingBufferMonitor(new DynamicByteBuffer());
        }
        reallocateOutgoing();
    }

    @Override
    public EncryptionWriter getEncryptionWriter() {
        return encryptionWriter;
    }

    @Override
    public void setEncryptionWriter(EncryptionWriter encryptionWriter) {
        this.encryptionWriter = encryptionWriter;
    }

    @Override
    public EncryptionReader getEncryptionReader() {
        return encryptionReader;
    }

    @Override
    public void setEncryptionReader(EncryptionReader encryptionReader) {
        this.encryptionReader = encryptionReader;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public DataBuffer getOutgoing() {
        return outgoingBuffer;
    }

    @Override
    public void reallocateOutgoing() {
        outgoingBuffer = new DynamicByteBuffer();
    }

    @Override
    public void setAuthenticated() {
        authenticated = true;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void disconnect() {
        disconnect(null);
    }

    @Override
    public synchronized void addDisconnectedListener(CommunicatorDisconnectedListener listener) {
        if (connected) {
            disconnectedPublisher.addListener(listener);
        } else {
            listener.onEvent(new CommunicatorDisconnectedEvent(AbstractCommunicator.this, disconnectionCause));
        }
    }

    @Override
    public synchronized void removeDisconnectedListener(CommunicatorDisconnectedListener listener) {
        disconnectedPublisher.removeListener(listener);
    }

    @Override
    public synchronized void disconnect(Throwable cause) {
        if (connected) {
            connected = false;
            authenticated = false;
            disconnectionCause = cause;
            onDisconnected(cause);
            disconnectedPublisher.publish(new CommunicatorDisconnectedEvent(AbstractCommunicator.this, cause));
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void sendOutgoing() throws IOException {
        if (outgoingBuffer.position() == 0) {
            return;
        }

        int limit = outgoingBuffer.limit();
        int position = outgoingBuffer.position();
        try {
            outgoingBuffer.flip();
            send(outgoingBuffer);
            outgoingBuffer.clear();
        } catch (IOException e) {
            outgoingBuffer.limit(limit);
            outgoingBuffer.position(position);
            throw e;
        }
    }

    @Override
    public boolean readIncoming(DataBuffer buffer) {
        buffer.clear();
        for (int i = 0; i < incomingBufferMonitor.length; ++i)
            incomingBufferMonitor[i].readIncoming(buffer);
        buffer.flip();
        return buffer.remaining() > 0;
    }

    @Override
    public void addIncoming(int channel, DataReader dataReader) throws IOException {
        incomingBufferMonitor[channel].addIncoming(dataReader);
    }

    @Override
    public void sendUpdate(DataBuffer buffer) throws IOException {
        send(buffer);
    }

    protected abstract void send(DataBuffer buffer) throws IOException;

    protected abstract void onDisconnected(Throwable cause);

    protected static class IncomingBufferMonitor {

        final DataBuffer incomingBuffer;

        private int lastId = -1;

        IncomingBufferMonitor(DataBuffer incomingBuffer) {
            this.incomingBuffer = incomingBuffer;
        }

        synchronized void addIncoming(DataReader dataReader) throws IOException {
            dataReader.read(incomingBuffer);
        }

        synchronized void readIncoming(DataBuffer buffer) {
            writeIncomingToBuffer(buffer);
        }

        private void writeIncomingToBuffer(DataBuffer buffer) {
            incomingBuffer.flip();

            int remainingBytes = incomingBuffer.remaining();

            while (remainingBytes > 0) {
                if (remainingBytes < Integer.BYTES) {
                    buffer.put(incomingBuffer);
                    resetIncomingWithOverflow(buffer, remainingBytes);
                    return;
                } else {
                    boolean discardOldPackages = incomingBuffer.getBool();
                    int id = discardOldPackages ? incomingBuffer.getInt() : -1;
                    int messageBytes = incomingBuffer.getInt();
                    int limit = incomingBuffer.limit();
                    if (messageBytes > remainingBytes) {
                        incomingBuffer.position(incomingBuffer.position() - Integer.BYTES);
                        incomingBuffer.limit(incomingBuffer.position() + remainingBytes);
                        buffer.put(incomingBuffer);
                        resetIncomingWithOverflow(buffer, remainingBytes);
                        return;
                    } else {
                        incomingBuffer.limit(incomingBuffer.position() + messageBytes);
                        if (discardOldPackages) {
                            if (id > lastId) {
                                lastId = id;
                                buffer.put(incomingBuffer);
                            } else {
                                incomingBuffer.position(incomingBuffer.limit());
                            }
                        } else {
                            buffer.put(incomingBuffer);
                        }
                        incomingBuffer.limit(limit);
                        remainingBytes = incomingBuffer.remaining();
                    }
                }
            }
            incomingBuffer.clear();
        }

        private void resetIncomingWithOverflow(DataBuffer buffer, int overflow) {
            int pos = buffer.position();
            int limit = buffer.limit();
            buffer.limit(pos);
            buffer.position(pos - overflow);
            incomingBuffer.clear();
            incomingBuffer.put(buffer);
            buffer.limit(limit);
            buffer.position(pos - overflow);
        }
    }
}