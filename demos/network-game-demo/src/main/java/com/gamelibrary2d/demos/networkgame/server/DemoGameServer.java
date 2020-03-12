package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.common.io.BitParser;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.common.io.Read;
import com.gamelibrary2d.demos.networkgame.common.NetworkConstants;
import com.gamelibrary2d.demos.networkgame.common.ServerMessages;
import com.gamelibrary2d.network.AbstractGameServer;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;
import com.gamelibrary2d.network.common.initialization.CommunicationInitializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DemoGameServer extends AbstractGameServer<DemoGameLogic> {

    private final BitParser bitParser = new BitParser(ByteBuffer.wrap(new byte[10000]));

    private DemoGameLogic gameLogic;

    public DemoGameServer(int connectionPort, int reconnectionPort) {
        super(connectionPort, reconnectionPort);
    }

    @Override
    protected void initialize(DemoGameLogic gameLogic, ServerContextInitializer initializer) {
        this.gameLogic = gameLogic;
    }

    @Override
    protected void onConfigureAuthentication(CommunicationInitializer initializer) {
        initializer.add(this::authenticate);
    }

    @Override
    protected void onConfigureInitialization(CommunicationInitializer initializer) {
        initializer.add(this::sendUpdateRate);
        initializer.add(this::sendGameSettings);
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
        buffer.putInt(10);
    }

    private void sendGameSettings(Communicator communicator) {
        var buffer = communicator.getOutgoing();
        buffer.put(ServerMessages.GAME_SETTINGS);
        gameLogic.getGameSettings().serializeMessage(buffer);
    }

    @Override
    public void onMessage(Communicator communicator, DataBuffer buffer) {

    }

    @Override
    public void updateClients() {
        sendToAll(ServerMessages.POSITION_UPDATE, true);

        bitParser.position(NetworkConstants.BIT_SIZE_HEADER);

        var balls = gameLogic.getRegisteredObjects();
        for (var object : balls) {
            bitParser.putInt(object.getId(), NetworkConstants.BIT_COUNT_OBJECT_ID);
            bitParser.putInt(Math.round(object.position().getX()), NetworkConstants.BIT_COUNT_POS_X);
            bitParser.putInt(Math.round(object.position().getY()), NetworkConstants.BIT_COUNT_POS_Y);
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
}
