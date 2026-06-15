package me.lukiiy.flobby;

import me.lukiiy.flow.*;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.EnumSet;

public class Echo implements Listener {
    private static final String MODIFY_PERMISSION = "flobby.modify";

    private static final EnumSet<EntityDamageEvent.DamageCause> ALLOWED_CAUSES = EnumSet.of(EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.KILL, EntityDamageEvent.DamageCause.WORLD_BORDER);
    private static final EnumSet<CreatureSpawnEvent.SpawnReason> ALLOWED_SPAWNREASON = EnumSet.of(CreatureSpawnEvent.SpawnReason.DISPENSE_EGG, CreatureSpawnEvent.SpawnReason.SPAWNER, CreatureSpawnEvent.SpawnReason.COMMAND, CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER);

    /**
     * the can modify check
     * @param player the player who caused it
     * @return A true if they SHOUDN'T modify, false otherwise.
     */
    private boolean cantModify(Player player) {
        if (!isLobby(player.getWorld())) return false;

        return !(player.hasPermission(MODIFY_PERMISSION) && player.getGameMode() == GameMode.CREATIVE);
    }

    private boolean isLobby(World world) {
        return world == Flobby.getInstance().getWorld();
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Minigame current = Flow.getInstance().getManager().getCurrent();

        if (current != null && current.isActive()) {
            FlowPlayer fp = new FlowPlayer(player);

            fp.setState(FlowPlayer.State.SPECTATING);
            current.addPlayer(fp);

            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Component.text("A game is in progress!").color(FDefaults.GRAY).append(Component.text(" You've been added as a spectator.").color(FDefaults.WHITE)));
            return;
        }

        Flobby.getInstance().sendToLobby(player);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (cantModify(e.getPlayer())) {
            e.setBuild(false);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (cantModify(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) { // TODO: only deny lobby items, when I implement those...
        Player p = e.getPlayer();

        if (cantModify(p)) e.setCancelled(true);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (isLobby(e.getEntity().getWorld()) && !ALLOWED_CAUSES.contains(e.getCause())) e.setCancelled(true);
    }

    @EventHandler
    public void hunger(FoodLevelChangeEvent e) {
        if (!isLobby(e.getEntity().getWorld())) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (!isLobby(p.getWorld())) return;

        if (!p.getGameMode().isInvulnerable() && e.getTo().y() <= Flobby.getInstance().getBoostY()) {
            p.spawnParticle(Particle.GUST_EMITTER_SMALL, p.getLocation(), 1);
            p.playSound(p.getLocation(), Sound.ENTITY_BREEZE_SHOOT, .75f, 0.75f);
            p.setVelocity(p.getVelocity().setY(3.5));
        }

        if (p.getLocation().distance(Flobby.getInstance().getMain()) >= Flobby.getInstance().getCutOffRadius()) Flobby.getInstance().sendToLobby(new FlowPlayer(p));
    }

    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent e) {
        if (isLobby(e.getLocation().getWorld()) && !ALLOWED_SPAWNREASON.contains(e.getSpawnReason())) e.setCancelled(true);
    }
}
