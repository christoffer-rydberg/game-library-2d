package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.common.io.DataBuffer;
import com.gamelibrary2d.demos.networkgame.server.objects.DemoServerObject;
import com.gamelibrary2d.network.ServerObjectRegister;
import com.gamelibrary2d.network.common.Message;

import java.util.Collection;

public class ServerState implements Message {
    private final ServerObjectRegister<DemoServerObject> objectRegister = new ServerObjectRegister<>();

    public void register(DemoServerObject obj) {
        objectRegister.register(obj);
    }

    public void deregister(DemoServerObject obj) {
    }

    public Collection<DemoServerObject> getAll() {
        return objectRegister.getAll();
    }

    @Override
    public void serializeMessage(DataBuffer buffer) {
        var objects = objectRegister.getAll();
        buffer.putInt(objects.size());
        for (var obj : objects) {
            buffer.put(obj.getObjectIdentifier());
            obj.serializeMessage(buffer);
        }
    }


}
