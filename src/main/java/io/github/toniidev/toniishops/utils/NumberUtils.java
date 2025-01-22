package io.github.toniidev.toniishops.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
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

    /**
     * Rounds the specified number to the specified decimal places
     * @param number The double to round
     * @param decimalPlaces The decimal places to round the number to
     * @return The specified double rounded to the specified decimal place
     */
    public static double round(Double number, int decimalPlaces){
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);  // Round to the specified number of decimals
        return bd.doubleValue();
    }

    public static boolean isInteger(String string) {
        if (string == null || string.isEmpty()) return false; // Null or empty check
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String string) {
        if (string == null || string.isEmpty()) return false; // Null or empty check
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
