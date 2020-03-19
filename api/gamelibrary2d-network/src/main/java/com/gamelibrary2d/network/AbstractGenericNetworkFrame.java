package com.gamelibrary2d.network;

import com.gamelibrary2d.Game;
import com.gamelibrary2d.common.exceptions.GameLibrary2DRuntimeException;
import com.gamelibrary2d.exceptions.LoadFailedException;
import com.gamelibrary2d.frames.AbstractFrame;
import com.gamelibrary2d.network.common.Communicator;
import com.gamelibrary2d.network.common.exceptions.InitializationException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public abstract class AbstractGenericNetworkFrame<
        TFrameClient extends FrameClient,
        TClientObject extends ClientObject,
        TClientPlayer extends ClientPlayer>
        extends AbstractFrame implements NetworkFrame<TFrameClient> {

    private final InternalNetworkClient<TFrameClient> networkClient;
    private final List<TClientPlayer> players = new ArrayList<>();
    private final List<TClientPlayer> playersReadOnly = Collections.unmodifiableList(players);
    private final List<TClientPlayer> localPlayers = new ArrayList<>();
    private final List<TClientPlayer> localPlayersReadOnly = Collections.unmodifiableList(localPlayers);
    private final Map<Integer, TClientObject> clientObjects = new HashMap<>();

    protected AbstractGenericNetworkFrame(Game game, TFrameClient frameClient) {
        super(game);
        networkClient = new InternalNetworkClient<>();
        networkClient.setContext(frameClient);
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

    public void disconnectFromServer() {
        Communicator communicator = networkClient.getCommunicator();
        if (communicator != null)
            communicator.disconnect();
    }

    @Override
    public TFrameClient getClient() {
        return networkClient.getContext();
    }

    @Override
    public void load() throws LoadFailedException {
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
                throw new LoadFailedException("Failed to connect to server", e);
            }
        }

        try {
            networkClient.clearInbox();
            networkClient.authenticate();
            super.load();
            networkClient.initialize();
        } catch (InitializationException e) {
            reset();
            throw new LoadFailedException("Client/server communication failed", e);
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
            if (player.isLocal(networkClient.getCommunicator()))
                localPlayers.add(player);
        }
    }

    public void unregisterObject(TClientObject obj) {
        clientObjects.remove(obj.getId());
        if (obj instanceof ClientPlayer) {
            ClientPlayer player = (ClientPlayer) obj;
            players.remove(player);
            if (player.isLocal(networkClient.getCommunicator()))
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
        networkClient.update();
        super.onUpdate(deltaTime);
        var communicator = networkClient.getCommunicator();
        try {
            communicator.sendOutgoing();
        } catch (IOException e) {
            communicator.disconnect(e);
        }
    }
}