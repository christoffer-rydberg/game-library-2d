package com.gamelibrary2d.network;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.exceptions.LoadInterruptedException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public abstract class AbstractGenericNetworkFrame<TClientObject extends ClientObject, TClientPlayer extends ClientPlayer>
        extends AbstractFrame implements NetworkFrame {

    private final InternalNetworkClient networkClient;
    private final List<TClientPlayer> players = new ArrayList<>();
    private final List<TClientPlayer> playersReadOnly = Collections.unmodifiableList(players);
    private final List<TClientPlayer> localPlayers = new ArrayList<>();
    private final List<TClientPlayer> localPlayersReadOnly = Collections.unmodifiableList(localPlayers);
    private final Map<Integer, TClientObject> clientObjects = new HashMap<>();

    protected AbstractGenericNetworkFrame(Game game) {
        super(game);
        networkClient = new InternalNetworkClient();
        networkClient.setSendingDataOnUpdate(false); // Send data after frame update instead
    }

    public List<TClientPlayer> getLocalPlayers() {
        return localPlayersReadOnly;
    }

    public List<TClientPlayer> getClientPlayers() {
        return playersReadOnly;
    }

    public Collection<TClientObject> getClientObjects() {
        return clientObjects.values();
    }

    @Override
    public Communicator getCommunicator() {
        return networkClient.getCommunicator();
    }

    public void setCommunicator(Communicator communicator) {
        networkClient.setCommunicator(communicator);
    }

    @Override
    public float getServerUpdateRate() {
        return networkClient.getServerUpdateRate();
    }

    public boolean isLocalServer() {
        return networkClient.isLocalServer();
    }

    public void disconnectFromServer() {
        Communicator communicator = getCommunicator();
        if (communicator != null)
            communicator.disconnect();
    }

    @Override
    protected final void initializeFrame(FrameInitializer initializer) {
        networkClient.setContext(initializeNetworkFrame(initializer));
    }

    @Override
    public void load() throws LoadInterruptedException {
        if (isLoaded())
            return;

        if (!isInitialized()) {
            System.err.println("Must call initialize prior to load");
            return;
        }

        if (!networkClient.isConnected()) {
            try {
                networkClient.connect().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new LoadInterruptedException("Failed to connect to server");
            }
        }

        try {
            networkClient.clearInbox();
            networkClient.authenticate();
            super.load();
            networkClient.initialize();
        } catch (InitializationException e) {
            e.printStackTrace();
            reset();
            throw new LoadInterruptedException("Client/server communication failed");
        }
    }

    @Override
    public void reset() {
        super.reset();
        disconnectFromServer();
        players.clear();
        localPlayers.clear();
        clientObjects.clear();
    }

    @Override
    public void dispose() {
        super.dispose();
        disconnectFromServer();
        players.clear();
        localPlayers.clear();
        clientObjects.clear();
    }

    public void registerObject(TClientObject obj) {
        var previous = clientObjects.put(obj.getId(), obj);
        if (previous != null) {
            clientObjects.put(obj.getId(), previous);
            throw new GameLibrary2DRuntimeException("An object with the same id has already been registered.");
        }
        if (obj instanceof ClientPlayer) {
            @SuppressWarnings("unchecked")
            TClientPlayer player = (TClientPlayer) obj;
            players.add(player);
            if (player.isLocal(getCommunicator()))
                localPlayers.add(player);
        }
    }

    public void unregisterObject(TClientObject obj) {
        clientObjects.remove(obj.getId());
        if (obj instanceof ClientPlayer) {
            ClientPlayer player = (ClientPlayer) obj;
            players.remove(player);
            if (player.isLocal(getCommunicator()))
                localPlayers.remove(player);
        }
    }

    protected boolean clientObjectExists(int id) {
        return clientObjects.containsKey(id);
    }

    public TClientObject getClientObjectById(int id) {
        return clientObjects.get(id);
    }

    @Override
    protected void onUpdate(float deltaTime) {
        networkClient.update(deltaTime);
        super.onUpdate(deltaTime);
        var communicator = getCommunicator();
        try {
            communicator.sendOutgoing();
        } catch (IOException e) {
            communicator.disconnect(e);
        }
    }

    protected abstract FrameClient initializeNetworkFrame(FrameInitializer initializer);
}