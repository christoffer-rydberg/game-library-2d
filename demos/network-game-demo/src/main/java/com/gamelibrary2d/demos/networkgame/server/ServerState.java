package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.network.ServerObject;
import com.gamelibrary2d.network.ServerObjectRegister;
import com.gamelibrary2d.network.common.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerState implements Message {

    private final ServerObjectRegister objectRegister = new ServerObjectRegister();

    private final List<ServerBoulder> boulders = new ArrayList<>();

    public void register(ServerBoulder obj) {
        objectRegister.register(obj);
        boulders.add(obj);
    }

    public Collection<ServerObject> getRegistered() {
        return objectRegister.getRegisteredObjects();
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        buffer.putInt(boulders.size());
        for (var obj : boulders) {
            obj.serializeMessage(buffer);
        }
    }
}
