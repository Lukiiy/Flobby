package me.lukiiy.flobby;

import org.bukkit.configuration.file.FileConfiguration;

public class Utils {
    public static Double loadDouble(String path) {
        FileConfiguration config = Flobby.getInstance().getConfig();

        switch (config.get(path)) { // Default or disabled
            case null -> {
                config.set(path, "");
                return null;
            }

            case Number number -> { // A number!
                double val = number.doubleValue();
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
}
