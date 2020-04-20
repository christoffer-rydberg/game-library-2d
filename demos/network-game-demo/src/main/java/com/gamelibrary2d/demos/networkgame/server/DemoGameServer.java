package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.common.io.BitParser;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.common.random.RandomInstance;
import com.gamelibrary2d.demos.networkgame.common.ClientMessages;
import com.gamelibrary2d.demos.networkgame.common.NetworkConstants;
import com.gamelibrary2d.demos.networkgame.common.RotationDirection;
import com.gamelibrary2d.demos.networkgame.common.ServerMessages;
import com.gamelibrary2d.demos.networkgame.server.objects.DemoServerObject;
import com.gamelibrary2d.demos.networkgame.server.objects.ServerPlayer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.server.Server;
import com.gamelibrary2d.network.common.server.ServerContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DemoGameServer implements ServerContext {
    public final static float UPDATES_PER_SECOND = 30;
    private final static int STREAM_UPDATE_RATE = 3;
    private final static float STREAMS_PER_SECOND = UPDATES_PER_SECOND / STREAM_UPDATE_RATE;
    private final BitParser bitParser = new BitParser(ByteBuffer.wrap(new byte[NetworkConstants.UPDATE_BUFFER_BYTE_SIZE]));

    private final ServerState state = new ServerState();
    private final PlayerLookup playerLookup = new PlayerLookup();

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
    public void onClientAuthenticated(CommunicationContext context, Communicator communicator) {
        log(String.format("Client has been authenticated: %s", communicator.getEndpoint()));
    }

    @Override
    public void onConnected(Communicator communicator) {
        log(String.format("Connection established: %s", communicator.getEndpoint()));
    }

    @Override
    public void configureClientInitialization(CommunicationSteps steps) {
        steps.add(this::sendUpdateRate);
        steps.write(gameLogic::getGameSettings);
        steps.read("requestedPlayers", DataBuffer::getInt);
        steps.write(() -> state);
    }

    private void sendUpdateRate(CommunicationContext context, Communicator communicator) {
        communicator.getOutgoing().putFloat(STREAMS_PER_SECOND);
    }

    @Override
    public void onClientInitialized(CommunicationContext context, Communicator communicator) {
        log(String.format("Client has been initialized: %s", communicator.getEndpoint()));

        var settings = gameLogic.getGameSettings();

        var requestedPlayers = context.get(Integer.class, "requestedPlayers");

        var players = new ArrayList<ServerPlayer>(requestedPlayers);

        for (int i = 0; i < requestedPlayers; ++i) {
            var player = new ServerPlayer(gameLogic, communicator, settings.getGameBounds(), settings.getBoulderBounds());
            var posX = RandomInstance.get().nextFloat() * settings.getGameBounds().width();
            var posY = RandomInstance.get().nextFloat() * settings.getGameBounds().height();
            player.setPosition(posX, posY);
            players.add(player);
            gameLogic.spawn(player);
        }

        playerLookup.setPlayers(communicator, players);
    }

    @Override
    public void onDisconnected(Communicator communicator, boolean pending) {
        log(String.format("Connection lost: %s", communicator.getEndpoint()));
        playerLookup.removePlayers(communicator);
    }

    @Override
    public void onMessage(Communicator communicator, DataBuffer buffer) {
        byte id = buffer.get();
        switch (id) {
            case ClientMessages.PLAYER_ROTATION:
                var player = playerLookup.getPlayer(communicator, buffer.getInt());
                var rotationDirection = buffer.getEnum(RotationDirection.class);
                player.setRotation(rotationDirection);
        }
    }

    @Override
    public void start() {
        log("Server has started");
    }

    @Override
    public void stop() {
        log("Server has stopped");
    }

    private boolean authenticate(CommunicationContext context, Communicator communicator, DataBuffer buffer) throws IOException {
        var serverPassword = Read.textWithSizeHeader(buffer, StandardCharsets.UTF_8);
        if (!serverPassword.equals("serverPassword123")) {
            throw new IOException("Wrong password");
        }
        return true;
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

    void spawn(DemoServerObject obj) {
        state.register(obj);
        server.sendToAll(ServerMessages.SPAWN, false);
        server.sendToAll(obj.getObjectIdentifier(), false);
        server.sendToAll(obj, false);
    }

    void destroy(DemoServerObject obj) {
        state.deregister(obj);
        server.sendToAll(ServerMessages.DESTROY, false);
        server.sendToAll(obj.getId(), false);
    }

    private void updateClients() {
        server.sendToAll(ServerMessages.UPDATE, true);

        bitParser.position(NetworkConstants.HEADER_BIT_SIZE);

        var objects = state.getAll();
        for (var object : objects) {
            bitParser.putInt(object.getId(), NetworkConstants.OBJECT_ID_BIT_SIZE);
            bitParser.putInt(Math.round(object.getPosition().getX()), NetworkConstants.POS_X_BIT_SIZE);
            bitParser.putInt(Math.round(object.getPosition().getY()), NetworkConstants.POS_Y_BIT_SIZE);
            bitParser.putInt(Math.round(object.getDirection()), NetworkConstants.DIRECTION_BIT_SIZE);
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
}
