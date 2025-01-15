package io.github.toniidev.toniishops.utils;

import io.github.toniidev.toniishops.strings.ConsoleString;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

public class ItemUtils {
    /**
     * Tells whether two ItemStacks have the same name or not
     *
     * @param itemStack1 The first ItemStack to check
     * @param itemStack2 The second ItemStack to check
     * @return true if the two ItemStacks have the same name, false if the two ItemStacks do not have the same name
     */
    public static boolean doItemStacksHaveTheSameName(ItemStack itemStack1, ItemStack itemStack2) {
        if (itemStack1.getItemMeta() == null || itemStack2.getItemMeta() == null) return false;
        return itemStack1.getItemMeta().getDisplayName().equals(itemStack2.getItemMeta().getDisplayName());
    }

    /**
     * Gets the lore of the given ItemStack and search for the lore line
     * where it is displayed the "Serial code". It splits the string
     * and only gets the serial code
     *
     * @param itemStack The ItemStack of which we need to read the lore to get the Serial code
     * @return null if the given ItemStack does not have ItemMeta, does not have lore or does not
     * have a lore line starting with "Serial"
     */
    @Nullable
    public static String isolateSerialCode(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return null;
        if (meta.getLore() == null) return null;
        if (!((long) meta.getLore().size() > 0)) return null;

        String rightString = meta.getLore().stream()
                .filter(x -> x
                        .replace("§7", "")
                        .startsWith("Serial:"))
                .findFirst().orElse(null);

        if (rightString == null) return null;

        return rightString.split(" ")[1]
                .replace("§8", "")
                .replace("§k", "");
    }

    /**
     * Renames an ItemStack
     *
     * @param itemStack The instance of the ItemStack to rename
     * @param name      The new name to assign to the ItemStack
     */
    public static void rename(ItemStack itemStack, String name) {
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            Bukkit.getLogger().warning(ConsoleString.MISSING_ITEM_META.getMessage() + itemStack.getType());
            return;
        }

        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
    }
}
