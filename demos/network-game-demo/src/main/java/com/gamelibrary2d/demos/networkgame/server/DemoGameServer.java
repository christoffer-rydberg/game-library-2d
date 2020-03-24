package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.common.io.BitParser;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.common.updating.UpdateLoop;
import com.gamelibrary2d.demos.networkgame.common.NetworkConstants;
import com.gamelibrary2d.demos.networkgame.common.ServerMessages;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.server.AbstractNetworkServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DemoGameServer extends AbstractNetworkServer {
    private final static float UPDATES_PER_SECOND = 30;
    private final static int STREAM_UPDATE_RATE = 3;
    private final static float STREAMS_PER_SECOND = UPDATES_PER_SECOND / STREAM_UPDATE_RATE;
    private final BitParser bitParser = new BitParser(ByteBuffer.wrap(new byte[10000]));

    private DemoGameLogic gameLogic;
    private int streamCounter;

    public DemoGameServer(int port) {
        super(port);
        this.gameLogic = new DemoGameLogic(this);
    }

    public void start() throws IOException {
        new UpdateLoop(this::update, UPDATES_PER_SECOND).run();
        stop();
    }

    @Override
    protected void configureClientAuthentication(CommunicationSteps steps) {
        steps.add(this::authenticate);
    }

    @Override
    protected void configureClientInitialization(CommunicationSteps steps) {
        steps.add(this::sendUpdateRate);
        steps.add(this::sendGameSettings);
    }

    @Override
    protected void onClientAuthenticated(Communicator communicator) {
        log(String.format("Client has been authenticated: %s", communicator.getEndpoint()));
    }

    @Override
    protected void onClientInitialized(Communicator communicator) {
        log(String.format("Client has been initialized: %s", communicator.getEndpoint()));
    }

    @Override
    protected boolean acceptConnection(String endpoint) {
        log(String.format("Accepting incoming connection: %s", endpoint));
        return true;
    }

    @Override
    protected void onConnectionFailed(String endpoint, Exception e) {
        log(String.format("Incoming connection failed: %s", endpoint), e);
    }

    @Override
    protected void onConnected(Communicator communicator) {
        log(String.format("Connection established: %s", communicator.getEndpoint()));
    }

    @Override
    protected void onDisconnected(Communicator communicator, boolean pending) {
        log(String.format("Connection lost: %s", communicator.getEndpoint()));
    }

    @Override
    protected void onUpdate(float deltaTime) {
        ++streamCounter;
        gameLogic.update(deltaTime);
        if (streamCounter == STREAM_UPDATE_RATE) {
            updateClients();
            streamCounter = 0;
        }
    }

    private boolean authenticate(Communicator communicator, DataBuffer buffer) throws InitializationException {
        var serverPassword = Read.textWithSizeHeader(buffer, StandardCharsets.UTF_8);
        if (!serverPassword.equals("serverPassword123")) {
            throw new InitializationException("Wrong password");
        }
        return true;
    }

    private void sendUpdateRate(Communicator communicator) {
        var buffer = communicator.getOutgoing();
        buffer.put(ServerMessages.UPDATE_RATE);
        buffer.putFloat(STREAMS_PER_SECOND);
    }

    private void sendGameSettings(Communicator communicator) {
        var buffer = communicator.getOutgoing();
        buffer.put(ServerMessages.GAME_SETTINGS);
        gameLogic.getGameSettings().serializeMessage(buffer);
    }

    @Override
    public void onMessage(Communicator communicator, DataBuffer buffer) {

    }

    private void updateClients() {
        sendToAll(ServerMessages.POSITION_UPDATE, true);

        bitParser.position(NetworkConstants.BIT_SIZE_HEADER);

        var balls = gameLogic.getRegisteredObjects();
        for (var object : balls) {
            bitParser.putInt(object.getId(), NetworkConstants.BIT_COUNT_OBJECT_ID);
            bitParser.putInt((int) (object.getPosition().getX() * 10), NetworkConstants.BIT_COUNT_POS_X);
            bitParser.putInt((int) (object.getPosition().getY() * 10), NetworkConstants.BIT_COUNT_POS_Y);
        }

        int bitPosition = (int) bitParser.position();
        int bytePosition = bitParser.bytePosition();
        int positionInByte = bitParser.bitIndex();
        int bytesToSend = positionInByte == 0 ? bytePosition : bytePosition + 1;

        bitParser.position(0);
        int bitSize = bitPosition - NetworkConstants.BIT_SIZE_HEADER;
        bitParser.putInt(bitSize, NetworkConstants.BIT_SIZE_HEADER);

        sendToAll(bitParser.getByteBuffer().array(), 0, bytesToSend, true);
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void log(String message, Throwable e) {
        System.out.println(message);
        e.printStackTrace();
    }
}
