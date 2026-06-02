package me.lukiiy.flobby;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Echo implements Listener {
    private static final String MODIFY_PERMISSION = "flobby.modify";

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Location loc = Flobby.getInstance().getMain();

        if (loc != null) e.getPlayer().teleport(loc);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (e.getBlock().getWorld() == Flobby.getInstance().getWorld() && !e.getPlayer().hasPermission(MODIFY_PERMISSION)) e.setCancelled(true);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (e.getBlock().getWorld() == Flobby.getInstance().getWorld() && !e.getPlayer().hasPermission(MODIFY_PERMISSION)) e.setCancelled(true);
    }
}
