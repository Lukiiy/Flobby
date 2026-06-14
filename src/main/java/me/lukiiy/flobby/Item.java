package me.lukiiy.flobby;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.lukiiy.flow.FDefaults;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class Item {
    public static ItemStack getHostItem() {
        ItemStack item = ItemStack.of(Material.PAPER);

        item.setData(DataComponentTypes.ITEM_MODEL, Key.key(Key.MINECRAFT_NAMESPACE, "clock"));
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Menu").color(FDefaults.YELLOW));
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.addEnchantment(Enchantment.EFFICIENCY, 1);

        return item;
    }
}
