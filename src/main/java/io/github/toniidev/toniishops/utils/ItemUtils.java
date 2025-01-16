package io.github.toniidev.toniishops.utils;

import io.github.toniidev.toniishops.enums.ShopItemType;
import io.github.toniidev.toniishops.strings.ConsoleString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    /**
     * Returns the ShopItemType of the specified material
     * @param material The material we need the ShopItemType of
     * @return ShopItemType.TOOL if the given material is a tool,
     * ShopItemType.ORE_BLOCK if the given material is an ore block,
     * ShopItemType.ORE if the given material is an ore,
     * ShopItemType.ITEM if the given material is an item
     * ShopItemType.FOOD if the given material is a food
     * ShopItemType.DECORATIVE if the given material is a decorative block
     * ShopItemType.BLOCK if it's a block
     * null if the Material is invalid
     */
    @Nullable
    public static ShopItemType classify(Material material){
        if(ItemUtils.isTool(material)) return ShopItemType.TOOL;
        else if (ItemUtils.isOreBlock(material)) return ShopItemType.ORE_BLOCK;
        else if (ItemUtils.isOre(material)) return ShopItemType.ORE;
        else if (ItemUtils.isItem(material)) return ShopItemType.ITEM;
        else if (ItemUtils.isFood(material)) return ShopItemType.FOOD;
        else if (ItemUtils.isDecorative(material)) return ShopItemType.DECORATIVE;
        else if (ItemUtils.isBlock(material)) return ShopItemType.BLOCK;
        return null;
    }

    /**
     * Tells whether the given material is a tool or not
     * @param material The material to check whether is a tool or not
     * @return true if the given material is a tool, false if it's not
     */
    public static boolean isTool(Material material){
        /// Check if the material is a tool by its name (e.g., sword, pickaxe)
        return material.name().endsWith("_SWORD") || material.name().endsWith("_PICKAXE") ||
                material.name().endsWith("_AXE") || material.name().endsWith("_SHOVEL") ||
                material.name().endsWith("_HOE");
    }

    /**
     * Tells whether the given material is a block or not
     * @param material The material to check whether is a block or not
     * @return true if the given material is a block, false if it's not
     */
    public static boolean isBlock(Material material) {
        return material.isBlock();
    }

    /**
     * Tells whether the given material is an item or not
     * @param material The material to check whether is an item or not
     * @return true if the given material is an item, false if it's not
     */
    private static boolean isItem(Material material) {
        /// All materials that are not blocks, ores, or tools are considered items
        return material.isItem() && !isTool(material) && !isBlock(material) && !isOre(material);
    }

    /**
     * Tells whether the given material is an ore or not
     * @param material The material to check whether is an ore or not
     * @return true if the given material is an ore, false if it's not
     */
    private static boolean isOre(Material material) {
        /// Check if the material is an ore (e.g., DIAMOND_ORE, IRON_ORE)
        return material.name().endsWith("_ORE");
    }

    /**
     * Tells whether the given material is an ore or not
     * @param material The material to check whether is an ore block or not
     * @return true if the given material is an ore block, false if it's not
     */
    private static boolean isOreBlock(Material material){
        /// Check if the material is an ore block (e.g., DIAMOND_BLOCK, IRON_BLOCK)
        return material.name().endsWith("_BLOCK") && (material.name().contains("DIAMOND") || material.name().contains("IRON") ||
                material.name().contains("GOLD") || material.name().contains("EMERALD"));
    }

    /**
     * Tells whether the given material is a food or not
     * @param material The material to check whether is a food or not
     * @return true if the given material is a food, false if it's not
     */
    private static boolean isFood(Material material) {
        /// Check if the material is a food item (e.g., APPLE, COOKED_BEEF)
        return material.name().endsWith("_APPLE") || material.name().endsWith("_COOKED") ||
                material == Material.COOKED_BEEF || material == Material.BREAD;
    }

    /**
     * Tells whether the given material is a decorative block or not
     * @param material The material to check whether is a decorative block or not
     * @return true if the given material is a decorative block, false if it's not
     */
    private static boolean isDecorative(Material material) {
        /// Check for decorative blocks (e.g., flowers, banners, etc.)
        return material.name().contains("FLOWER") || material.name().contains("BANNER");
    }
}
