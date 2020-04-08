package com.gamelibrary2d.demos.networkgame.server;

import com.gamelibrary2d.demos.networkgame.server.objects.ServerPlayer;
import com.gamelibrary2d.network.common.Communicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerLookup {
    private final Map<Communicator, ArrayList<ServerPlayer>> players = new HashMap<>();

    public void setPlayers(Communicator communicator, ArrayList<ServerPlayer> players) {
        this.players.put(communicator, players);
    }

    public void addPlayer(Communicator communicator, ServerPlayer player) {
        this.players.get(communicator).add(player);
    }

    public void removePlayer(Communicator communicator, ServerPlayer player) {
        this.players.get(communicator).remove(player);
    }

    public ArrayList<ServerPlayer> getPlayers(Communicator communicator) {
        return players.get(communicator);
    }

    public ArrayList<ServerPlayer> removePlayers(Communicator communicator) {
        return players.remove(communicator);
    }

    public ServerPlayer getPlayer(Communicator communicator, int playerId) {
        var players = this.players.get(communicator);
        for (int i = 0; i < players.size(); ++i) {
            var player = players.get(i);
            if (player.getId() == playerId) {
                return player;
            }
        }

        return null;
    }
}
