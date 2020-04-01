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
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.util.HashMap;
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
        steps.add(this::readUpdateRate);
        steps.add(this::readGameSettings);
        steps.add(this::readState);
    }

    private boolean readUpdateRate(Communicator communicator, DataBuffer buffer) {
        serverUpdatesPerSecond = buffer.getFloat();
        return true;
    }

    private boolean readGameSettings(Communicator communicator, DataBuffer buffer) {
        var gameSettings = new GameSettings(buffer);
        frame.invokeLater(() -> frame.applySettings(gameSettings));
        return true;
    }

    private boolean readState(Communicator communicator, DataBuffer buffer) {
        int boulders = buffer.getInt();
        for (int i = 0; i < boulders; ++i) {
            // TODO: Read object header
            spawnBoulder(buffer);
        }
        return true;
    }

    @Override
    public void onInitialized() {

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
}