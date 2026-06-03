package me.lukiiy.flobby;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.lukiiy.flow.BaseLobby;
import me.lukiiy.flow.Flow;
import me.lukiiy.flow.FlowPlayer;
import me.lukiiy.flow.component.BasePlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

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
        saveDefaultConfig();
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
        player.setHealth(20);
        player.setSaturation(20);
        player.setFoodLevel(20);
        player.setFlying(false);
        player.setFireTicks(0);

        if (player.isDead()) player.spigot().respawn();

        player.setGameMode(GameMode.ADVENTURE);
        player.setExperienceLevelAndProgress(0);
        player.clearTitle();
        player.clearActiveItem();
        player.clearActivePotionEffects();
        player.getInventory().clear();
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
        String worldName = world == null ? "" : world.getName();

        return String.format("%s;%f;%f;%f;%f;%f", worldName, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public static Location deserialize(String data) {
        if (data == null || data.isBlank()) return null;

        String[] p = data.split(";", -1);
        if (p.length != 6) return null; // incomplete/missing data

        World world = p[0].isEmpty() ? null : Bukkit.getWorld(p[0]);
        if (!p[0].isEmpty() && world == null) return null; // unknown world

        return new Location(world, Double.parseDouble(p[1]), Double.parseDouble(p[2]), Double.parseDouble(p[3]), Float.parseFloat(p[4]), Float.parseFloat(p[5]));
    }
}
