package com.gamelibrary2d.demos.networkgame.client.frames;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.common.io.BitParser;
import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.client.objects.DefaultDemoClientObject;
import com.gamelibrary2d.demos.networkgame.client.objects.DemoClientObject;
import com.gamelibrary2d.demos.networkgame.client.objects.LocalPlayer;
import com.gamelibrary2d.demos.networkgame.common.GameSettings;
import com.gamelibrary2d.demos.networkgame.common.NetworkConstants;
import com.gamelibrary2d.demos.networkgame.common.ObjectIdentifiers;
import com.gamelibrary2d.demos.networkgame.common.ServerMessages;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.client.AbstractClient;
import com.gamelibrary2d.network.common.initialization.CommunicationContext;
import com.gamelibrary2d.network.common.initialization.CommunicationSteps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gamelibrary2d.demos.networkgame.common.ServerMessages.DESTROY;
import static com.gamelibrary2d.demos.networkgame.common.ServerMessages.SPAWN;

public class DemoFrameClient extends AbstractClient {
    private final BitParser bitParser = new BitParser();
    private final Map<Integer, DemoClientObject> objects = new HashMap<>();

    private DemoFrame frame;
    private float serverUpdatesPerSecond;

    void initialize(DemoFrame frame) {
        this.frame = frame;
    }

    @Override
    public void onConfigureInitialization(CommunicationSteps steps) {
        steps.read("updateRate", DataBuffer::getFloat);
        steps.read(GameSettings::new);
        steps.add(this::requestPlayers);
        steps.add(this::readState);
    }

    private void requestPlayers(CommunicationContext context, Communicator communicator) {
        communicator.getOutgoing().putInt(1);
    }

    private boolean readState(CommunicationContext context, Communicator communicator, DataBuffer buffer) {
        context.register(new InitialState(buffer));
        return true;
    }

    @Override
    public void initialized(CommunicationContext context) {
        serverUpdatesPerSecond = context.get(Float.class, "updateRate");
        frame.applySettings(context.get(GameSettings.class));
        addObjects(context.get(InitialState.class).getObjects());
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
        }
    }

    private DemoClientObject readObject(byte id, DataBuffer buffer) {
        switch (id) {
            case ObjectIdentifiers.PLAYER:
                var isLocal = buffer.getBool();
                if (isLocal) {
                    return new LocalPlayer(id, this, buffer);
                } else {
                    return new DefaultDemoClientObject(id, this, buffer);
                }
            case ObjectIdentifiers.PORTAL:
            case ObjectIdentifiers.BOULDER:
                return new DefaultDemoClientObject(id, this, buffer);
        }

        throw new GameLibrary2DRuntimeException("Invalid object id");
    }

    private void destroy(int id) {
        var obj = objects.remove(id);
        frame.destroy(obj);
    }

    private void spawn(DemoClientObject obj) {
        objects.put(obj.getId(), obj);
        frame.spawn(obj);
    }

    private <T extends DemoClientObject> void addObjects(List<T> objects) {
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

        while (bitParser.position() < endOfUpdate) {
            int id = bitParser.getInt(NetworkConstants.OBJECT_ID_BIT_SIZE);
            float x = bitParser.getInt(NetworkConstants.POS_X_BIT_SIZE);
            float y = bitParser.getInt(NetworkConstants.POS_Y_BIT_SIZE);
            float rotation = bitParser.getInt(NetworkConstants.ROTATION_BIT_SIZE);
            var obj = objects.get(id);
            if (obj != null) {
                obj.setGoalPosition(x, y);
                obj.setGoalRotation(rotation);
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
        if (cause != null) {
            cause.printStackTrace();
        }
        frame.goToMenu();
    }

    public float getServerUpdatesPerSecond() {
        return serverUpdatesPerSecond;
    }

    private class InitialState {
        private final List<DemoClientObject> objects;

        InitialState(DataBuffer buffer) {
            var size = buffer.getInt();
            objects = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                var obj = readObject(buffer.get(), buffer);
                objects.add(obj);
            }
        }

        List<DemoClientObject> getObjects() {
            return objects;
        }
    }
}