package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.io.BitParser;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.objects.network.*;
import com.gamelibrary2d.demos.networkgame.common.*;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.AbstractClient;
import com.gamelibrary2d.network.common.events.CommunicatorDisconnectedEvent;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gamelibrary2d.demos.networkgame.common.ServerMessages.*;

public class DemoFrameClient extends AbstractClient {
    private final DemoFrame frame;
    private final BitParser bitParser = new BitParser();
    private final Map<Integer, ClientObject> objects = new HashMap<>();

    private float serverUpdatesPerSecond;

    public DemoFrameClient(DemoFrame frame) {
        this.frame = frame;
    }

    @Override
    public void onConfigureInitialization(CommunicationSteps steps) {
        steps.read("updateRate", DataBuffer::getFloat);
        steps.read(GameSettings.class, GameSettings::new);
        steps.add(this::requestPlayers);
        steps.add(this::readState);
    }

    private void requestPlayers(CommunicationContext context, Communicator communicator) {
        communicator.getOutgoing().putInt(1);
    }

    private boolean readState(CommunicationContext context, Communicator communicator, DataBuffer buffer) {
        context.register(InitialState.class, new InitialState(buffer));
        return true;
    }

    @Override
    protected void onInitialized(CommunicationContext context, Communicator communicator) {
        communicator.addDisconnectedListener(this::onDisconnected);
        serverUpdatesPerSecond = context.get(Float.class, "updateRate");
        frame.applySettings(context.get(GameSettings.class));
        addObjects(context.get(InitialState.class).getObjects());
    }

    private void onDisconnected(CommunicatorDisconnectedEvent event) {
        frame.invokeLater(() -> {
            System.err.println("Disconnected from server");
            var cause = event.getCause();
            if (cause != null) {
                cause.printStackTrace();
            }
            frame.goToMenu();
        });
    }

    @Override
    public void onMessage(DataBuffer buffer) {
        byte id = buffer.get();
        switch (id) {
            case ServerMessages.UPDATE:
                update(buffer);
                break;
            case SPAWN:
                spawn(readObject(buffer.get(), buffer));
                break;
            case DESTROY:
                destroy(buffer.getInt());
                break;
            case GAME_OVER:
                gameOver();
                break;
            case GAME_ENDED:
                gameEnded();
                break;
        }
    }

    private void gameOver() {
        objects.clear();
        frame.gameOver();
    }

    private void gameEnded() {
        objects.clear();
        frame.gameEnded();
    }

    private ClientObject readObject(byte id, DataBuffer buffer) {
        switch (id) {
            case ObjectIdentifiers.BOULDER:
                return new Boulder(id, this, buffer);
            case ObjectIdentifiers.PLAYER:
                var isLocal = buffer.getBool();
                if (isLocal) {
                    return new LocalPlayer(id, this, buffer);
                } else {
                    return new RemotePlayer(id, this, buffer);
                }
            case ObjectIdentifiers.PORTAL:
                return new Portal(id, this, buffer);
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }
    }

    private void destroy(int id) {
        var obj = objects.remove(id);
        frame.destroy(obj);
    }

    private void spawn(ClientObject obj) {
        objects.put(obj.getId(), obj);
        frame.spawn(obj);
    }

    private <T extends ClientObject> void addObjects(List<T> objects) {
        for (var obj : objects) {
            spawn(obj);
        }
    }

    private void update(DataBuffer buffer) {
        bitParser.setByteBuffer(buffer.internalByteBuffer());

        final int headerSize = NetworkConstants.HEADER_BIT_SIZE;
        final int bitSize = bitParser.getInt(headerSize);
        final int totalBitSize = headerSize + bitSize;
        final long endOfUpdate = bitParser.position() + bitSize;

        int byteSize = totalBitSize / 8;
        int positionInByte = totalBitSize - byteSize * 8;
        byteSize = positionInByte == 0 ? byteSize : byteSize + 1;

        int time = bitParser.getInt();

        frame.setTime(time);

        while (bitParser.position() < endOfUpdate) {
            int id = bitParser.getInt(NetworkConstants.OBJECT_ID_BIT_SIZE);
            float x = bitParser.getInt(NetworkConstants.POS_X_BIT_SIZE);
            float y = bitParser.getInt(NetworkConstants.POS_Y_BIT_SIZE);
            float direction = bitParser.getInt(NetworkConstants.DIRECTION_BIT_SIZE);
            var obj = objects.get(id);
            if (obj != null) {
                obj.setGoalPosition(x, y);
                obj.setGoalDirection(direction);
            }
        }

        if (bitParser.position() != endOfUpdate) {
            throw new IllegalStateException("Wrong bit count in update!");
        }

        buffer.position(buffer.position() + byteSize);
    }

    public float getServerUpdatesPerSecond() {
        return serverUpdatesPerSecond;
    }

    public void requestNewGame() {
        getCommunicator().getOutgoing().put(ClientMessages.PLAY_AGAIN);
    }

    private class InitialState {
        private final List<ClientObject> objects;

        InitialState(DataBuffer buffer) {
            var size = buffer.getInt();
            objects = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                var obj = readObject(buffer.get(), buffer);
                objects.add(obj);
            }
        }

        List<ClientObject> getObjects() {
            return objects;
        }
    }
}