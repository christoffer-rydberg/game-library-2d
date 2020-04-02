package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.io.BitParser;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.objects.ClientBoulder;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.demos.networkgame.common.NetworkConstants;
import com.gamelibrary2d.demos.networkgame.common.ServerMessages;
import com.gamelibrary2d.network.ClientObject;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.AbstractClient;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoFrameClient extends AbstractClient {
    private final BitParser bitParser = new BitParser();
    private final Map<Integer, ClientObject> objects = new HashMap<>();

    private DemoFrame frame;
    private float serverUpdatesPerSecond;

    void initialize(DemoFrame frame) {
        this.frame = frame;
    }

    @Override
    public void onConfigureInitialization(CommunicationSteps steps) {
        steps.read("updateRate", DataBuffer::getFloat);
        steps.read(GameSettings::new);
        steps.add(this::readState);
    }

    private boolean readState(CommunicationContext context, Communicator communicator, DataBuffer buffer) {
        var serverUpdatesPerSecond = context.get(Float.class, "updateRate");
        var gameState = new InitialState(serverUpdatesPerSecond, buffer);
        context.register(gameState);
        return true;
    }

    @Override
    public void initialized(CommunicationContext context) {
        serverUpdatesPerSecond = context.get(Float.class, "updateRate");
        frame.applySettings(context.get(GameSettings.class));

        var gameState = context.get(InitialState.class);
        for (var boulder : gameState.getBoulders()) {
            frame.addBoulder(boulder);
            objects.put(boulder.getId(), boulder);
        }
    }

    @Override
    public void onMessage(DataBuffer buffer) {
        byte id = buffer.get();
        switch (id) {
            case ServerMessages.POSITION_UPDATE:
                updatePositions(buffer);
                break;
            case ServerMessages.SPAWN_BOULDER:
                spawnBoulder(buffer);
                break;
        }
    }

    private void spawnBoulder(DataBuffer buffer) {
        var boulder = new ClientBoulder(buffer, serverUpdatesPerSecond);
        frame.addBoulder(boulder);
        objects.put(boulder.getId(), boulder);
    }

    private void updatePositions(DataBuffer buffer) {
        bitParser.setByteBuffer(buffer.internalByteBuffer());

        final int headerSize = NetworkConstants.HEADER_BIT_SIZE;
        final int bitSize = bitParser.getInt(headerSize);
        final int totalBitSize = headerSize + bitSize;
        final long endOfUpdate = bitParser.position() + bitSize;

        int byteSize = totalBitSize / 8;
        int positionInByte = totalBitSize - byteSize * 8;
        byteSize = positionInByte == 0 ? byteSize : byteSize + 1;

        while (bitParser.position() < endOfUpdate) {
            int id = bitParser.getInt(NetworkConstants.OBJECT_ID_BIT_SIZE);
            float x = bitParser.getInt(NetworkConstants.POS_X_BIT_SIZE);
            float y = bitParser.getInt(NetworkConstants.POS_Y_BIT_SIZE);
            var obj = objects.get(id);
            if (obj != null) {
                obj.setGoalPosition(x, y);
            }
        }

        if (bitParser.position() != endOfUpdate) {
            throw new IllegalStateException("Wrong bit count in update!");
        }

        buffer.position(buffer.position() + byteSize);
    }

    @Override
    protected void onDisconnected(Communicator communicator, Throwable cause) {
        frame.invokeLater(() -> onDisconnected(cause));
    }

    private void onDisconnected(Throwable cause) {
        System.err.println("Disconnected from server");
        cause.printStackTrace();
        frame.getGame().exit();
    }

    private static class InitialState {
        private final List<ClientBoulder> boulders;

        InitialState(float serverUpdatesPerSecond, DataBuffer buffer) {
            var size = buffer.getInt();
            boulders = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                boulders.add(new ClientBoulder(buffer, serverUpdatesPerSecond));
            }
        }

        List<ClientBoulder> getBoulders() {
            return boulders;
        }
    }
}