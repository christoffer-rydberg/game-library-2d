package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.demos.networkgame.server.objects.ServerPlayer;
import com.gamelibrary2d.network.common.Communicator;

import java.util.ArrayList;

public class ClientState {
    private final Communicator communicator;
    private final ArrayList<ServerPlayer> players;

    private boolean ready;

    public ClientState(Communicator communicator, ArrayList<ServerPlayer> players) {
        this.communicator = communicator;
        this.players = players;
    }

    public Communicator getCommunicator() {
        return communicator;
    }

    public ServerPlayer getPlayer(int playerId) {
        for (int i = 0; i < players.size(); ++i) {
            ServerPlayer player = players.get(i);
            if (player.getId() == playerId) {
                return player;
            }
        }

        return null;
    }

    public ArrayList<ServerPlayer> getPlayers() {
        return players;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
