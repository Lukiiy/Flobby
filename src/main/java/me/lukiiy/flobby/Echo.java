package me.lukiiy.flobby;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Echo implements Listener {
    private static final String MODIFY_PERMISSION = "flobby.modify";

    /**
     * The default check for most of the event handlers here!
     * @param world event world
     * @param player the player who caused it
     * @return A true if they SHOUDN'T modify, false otherwise.
     */
    private boolean cantModify(World world, Player player) {
        return (world == Flobby.getInstance().getWorld() && !player.hasPermission(MODIFY_PERMISSION));
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Location loc = Flobby.getInstance().getMain();

        if (loc != null) e.getPlayer().teleport(loc);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (cantModify(e.getBlock().getWorld(), e.getPlayer())) {
            e.setBuild(false);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (cantModify(e.getBlock().getWorld(), e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) { // TODO: only deny lobby items, when I implement those...
        Player p = e.getPlayer();

        if (cantModify(p.getWorld(), p)) e.setCancelled(true);
    }
}
