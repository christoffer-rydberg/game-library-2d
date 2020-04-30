package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.demos.networkgame.server.objects.ServerPlayer;
import com.gamelibrary2d.network.common.Communicator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClientState {
    private final Communicator communicator;
    private final List<ServerPlayer> players;
    private final List<ServerPlayer> playersReadonly;

    private boolean ready;

    public ClientState(Communicator communicator, Collection<ServerPlayer> players) {
        this.communicator = communicator;
        this.players = new ArrayList<>(players);
        playersReadonly = Collections.unmodifiableList(this.players);
    }

    public Communicator getCommunicator() {
        return communicator;
    }

    public void addPlayers(Collection<ServerPlayer> players) {
        players.addAll(players);
    }

    public void addPlayer(ServerPlayer player) {
        players.add(player);
    }

    public void removePlayer(ServerPlayer player) {
        players.remove(player);
    }

    public ServerPlayer getPlayer(int playerId) {
        for (int i = 0; i < players.size(); ++i) {
            var player = players.get(i);
            if (player.getId() == playerId) {
                return player;
            }
        }

        return null;
    }

    public List<ServerPlayer> getPlayers() {
        return playersReadonly;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
