package io.github.toniidev.toniishops.enums;

import io.github.toniidev.toniishops.factories.ItemStackFactory;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ShopItemType {
    BLOCK(new ItemStackFactory(Material.GRASS_BLOCK)
            .setName(StringUtils.formatColorCodes('&', "&e&lBuilding blocks"))
            .addLoreLine("Most common building blocks")
            .get()),
    ITEM(new ItemStackFactory(Material.FEATHER)
            .setName(StringUtils.formatColorCodes('&', "&b&lItems"))
            .addLoreLine("Most common items")
            .get()),
    ORE(new ItemStackFactory(Material.DIAMOND)
            .setName(StringUtils.formatColorCodes('&', "&9&lOres"))
            .addLoreLine("All the main ores")
            .get()),
    FOOD(new ItemStackFactory(Material.COOKED_MUTTON)
            .setName(StringUtils.formatColorCodes('&', "&e&lFood"))
            .addLoreLine("All the foods")
            .get()),
    DECORATIVE(new ItemStackFactory(Material.TORCHFLOWER)
            .setName(StringUtils.formatColorCodes('&', "&6&lDecoration"))
            .addLoreLine("Main decorational blocks")
            .get());

    private ItemStack icon;

    ShopItemType(ItemStack stack){
        this.icon = stack;
    }

    public ItemStack getIcon(){
        return this.icon;
    }
}
