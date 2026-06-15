package me.lukiiy.flobby;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.lukiiy.flow.BaseLobby;
import me.lukiiy.flow.FDefaults;
import me.lukiiy.flow.Flow;
import me.lukiiy.flow.FlowPlayer;
import me.lukiiy.flow.component.BasePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public final class Flobby extends JavaPlugin implements BaseLobby {
    private Location main = null;
    private double boostY = -1;
    private double cutOffRadius = -1;

    @Override
    public void onEnable() {
        setupConfig();
        reloadVars();

        getServer().getPluginManager().registerEvents(new Echo(), this);
        Flow.getInstance().getManager().setLobby(this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, it -> it.registrar().register(Cmd.register(), "Lobby management command"));
    }

    public static Flobby getInstance() {
        return JavaPlugin.getPlugin(Flobby.class);
    }

    public void setMain(Location main) {
        this.main = main;
        getConfig().set("pos", serialize(main));

        saveConfig();
    }

    public Location getMain() {
        return main;
    }

    public void setBoostY(double boostY) {
        this.boostY = boostY;
        getConfig().set("boostY", boostY);

        saveConfig();
    }

    public double getBoostY() {
        return boostY;
    }

    public void setCutOffRadius(double cutOffRadius) {
        this.cutOffRadius = cutOffRadius;
        getConfig().set("cutOffRadius", cutOffRadius);

        saveConfig();
    }

    public double getCutOffRadius() {
        return cutOffRadius;
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
        main = deserialize(getConfig().getString("pos", null));
        boostY = getConfig().getDouble("boostY", -1);
        cutOffRadius = getConfig().getDouble("cutOffRadius", -1);
    }

    @Override
    public void sendToLobby(@NonNull BasePlayer basePlayer) {
        if (!(basePlayer instanceof FlowPlayer fp)) return;

        sendToLobby(fp.getPlayer());
    }

    public void sendToLobby(@NonNull Player player) {
        if (player.isDead()) player.spigot().respawn();

        AttributeInstance hpMod = player.getAttribute(Attribute.MAX_HEALTH);
        if (hpMod != null) player.setHealth(hpMod.getValue());

        player.setGameMode(GameMode.ADVENTURE);
        player.setSaturation(20);
        player.setFoodLevel(20);
        player.setFlying(false);
        player.setFireTicks(0);
        player.setExhaustion(0);
        player.setExperienceLevelAndProgress(0);
        player.clearTitle();
        player.clearActiveItem();
        player.clearActivePotionEffects();
        player.getInventory().clear();

        FlowPlayer leader = Flow.getInstance().getLeader();

        if (leader == null) leader = new FlowPlayer(player);

        if (leader.getPlayer() == player) {
            if (getWorld() != player.getWorld()) player.sendMessage(Component.text("You're the leader!").color(FDefaults.GREEN));

            player.getInventory().addItem(Item.hostItem);
        }
    }

    @Override
    public void reset() {
        if (getWorld() == null) return;

        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        getWorld().setGameRule(GameRule.DO_INSOMNIA, false);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
    }

    // Utils
    public static String serialize(Location loc) { // world;x;y;z;yaw;pitch
        if (loc == null) return null;

        World world = loc.getWorld();

        return String.format(Locale.US, "%s;%f;%f;%f;%f;%f", world == null ? "" : world.getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public static Location deserialize(String data) {
        if (data == null || data.isBlank()) return null;

        String[] parts = data.split(";", -1);
        if (parts.length != 6) return null; // incomplete/missing data

        World world = parts[0].isEmpty() ? null : Bukkit.getWorld(parts[0]);
        if (!parts[0].isEmpty() && world == null) return null; // unknown world

        try {
            return new Location(world, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
