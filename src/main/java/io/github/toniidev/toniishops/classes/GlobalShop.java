package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * WARNING: This class contains some strings that aren't stored in any separate enum!
 */
public class GlobalShop {
    /**
     * Items of this list are loaded onEnable and
     * are saved onDisable
     * TODO: Save and load this list's items
     */
    public static List<GlobalShopItem> shop = new ArrayList<>();

    /**
     * Tells whether shop is selling the specified material or not
     *
     * @param material The material we need to know whether the shop is selling or not
     * @return true if the Material is actually being sold by the Global Shop, false if
     * it isn't
     */
    public static boolean contains(Material material) {
        return getItem(material) != null;
    }

    /**
     * Gets the GlobalShopItem instance linked to the specified material
     *
     * @param material The material we need the GlobalShopItem instance of
     * @return The GlobalShopItem linked to the specified material if it exists,
     * null if it doesn't exist
     */
    @Nullable
    public static GlobalShopItem getItem(Material material) {
        return GlobalShop.shop.stream()
                .filter(x -> x.getMaterial().equals(material))
                .findFirst().orElse(null);
    }

    /**
     * Initializes the shop by adding 200 items of all missing materials to it.
     * TODO: Add ores starting price
     *
     * @param amountOfItemsOfEachMaterialToAdd The exact amount of items of each missing material to add to the market
     */
    public static void initializeShop(long amountOfItemsOfEachMaterialToAdd) {
        for (Material material : Material.values()) {
            if (!GlobalShop.contains(material)) {
                double price = 200.0;
                if (ItemUtils.isToolMaterial(material)) {
                    price = GlobalShop.getToolPrice(material);
                }

                GlobalShopItem item = new GlobalShopItem(material, price, amountOfItemsOfEachMaterialToAdd);
                GlobalShop.shop.add(item);
                Bukkit.getLogger().info("Successfully added " + amountOfItemsOfEachMaterialToAdd + " items of " + material +
                        " to the global shop.");
            }
        }
    }

    /**
     * WARNING: This method should only be used to get the price of Materials that are 100% tools!
     * Gets the default price for the specified tool based on the material
     * Wood tools have a starting price of 200,
     * Stone tools have a starting price of 400,
     * Iron tools have a starting price of 600,
     * Diamond tools have a starting price of 800,
     * Netherite tools have a starting price of 1000.
     *
     * @param material The material of the tool that we have to get the default price of
     * @return The default price for the specified tool material
     */
    private static double getToolPrice(Material material) {
        final Map<String, Double> toolPrices = Map.of(
                "wood", 200.0,
                "stone", 400.0,
                "iron", 600.0,
                "diamond", 800.0,
                "netherite", 900.0
        );

        String materialName = material.name().toLowerCase();
        double value = 0.0;

        for (Map.Entry<String, Double> entry : toolPrices.entrySet()) {
            if (materialName.contains(entry.getKey())) {
                value = entry.getValue();
            }
        }

        return value;
    }
}
