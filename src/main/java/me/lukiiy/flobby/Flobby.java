package me.lukiiy.flobby;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.lukiiy.flobby.cmd.Main;
import me.lukiiy.flobby.cmd.Leadership;
import me.lukiiy.flobby.cmd.MenuCUI;
import me.lukiiy.flow.*;
import me.lukiiy.flow.component.BasePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class Flobby extends JavaPlugin implements BaseLobby {
    private Location main = null;
    private Double boostY = null;
    private Double cutOffRadius = null;
    private double boostYForce;

    public static final Component LEADER_PREFIX = Component.text("⭐").color(FDefaults.LIGHT_YELLOW);

    @Override
    public void onEnable() {
        setupConfig();
        reloadVars();

        getServer().getPluginManager().registerEvents(new Echo(), this);
        Flow.getInstance().getManager().setLobby(this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, it -> {
            it.registrar().register(Main.register(), "Lobby management command");
            it.registrar().register(Leadership.register(), "Leadership management command");
            it.registrar().register(MenuCUI.register(), "Alternate implementation of the main Game Manager, via chat.");
        });
    }

    public static Flobby getInstance() {
        return JavaPlugin.getPlugin(Flobby.class);
    }

    public void setMain(Location main) {
        this.main = main;
        getConfig().set("pos", Utils.serialize(main));

        saveConfig();
    }

    public Location getMain() {
        return main;
    }

    public void setBoostY(@Nullable Double boostY) {
        this.boostY = boostY;
        getConfig().set("boostY", boostY);

        saveConfig();
    }

    public @Nullable Double getBoostY() {
        return boostY;
    }

    public void setCutOffRadius(@Nullable Double cutOffRadius) {
        this.cutOffRadius = cutOffRadius;
        getConfig().set("cutOffRadius", cutOffRadius);

        saveConfig();
    }

    public @Nullable Double getCutOffRadius() {
        return cutOffRadius;
    }

    public void setBoostYForce(double force) {
        this.boostYForce = force;
        getConfig().set("boostYForce", force);

        saveConfig();
    }

    public double getBoostYForce() {
        return boostYForce;
    }

    public World getWorld() {
        if (main == null) return null;

        return main.getWorld();
    }

    // Config
    public void setupConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void reloadVars() {
        main = Utils.deserialize(getConfig().getString("pos", null));
        boostY = Utils.loadDouble("boostY");
        cutOffRadius = Utils.loadDouble("cutOffRadius");
        boostYForce = getConfig().getDouble("boostYForce", 0);
    }

    @Override
    public void sendToLobby(@NonNull BasePlayer basePlayer) {
        if (!(basePlayer instanceof FlowPlayer fp)) return;

        sendToLobby(fp.getPlayer());
    }

    public void sendToLobby(@NonNull Player player) {
        FUtils.softReset(player, GameMode.ADVENTURE);
        player.teleport(Flobby.getInstance().main);

        FlowPlayer leader = Flow.getInstance().getLeader();

        if (leader == null) {
            setLeader(player);
        } else if (leader.getPlayer() == player) {
            player.getInventory().addItem(Item.HOST_ITEM);
        }
    }

    public void setLeader(@Nullable Player player) {
        if (player == null) {
            Flow.getInstance().setLeader(null);
            return;
        }

        FlowPlayer old = Flow.getInstance().getLeader();
        if (old != null) {
            Player oldP = old.getPlayer();

            oldP.playerListName(oldP.displayName());
            Bukkit.broadcast(Component.empty().append(LEADER_PREFIX).append(Component.text(" Leadership has been transferred to ").color(FDefaults.LIME)).append(player.displayName().color(FDefaults.LIGHT_YELLOW)));
            oldP.getInventory().remove(Item.HOST_ITEM);
            oldP.updateCommands();
        }

        Flow.getInstance().setLeader(new FlowPlayer(player));
        player.sendMessage(Component.text("You're the leader!").color(FDefaults.GREEN));

        if (player.getWorld() == getWorld()) player.getInventory().addItem(Item.HOST_ITEM);

        player.playerListName(Component.empty().append(LEADER_PREFIX).appendSpace().append(player.displayName()));
        player.updateCommands();
    }

    public void setLeaderRandom() {
        List<Player> remaining = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (remaining.isEmpty()) return;

        FlowPlayer leader = Flow.getInstance().getLeader();
        if (leader != null) remaining.remove(leader.getPlayer());

        setLeader(remaining.get(ThreadLocalRandom.current().nextInt(remaining.size())));
    }

    @Override
    public void reset() {
        if (getWorld() == null) return;

        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        getWorld().setGameRule(GameRule.DO_INSOMNIA, false);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
    }
}
