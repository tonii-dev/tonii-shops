package io.github.toniidev.toniishops.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    /**
     * Creates a new Inventory instance having the same exact properties of the
     * specified inventory. It allows us to edit already existing Inventories
     * and showing them to other players, without editing the real Inventory instance
     *
     * @param inventory The Inventory that must be cloned
     * @param title     The title of the new Inventory
     * @return A new Inventory instance having the same exact properties of the chosen Inventory
     * (except title, that can be the same, but it has to be specified)
     */
    public static Inventory cloneInventory(Inventory inventory, String title) {
        Inventory newInventoryInstance = Bukkit.createInventory(null, inventory.getSize(), title);

        for (int i = 0; i < inventory.getSize(); i++) {
            newInventoryInstance.setItem(i, inventory.getItem(i));
        }

        return newInventoryInstance;
    }
}
