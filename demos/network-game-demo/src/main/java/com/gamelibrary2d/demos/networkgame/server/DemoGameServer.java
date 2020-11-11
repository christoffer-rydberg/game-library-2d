package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.common.io.BitParser;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.DynamicByteBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.demos.networkgame.common.ClientMessages;
import com.gamelibrary2d.demos.networkgame.common.NetworkConstants;
import com.gamelibrary2d.demos.networkgame.common.RotationDirection;
import com.gamelibrary2d.demos.networkgame.common.ServerMessages;
import com.gamelibrary2d.demos.networkgame.server.objects.DemoServerObject;
import com.gamelibrary2d.demos.networkgame.server.objects.ServerPlayer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.ConnectionType;
import com.gamelibrary2d.network.common.NetworkCommunicator;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;
import com.gamelibrary2d.network.common.security.ServerHandshake;
import com.gamelibrary2d.network.common.server.Server;
import com.gamelibrary2d.network.common.server.ServerContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.ArrayList;

public class DemoGameServer implements ServerContext {
    public final static float UPDATES_PER_SECOND = 30;
    private final static int STREAM_UPDATE_RATE = 3;
    private final static float STREAMS_PER_SECOND = UPDATES_PER_SECOND / STREAM_UPDATE_RATE;
    private final BitParser bitParser = new BitParser(ByteBuffer.wrap(new byte[NetworkConstants.UPDATE_BUFFER_BYTE_SIZE]));

    private final ServerState state = new ServerState();
    private final ClientStateService clientStateService = new ClientStateService();

    private final Server server;
    private final DemoGameLogic gameLogic;
    private final KeyPair keyPair;
    private final DataBuffer decryptionBuffer = new DynamicByteBuffer();

    private int streamCounter;

    private float startCountdown = 10f;
    private float timer;

    public DemoGameServer(Server server) {
        this(server, null);
    }

    public DemoGameServer(Server server, KeyPair keyPair) {
        this.server = server;
        this.keyPair = keyPair;
        this.gameLogic = new DemoGameLogic(this);

    }

    @Override
    public void configureClientAuthentication(CommunicationSteps steps) {
        if (keyPair == null) {
            throw new NullPointerException("Key pair has not been set");
        }

        var serverHandshake = new ServerHandshake(keyPair);
        serverHandshake.configure(steps);
        steps.add(this::readPassword);
        steps.add(this::readUdpPort);
    }

    @Override
    public void configureClientInitialization(CommunicationSteps steps) {
        steps.add(this::sendUpdateRate);
        steps.write(gameLogic::getGameSettings);
        steps.read("requestedPlayers", DataBuffer::getInt);
        steps.write(() -> state);
    }

    private boolean readPassword(CommunicationContext context, Communicator communicator, DataBuffer inbox) throws IOException {
        decryptionBuffer.clear();
        communicator.readEncrypted(inbox, decryptionBuffer);
        decryptionBuffer.flip();

        var serverPassword = Read.textWithSizeHeader(decryptionBuffer, StandardCharsets.UTF_8);
        if (!serverPassword.equals("serverPassword123")) {
            throw new IOException("Wrong password");
        }
        return true;
    }

    private boolean readUdpPort(CommunicationContext context, Communicator communicator, DataBuffer inbox) throws IOException {
        decryptionBuffer.clear();
        communicator.readEncrypted(inbox, decryptionBuffer);
        decryptionBuffer.flip();

        var udpPort = decryptionBuffer.getInt();
        ((NetworkCommunicator) communicator).enableUdp(ConnectionType.WRITE, 0, udpPort);
        return true;
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
            players.add(new ServerPlayer(gameLogic, communicator, settings.getBoulderBounds()));
        }

        var communicatorState = new ClientState(communicator, players);
        clientStateService.put(communicatorState);

        if (!gameLogic.gameIsRunning()) {
            communicator.getOutgoing().put(ServerMessages.GAME_ENDED);
        } else {
            communicatorState.setReady(true);
        }
    }

    @Override
    public void onDisconnected(Communicator communicator, boolean pending) {
        log(String.format("Connection lost: %s", communicator.getEndpoint()));
        var clientState = clientStateService.remove(communicator);
        if (clientState != null) {
            for (var player : clientState.getPlayers()) {
                gameLogic.destroy(player);
            }
        }
    }

    @Override
    public void onMessage(Communicator communicator, DataBuffer buffer) {
        byte id = buffer.get();
        switch (id) {
            case ClientMessages.PLAYER_ROTATION:
                var player = clientStateService.get(communicator).getPlayer(buffer.getInt());
                var rotationDirection = buffer.getEnum(RotationDirection.class);
                player.setRotationDirection(rotationDirection);
                break;
            case ClientMessages.PLAY_AGAIN:
                clientStateService.get(communicator).setReady(true);
                break;
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

    @Override
    public void update(float deltaTime) {
        if (clientStateService.size() > 0) {
            if (!gameLogic.gameIsRunning()) {
                startCountdown -= deltaTime;
                if (startCountdown <= 0 || clientStateService.allReady()) {
                    startCountdown = 10f;
                    startGame();
                }
            } else {
                // Cap the delta time in case of lag to avoid big movement of objects
                deltaTime = (Math.min(deltaTime, 0.1f));

                timer += deltaTime;

                ++streamCounter;
                gameLogic.update(deltaTime);
                if (streamCounter == STREAM_UPDATE_RATE) {
                    updateClients();
                    streamCounter = 0;
                }
            }
        }
    }

    private void startGame() {
        timer = 0;

        for (var clientState : clientStateService.getAll()) {
            if (!clientState.isReady()) {
                clientState.getCommunicator().disconnect();
            }
        }

        gameLogic.startGame(clientStateService.getPlayers());
    }

    void endGame() {
        state.clear();
        for (var communicatorState : clientStateService.getAll()) {
            communicatorState.setReady(false);
        }

        server.sendToAll(ServerMessages.GAME_OVER, false);
    }

    void spawn(DemoServerObject obj) {
        state.register(obj);
        server.sendToAll(ServerMessages.SPAWN, false);
        server.sendToAll(obj.getObjectIdentifier(), false);
        server.sendToAll(obj, false);
    }

    private boolean allPlayersAreDead() {
        var players = clientStateService.getPlayers();
        for (var player : players) {
            if (!player.isDestroyed()) {
                return false;
            }
        }

        return true;
    }

    void destroy(DemoServerObject obj) {
        state.deregister(obj);
        server.sendToAll(ServerMessages.DESTROY, false);
        server.sendToAll(obj.getId(), false);

        if (obj instanceof ServerPlayer) {
            if (allPlayersAreDead()) {
                gameLogic.endGame();
            }
        }
    }

    private void updateClients() {
        server.sendToAll(ServerMessages.UPDATE, true);

        bitParser.position(NetworkConstants.HEADER_BIT_SIZE);

        bitParser.putInt((int) timer);

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
