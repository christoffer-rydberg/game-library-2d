package com.gamelibrary2d.demos.networkgame.client;

import com.gamelibrary2d.common.io.BitParser;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.demos.networkgame.common.NetworkConstants;
import com.gamelibrary2d.demos.networkgame.common.ServerMessages;
import com.gamelibrary2d.network.AbstractFrameClient;
import com.gamelibrary2d.network.ClientObject;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.util.HashMap;
import java.util.Map;

public class DemoFrameClient extends AbstractFrameClient<DemoCommunicator> {
    private final BitParser bitParser = new BitParser();
    private final Map<Integer, ClientObject> objects = new HashMap<>();

    private DemoFrame frame;
    private float serverUpdateRate;

    void initialize(DemoFrame frame) {
        this.frame = frame;
    }

    @Override
    public void configureInitialization(CommunicationSteps steps) {

    }

    @Override
    public void onInitialized() {

    }

    @Override
    public void onMessage(DataBuffer buffer) {
        byte id = buffer.get();
        switch (id) {
            case ServerMessages.UPDATE_RATE:
                onUpdateRateMessage(buffer);
                break;
            case ServerMessages.GAME_SETTINGS:
                onGameSettingsMessage(buffer);
                break;
            case ServerMessages.POSITION_UPDATE:
                onUpdateMessage(buffer);
                break;
            case ServerMessages.SPAWN_BOULDER:
                var boulder = new ClientBoulder(this, buffer);
                frame.addBoulder(boulder);
                objects.put(boulder.getId(), boulder);
                break;
        }
    }

    private void onUpdateRateMessage(DataBuffer buffer) {
        serverUpdateRate = buffer.getFloat();
    }

    private void onGameSettingsMessage(DataBuffer buffer) {
        frame.applySettings(new GameSettings(buffer));
    }

    private void onUpdateMessage(DataBuffer buffer) {
        bitParser.setByteBuffer(buffer.internalByteBuffer());

        final int headerSize = NetworkConstants.BIT_SIZE_HEADER;
        final int bitSize = bitParser.getInt(headerSize);
        final int totalBitSize = headerSize + bitSize;
        final long endOfUpdate = bitParser.position() + bitSize;

        int byteSize = totalBitSize / 8;
        int positionInByte = totalBitSize - byteSize * 8;
        byteSize = positionInByte == 0 ? byteSize : byteSize + 1;

        while (bitParser.position() < endOfUpdate) {
            int id = bitParser.getInt(NetworkConstants.BIT_COUNT_OBJECT_ID);
            float x = bitParser.getInt(NetworkConstants.BIT_COUNT_POS_X) / 10f;
            float y = bitParser.getInt(NetworkConstants.BIT_COUNT_POS_Y) / 10f;
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
    public float getServerUpdatesPerSecond() {
        return serverUpdateRate;
    }

    @Override
    protected void onDisconnected(DemoCommunicator communicator, Throwable cause) {
        frame.invokeLater(() -> onDisconnected(cause));
    }

    private void onDisconnected(Throwable cause) {
        System.err.println("Disconnected from server");
        cause.printStackTrace();
        frame.getGame().exit();
    }
}