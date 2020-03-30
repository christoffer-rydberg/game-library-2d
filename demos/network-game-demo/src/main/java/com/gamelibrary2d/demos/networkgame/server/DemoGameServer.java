package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.common.io.BitParser;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.demos.networkgame.common.NetworkConstants;
import com.gamelibrary2d.demos.networkgame.common.ServerMessages;
import com.gamelibrary2d.network.ServerObject;
import com.gamelibrary2d.network.ServerObjectRegister;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.server.Server;
import com.gamelibrary2d.network.common.server.ServerContext;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DemoGameServer implements ServerContext {

    public final static float UPDATES_PER_SECOND = 30;
    public final static int STREAM_UPDATE_RATE = 3;
    public final static float STREAMS_PER_SECOND = UPDATES_PER_SECOND / STREAM_UPDATE_RATE;
    private final BitParser bitParser = new BitParser(ByteBuffer.wrap(new byte[NetworkConstants.UPDATE_BUFFER_BYTE_SIZE]));
    private final ServerObjectRegister objectRegister = new ServerObjectRegister();

    private final Server server;
    private final DemoGameLogic gameLogic;

    private int streamCounter;

    public DemoGameServer(Server server) {
        this.server = server;
        this.gameLogic = new DemoGameLogic(this);
    }

    @Override
    public void configureClientAuthentication(CommunicationSteps steps) {
        steps.add(this::authenticate);
    }

    @Override
    public boolean acceptConnection(String endpoint) {
        log(String.format("Accepting incoming connection: %s", endpoint));
        return true;
    }

    @Override
    public void onConnectionFailed(String endpoint, Exception e) {
        log(String.format("Incoming connection failed: %s", endpoint), e);
    }

    @Override
    public void onClientAuthenticated(Communicator communicator) {
        log(String.format("Client has been authenticated: %s", communicator.getEndpoint()));
    }

    @Override
    public void onConnected(Communicator communicator) {
        log(String.format("Connection established: %s", communicator.getEndpoint()));
    }

    @Override
    public void configureClientInitialization(CommunicationSteps steps) {
        steps.add(this::sendUpdateRate);
        steps.add(this::sendGameSettings);
    }

    @Override
    public void onClientInitialized(Communicator communicator) {
        log(String.format("Client has been initialized: %s", communicator.getEndpoint()));
    }

    @Override
    public void onDisconnected(Communicator communicator, boolean pending) {
        log(String.format("Connection lost: %s", communicator.getEndpoint()));
    }

    @Override
    public void onMessage(Communicator communicator, DataBuffer buffer) {

    }

    @Override
    public void stop() {
        log("Server has stopped");
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
    public void update(float deltaTime) {
        // Cap the delta time in case of lag to avoid big movement of objects
        deltaTime = (Math.min(deltaTime, 0.1f));

        ++streamCounter;
        gameLogic.update(deltaTime);
        if (streamCounter == STREAM_UPDATE_RATE) {
            updateClients();
            streamCounter = 0;
        }
    }

    private void updateClients() {
        server.sendToAll(ServerMessages.POSITION_UPDATE, true);

        bitParser.position(NetworkConstants.HEADER_BIT_SIZE);

        var balls = objectRegister.getRegisteredObjects();
        for (var object : balls) {
            bitParser.putInt(object.getId(), NetworkConstants.OBJECT_ID_BIT_SIZE);
            bitParser.putInt(Math.round(object.getPosition().getX()), NetworkConstants.POS_X_BIT_SIZE);
            bitParser.putInt(Math.round(object.getPosition().getY()), NetworkConstants.POS_Y_BIT_SIZE);
        }

        int bitPosition = (int) bitParser.position();
        int bytePosition = bitParser.bytePosition();
        int positionInByte = bitParser.bitIndex();
        int bytesToSend = positionInByte == 0 ? bytePosition : bytePosition + 1;

        bitParser.position(0);
        int bitSize = bitPosition - NetworkConstants.HEADER_BIT_SIZE;
        bitParser.putInt(bitSize, NetworkConstants.HEADER_BIT_SIZE);

        server.sendToAll(bitParser.getByteBuffer().array(), 0, bytesToSend, true);
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void log(String message, Throwable e) {
        System.out.println(message);
        e.printStackTrace();
    }

    void createObject(byte id, ServerObject obj) {
        objectRegister.register(obj);
        server.sendToAll(id, false);
        server.sendToAll(obj, false);
    }
}
