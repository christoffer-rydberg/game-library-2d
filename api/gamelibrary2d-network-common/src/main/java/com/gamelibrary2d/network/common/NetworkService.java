package com.gamelibrary2d.network.common;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class NetworkService {

    private final Map<SelectableChannel, ConnectionListener> connectionListeners = new Hashtable<>();

    private final Map<SocketChannel, TcpConnection> tcpConnections = new Hashtable<>();

    private final Map<DatagramChannel, UdpConnection> udpConnections = new Hashtable<>();

    private final ByteBuffer readBuffer;

    private Selector selector;

    private Thread thread;

    public NetworkService() {
        this(8192);
    }

    public NetworkService(int readBufferSize) {
        readBuffer = ByteBuffer.allocateDirect(readBufferSize);
    }

    public boolean isRunning() {
        return thread != null && thread.isAlive();
    }

    public void start() throws IOException {
        if (!isRunning()) {
            selector = Selector.open();
            thread = new Thread(this::run);
            thread.start();
        }
    }

    public void stop() throws InterruptedException {
        if (isRunning()) {
            thread.interrupt();
            try {
                thread.join();
            } finally {
                thread = null;
            }
        }
    }

    public DatagramChannel openDatagramChannel(Communicator communicator, ConnectionOperations operations,
                                               ChannelDisconnectedHandler disconnectedHandler, int localPort, int hostPort) throws IOException {
        var udpConnection = new UdpConnection(communicator, operations, disconnectedHandler);
        DatagramChannel channel = DatagramChannel.open();
        channel.bind(new InetSocketAddress(localPort));
        channel.connect(new InetSocketAddress(communicator.getEndpoint(), hostPort));
        channel.configureBlocking(false);
        if (operations != ConnectionOperations.WRITE)
            channel.register(selector, SelectionKey.OP_READ, udpConnection);
        udpConnections.put(channel, udpConnection);
        return channel;
    }

    public ServerSocketChannelRegistration registerConnectionListener(
            String hostName, int port, SocketChannelConnectedHandler onConnected,
            SocketChannelFailedConnectionHandler onConnectionFailed)
            throws IOException {

        ServerSocketChannel socketChannel = null;
        try {
            socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.socket().bind(new InetSocketAddress(hostName, port));
            connectionListeners.put(socketChannel, new ConnectionListener(onConnected, onConnectionFailed));
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            selector.wakeup();
            return new ServerSocketChannelRegistration(socketChannel, socketChannel.socket().getLocalPort());
        } catch (IOException e) {
            close(socketChannel);
            throw e;
        }
    }

    public void deregisterConnectionListener(ServerSocketChannelRegistration registration) throws IOException {
        var channel = registration.getServerSocketChannel();
        connectionListeners.remove(registration.getServerSocketChannel());
        channel.close();
    }

    public void send(DatagramChannel channel, DataBuffer buffer) throws IOException {
        try {
            var udpConnection = udpConnections.get(channel);
            if (udpConnection == null) {
                throw new IOException("No connected UDP communicator");
            }
            var byteBuffer = udpConnection.getWriteBuffer();
            synchronized (byteBuffer) {
                var transmissionId = udpConnection.incrementTransmissionId();
                byteBuffer.putBool(true);
                byteBuffer.putInt(transmissionId);
                byteBuffer.putInt(buffer.remaining());
                byteBuffer.put(buffer);
                channel.register(selector, SelectionKey.OP_WRITE, udpConnection);
            }
        } catch (IOException e) {
            disconnect(channel, e);
            throw e;
        }

        selector.wakeup();
    }

    public void send(SocketChannel channel, DataBuffer buffer) throws IOException {
        try {
            var tcpConnection = tcpConnections.get(channel);
            if (tcpConnection == null) {
                throw new IOException("No connected TCP communicator");
            }
            var byteBuffer = tcpConnection.getWriteBuffer();
            synchronized (byteBuffer) {
                tcpConnection.incrementTransmissionId();
                byteBuffer.putBool(false);
                byteBuffer.putInt(buffer.remaining());
                byteBuffer.put(buffer);
                channel.register(selector, SelectionKey.OP_WRITE, tcpConnection);
            }
        } catch (IOException e) {
            disconnect(channel, e);
            throw e;
        }

        selector.wakeup();
    }

    public void connect(String hostname, int port, SocketChannelConnectedHandler onConnected,
                        SocketChannelFailedConnectionHandler onConnectionFailed) throws IOException {
        var channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(hostname, port));
        connectionListeners.put(channel, new ConnectionListener(onConnected, onConnectionFailed));
        channel.register(selector, SelectionKey.OP_CONNECT);
        selector.wakeup();
    }

    public void connect(SocketChannel socketChannel, Communicator communicator,
                        ChannelDisconnectedHandler disconnectedHandler) {
        try {
            var tcpConnection = new TcpConnection(communicator, disconnectedHandler);
            tcpConnections.put(socketChannel, tcpConnection);
            socketChannel.register(selector, SelectionKey.OP_READ, tcpConnection);
            selector.wakeup();
        } catch (IOException e) {
            disconnect(socketChannel, e);
        }
    }

    public void disconnect(SocketChannel socketChannel) {
        disconnect(socketChannel, null);
    }

    public void closeAfterLastScheduledSend(SocketChannel channel) {
        var tcpConnection = tcpConnections.get(channel);
        if (tcpConnection != null) {
            var byteBuffer = tcpConnection.getWriteBuffer();
            synchronized (byteBuffer) {
                tcpConnection.scheduleCloseConnection();
                if (tcpConnection.shouldClose()) {
                    disconnect(channel);
                }
            }
        }
    }

    public void disconnect(DatagramChannel datagramChannel) {
        disconnect(datagramChannel, null);
    }

    private void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid())
                        continue;

                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isWritable()) {
                        write(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isConnectable()) {
                        handleConnect(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = null;
        var connectionHandler = connectionListeners.get(serverSocketChannel);
        try {
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            connectionHandler.onConnected(socketChannel);
        } catch (IOException e) {
            var hostName = socketChannel.socket().getInetAddress().getCanonicalHostName();
            connectionHandler.onConnectionFailed(hostName, e);
            close(socketChannel);
        }
    }

    private void close(Closeable channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void write(SelectionKey key) {
        var connection = (AbstractConnection) key.attachment();
        if (connection instanceof TcpConnection) {
            writeTcp(key, (TcpConnection) connection);
        } else if (connection instanceof UdpConnection) {
            writeUdp(key, (UdpConnection) connection);
        }
    }

    private void writeTcp(SelectionKey key, TcpConnection tcpConnection) {
        var channel = (SocketChannel) key.channel();
        var byteBuffer = tcpConnection.getWriteBuffer();
        try {
            synchronized (byteBuffer) {
                byteBuffer.flip();
                channel.write(byteBuffer.internalByteBuffer());
                int remaining = byteBuffer.remaining();
                if (remaining > 0) {
                    int position = byteBuffer.position();
                    byteBuffer.clear();
                    byteBuffer.put(byteBuffer.array(), position, remaining);
                    key.interestOps(SelectionKey.OP_WRITE);
                } else {
                    byteBuffer.clear();
                    key.interestOps(SelectionKey.OP_READ);
                }

                tcpConnection.incrementProcessedTransmissions();
                if (tcpConnection.shouldClose()) {
                    disconnect(channel);
                }
            }
        } catch (IOException e) {
            disconnect(channel, e);
        }
    }

    private void writeUdp(SelectionKey key, UdpConnection udpConnection) {
        var channel = (DatagramChannel) key.channel();
        var byteBuffer = udpConnection.getWriteBuffer();
        try {
            synchronized (byteBuffer) {
                byteBuffer.flip();
                channel.write(byteBuffer.internalByteBuffer());
                int remaining = byteBuffer.remaining();
                if (remaining > 0) {
                    int position = byteBuffer.position();
                    byteBuffer.clear();
                    byteBuffer.put(byteBuffer.array(), position, remaining);
                    key.interestOps(SelectionKey.OP_WRITE);
                } else {
                    byteBuffer.clear();
                    if (udpConnection.getAllowedOperations() != ConnectionOperations.WRITE) {
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        } catch (IOException e) {
            disconnect(channel, e);
        }
    }

    private void read(SelectionKey key) {
        var connection = (AbstractConnection) key.attachment();
        if (connection instanceof TcpConnection) {
            readTcp(key, (TcpConnection) connection);
        } else if (connection instanceof UdpConnection) {
            readUdp(key, (UdpConnection) connection);
        }
    }

    private void readTcp(SelectionKey key, TcpConnection tcpConnection) {
        var channel = (SocketChannel) key.channel();
        try {
            tcpConnection.getCommunicator().addIncoming(tcpConnection.getChannel(), buffer -> {
                while (true) {
                    int bytesRead = channel.read(readBuffer);
                    if (bytesRead > 0) {
                        readBuffer.flip();
                        buffer.put(readBuffer);
                        readBuffer.clear();
                    } else if (bytesRead == 0) {
                        return;
                    } else {
                        disconnect(channel);
                        return;
                    }
                }
            });
        } catch (IOException e) {
            disconnect(channel, e);
        }
    }

    private void readUdp(SelectionKey key, UdpConnection udpConnection) {
        var channel = (DatagramChannel) key.channel();
        try {
            udpConnection.getCommunicator().addIncoming(udpConnection.getChannel(), buffer -> {
                while (true) {
                    int bytesRead = channel.read(readBuffer);
                    if (bytesRead > 0) {
                        readBuffer.flip();
                        buffer.put(readBuffer);
                        readBuffer.clear();
                    } else if (bytesRead == 0) {
                        return;
                    } else {
                        disconnect(channel);
                        return;
                    }
                }
            });
        } catch (IOException e) {
            disconnect(channel, e);
        }
    }

    private void handleConnect(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        var connectionHandler = connectionListeners.remove(channel);
        try {
            if (channel.isConnectionPending()) {
                channel.finishConnect();
            }
            channel.configureBlocking(false);
            connectionHandler.onConnected(channel);
        } catch (IOException e) {
            try {
                var hostName = channel.socket().getInetAddress().getCanonicalHostName();
                connectionHandler.onConnectionFailed(hostName, e);
                channel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void disconnect(SocketChannel socketChannel, IOException e) {
        var tcpConnection = tcpConnections.remove(socketChannel);
        try {
            socketChannel.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (tcpConnection != null) {
                tcpConnection.onDisconnected(e);
            }
        }
    }

    private void disconnect(DatagramChannel datagramChannel, IOException e) {
        var udpConnection = udpConnections.remove(datagramChannel);
        try {
            datagramChannel.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (udpConnection != null) {
                udpConnection.onDisconnected(e);
            }
        }
    }

    private void closeConnection() {
        if (selector != null) {
            close(selector);
            readBuffer.clear();

            for (var channel : connectionListeners.keySet()) {
                close(channel);
            }
            connectionListeners.clear();

            for (var socketChannel : tcpConnections.keySet()) {
                close(socketChannel);
            }
            tcpConnections.clear();

            for (var datagramChannel : udpConnections.keySet()) {
                close(datagramChannel);
            }
            udpConnections.clear();
        }
    }

    private static class ConnectionListener {
        private final SocketChannelConnectedHandler connectedHandler;
        private final SocketChannelFailedConnectionHandler connectionFailedHandler;

        ConnectionListener(SocketChannelConnectedHandler connectedHandler,
                           SocketChannelFailedConnectionHandler connectionFailedHandler) {
            this.connectedHandler = connectedHandler;
            this.connectionFailedHandler = connectionFailedHandler;
        }

        void onConnected(SocketChannel socketChannel) throws IOException {
            connectedHandler.onConnected(socketChannel);
        }

        void onConnectionFailed(String endpoint, IOException exception) {
            connectionFailedHandler.onConnectionFailed(endpoint, exception);
        }
    }

    private abstract static class AbstractConnection {
        private final Communicator communicator;
        private final ChannelDisconnectedHandler disconnectedHandler;
        private final DataBuffer writeBuffer = new DynamicByteBuffer();
        private final int channel;

        int prevTransmissionId;

        AbstractConnection(Communicator communicator, ChannelDisconnectedHandler disconnectedHandler,
                           int channel) {
            this.communicator = communicator;
            this.disconnectedHandler = disconnectedHandler;
            this.channel = channel;
        }

        int incrementTransmissionId() {
            return ++prevTransmissionId;
        }

        public void onDisconnected(IOException e) {
            disconnectedHandler.onDisconnected(e);
        }

        public Communicator getCommunicator() {
            return communicator;
        }

        DataBuffer getWriteBuffer() {
            return writeBuffer;
        }

        int getChannel() {
            return channel;
        }
    }

    private static class TcpConnection extends AbstractConnection {

        private int finalTransmission = -1;
        private int processedTransmissions;

        TcpConnection(Communicator communicator, ChannelDisconnectedHandler disconnectedHandler) {
            super(communicator, disconnectedHandler, 0);
        }

        void incrementProcessedTransmissions() {
            ++processedTransmissions;
        }

        void scheduleCloseConnection() {
            finalTransmission = prevTransmissionId;
        }

        boolean shouldClose() {
            return finalTransmission != -1 && processedTransmissions >= finalTransmission;
        }
    }

    private static class UdpConnection extends AbstractConnection {
        private final ConnectionOperations allowedOperations;

        UdpConnection(Communicator communicator, ConnectionOperations allowedOperations,
                      ChannelDisconnectedHandler disconnectedHandler) {
            super(communicator, disconnectedHandler, 1);
            this.allowedOperations = allowedOperations;
        }

        ConnectionOperations getAllowedOperations() {
            return allowedOperations;
        }
    }
}