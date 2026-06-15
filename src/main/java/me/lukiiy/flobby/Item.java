package me.lukiiy.flobby;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.lukiiy.flow.FDefaults;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class Item {
    private static final NamespacedKey KEY = new NamespacedKey(Flobby.getInstance(), "item");

    public static final ItemStack hostItem = create(Material.PAPER, i -> {
        i.setData(DataComponentTypes.ITEM_MODEL, Key.key(Key.MINECRAFT_NAMESPACE, "clock"));
        i.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        i.setData(DataComponentTypes.ITEM_NAME, Component.text("Menu").color(FDefaults.YELLOW));
        i.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.addEnchantment(Enchantment.EFFICIENCY, 1);
    });

    private static ItemStack create(Material material, Consumer<ItemStack> builder) {
        ItemStack item = ItemStack.of(material);

        builder.accept(item);
        item.editPersistentDataContainer(container -> container.set(KEY, PersistentDataType.BOOLEAN, true));

        return item;
    }
}
