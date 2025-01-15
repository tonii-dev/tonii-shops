package io.github.toniidev.toniishops.utils;

public class IntegerUtils {
    /**
     * Find the minimum amount of rows an Inventory has to be done of based on how many ItemStacks
     * have to be in that Inventory.
     *
     * @param numberOfItemsToPutIntoInventory The number of ItemStacks that have to be put into the Inventory
     * @return The minimum amount of rows the Inventory has to be done of. If too many ItemStacks have to be
     * put into the Inventory, it returns 0. The maximum amount of ItemStacks that can be put in a single
     * Inventory is 53.
     */
    public static int findInventoryRowsToCreate(int numberOfItemsToPutIntoInventory) {
        if (numberOfItemsToPutIntoInventory > 53) return 0;
        else if (numberOfItemsToPutIntoInventory <= 9) return 1;
        else if (numberOfItemsToPutIntoInventory <= 18) return 2;
        else if (numberOfItemsToPutIntoInventory <= 27) return 3;
        else if (numberOfItemsToPutIntoInventory <= 36) return 4;
        else if (numberOfItemsToPutIntoInventory <= 45) return 5;
        else return 6;
    }
}
