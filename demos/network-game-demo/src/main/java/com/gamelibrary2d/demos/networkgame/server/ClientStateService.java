package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.demos.networkgame.server.objects.ServerPlayer;
import com.gamelibrary2d.network.common.Communicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientStateService {

    private final Map<Communicator, ClientState> clientStates = new HashMap<>();

    public void put(ClientState state) {
        clientStates.put(state.getCommunicator(), state);
    }

    public ClientState get(Communicator communicator) {
        return clientStates.get(communicator);
    }

    public List<ClientState> getAll() {
        return new ArrayList<>(clientStates.values());
    }

    public ClientState remove(Communicator communicator) {
        return clientStates.remove(communicator);
    }

    public int size() {
        return clientStates.size();
    }

    public boolean allReady() {
        for (var state : clientStates.values()) {
            if (!state.isReady()) {
                return false;
            }
        }

        return true;
    }

    public List<ServerPlayer> getPlayers() {
        List<ServerPlayer> players = new ArrayList<>();
        for (var state : clientStates.values()) {
            players.addAll(state.getPlayers());
        }
        return players;
    }
}
