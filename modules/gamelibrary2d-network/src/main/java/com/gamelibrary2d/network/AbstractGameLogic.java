package com.gamelibrary2d.network;

import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.server.Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractGameLogic implements GameLogic {

    private final Server server;

    private final List<ServerPlayer> players;

    private final List<ServerPlayer> playersReadOnly;

    private final InternalServerObjectRegister objectRegister;

    public AbstractGameLogic(Server server) {
        this.server = server;
        players = new ArrayList<>();
        playersReadOnly = Collections.unmodifiableList(players);
        objectRegister = new InternalServerObjectRegister();
    }

    public Server getServer() {
        return server;
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
        obj.onUnregistered();
    }

    protected boolean isRegistered(ServerObject obj) {
        return objectRegister.get(obj.getId()) == obj;
    }

    public List<ServerPlayer> getRegisteredPlayers() {
        return playersReadOnly;
    }

    @Override
    public void getRegisteredPlayers(int communicatorId, List<ServerPlayer> output) {
        int count = players.size();
        for (int i = 0; i < count; ++i) {
            ServerPlayer player = players.get(i);
            if (player.getCommunicator().getId() == communicatorId) {
                output.add(player);
            }
        }
    }

    @Override
    public Collection<ServerObject> getRegisteredObjects() {
        return objectRegister.getValues();
    }

    @Override
    public ServerObject getRegisteredObject(int id) {
        return objectRegister.get(id);
    }

    @Override
    public void clearRegisteredObjects() {
        objectRegister.clear();
    }

    @Override
    public void initialized(Communicator communicator) {
        onInitialized(communicator);
    }

    @Override
    public void disconnected(Communicator communicator, boolean pending) {
        onDisconnected(communicator, pending);
        disconnectPlayers(communicator);
    }

    private void disconnectPlayers(Communicator communicator) {
        List<ServerPlayer> playersToRemove = new ArrayList<>();
        getRegisteredPlayers(communicator.getId(), playersToRemove);
        for (int i = 0; i < playersToRemove.size(); ++i) {
            onRemovePlayer(playersToRemove.get(i));
        }
    }

    protected abstract void onInitialized(Communicator communicator);

    protected abstract void onDisconnected(Communicator communicator, boolean pending);

    protected abstract void onRemovePlayer(ServerPlayer serverPlayer);
}