package com.gamelibrary2d.network;

import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;

import java.util.*;

public abstract class AbstractGameLogic {
    private final List<ServerPlayer> players;
    private final List<ServerPlayer> playersReadOnly;
    private final ObjectRegister objectRegister;

    protected AbstractGameLogic() {
        players = new ArrayList<>();
        playersReadOnly = Collections.unmodifiableList(players);
        objectRegister = new ObjectRegister();
    }

    protected void register(ServerObject obj) {
        if (obj instanceof ServerPlayer) {
            registerPlayer((ServerPlayer) obj);
        } else {
            registerObject(obj);
        }
    }

    private void registerPlayer(ServerPlayer player) {
        registerObject(player);
        players.add(player);
    }

    private void registerObject(ServerObject obj) {
        int id = obj.getId();
        if (id == Integer.MAX_VALUE)
            id = objectRegister.add(obj);
        else
            objectRegister.add(id, obj);
        obj.onRegistered(id);
    }

    protected void unregister(ServerObject obj) {
        if (obj instanceof ServerPlayer) {
            unregisterPlayer((ServerPlayer) obj);
        } else {
            unregisterObject(obj);
        }
    }

    private void unregisterPlayer(ServerPlayer player) {
        unregisterObject(player);
        players.remove(player);
    }

    private void unregisterObject(ServerObject obj) {
        objectRegister.remove(obj.getId());
        obj.onDeregister();
    }

    protected boolean isRegistered(ServerObject obj) {
        return objectRegister.get(obj.getId()) == obj;
    }

    public List<ServerPlayer> getRegisteredPlayers() {
        return playersReadOnly;
    }

    protected void getRegisteredPlayers(int communicatorId, List<ServerPlayer> output) {
        int count = players.size();
        for (int i = 0; i < count; ++i) {
            ServerPlayer player = players.get(i);
            if (player.getCommunicator().getId() == communicatorId) {
                output.add(player);
            }
        }
    }

    public Collection<ServerObject> getRegisteredObjects() {
        return objectRegister.getValues();
    }

    protected ServerObject getRegisteredObject(int id) {
        return objectRegister.get(id);
    }

    protected void clearRegisteredObjects() {
        players.clear();
        objectRegister.clear();
    }

    private static class ObjectRegister {
        private final Map<Integer, ServerObject> dictionary = new HashMap<>();

        private int lastId = -1;

        ServerObject get(int key) {
            return dictionary.get(key);
        }

        Collection<ServerObject> getValues() {
            return dictionary.values();
        }

        int add(ServerObject obj) {
            int key = ++lastId;
            var previous = dictionary.put(key, obj);
            if (previous != null) {
                dictionary.put(key, previous);
                throw new GameLibrary2DRuntimeException("Failed to resolve a unique key for object!");
            }
            return key;
        }

        void remove(int key) {
            dictionary.remove(key);
        }

        void clear() {
            lastId = -1;
            dictionary.clear();
        }

        void add(int id, ServerObject obj) {
            if (id < 0 || id > lastId)
                throw new IllegalStateException("Object has an invalid identifier: " + id);

            if (dictionary.containsKey(id))
                throw new IllegalStateException("An object with the specified identifier has already been addded: " + id);

            dictionary.put(id, obj);
        }
    }
}