package io.github.toniidev.toniishops.listeners;

import io.github.toniidev.toniishops.classes.ServerPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (ServerPlayer.isPlayerNew(e.getPlayer())) {
            ServerPlayer.serverPlayers.add(new ServerPlayer(e.getPlayer()));
        }

        ServerPlayer player = ServerPlayer.getPlayer(e.getPlayer());
        assert player != null;
        player.refreshScoreboard();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        ServerPlayer.refreshScoreboard(e.getPlayer());
    }
}
