package me.lukiiy.flobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class Utils {
    public static Double loadDouble(String path) {
        FileConfiguration config = Flobby.getInstance().getConfig();

        switch (config.get(path)) { // Default or disabled
            case null -> {
                config.set(path, "");
                return null;
            }

            case Number num -> { // A number!
                double val = num.doubleValue();
                config.set(path, val);

                return val;
            }

            case String str -> { // bad format like -> "3.2"
                if (str.isBlank()) return null;

                double val = Double.parseDouble(str);
                config.set(path, val); // remove quotes

                return val;
            }

            default -> {}
        }

        return null; // fallback.
    }

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
