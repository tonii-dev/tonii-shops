package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.factories.StringFactory;
import io.github.toniidev.toniishops.strings.GlobalShopSuccess;
import io.github.toniidev.toniishops.utils.IntegerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class GlobalShopItem {
    /**
     * Items of this list are loaded onEnable and
     * are saved onDisable
     * TODO: save and load this list's items
     */
    private long amountOnTheMarket;

    private final Material material;
    private final double basePrice;

    public GlobalShopItem(Material itemMaterial, double price, long amount){
        this.material = itemMaterial;
        this.basePrice = price;
        this.amountOnTheMarket = amount;
    }

    private static boolean isTool(Material material){
        return material.name().endsWith("_SWORD") || material.name().endsWith("_PICKAXE") ||
                material.name().endsWith("_AXE") || material.name().endsWith("_SHOVEL") ||
                material.name().endsWith("_HOE");
    }

    public double getSellPrice(){
        return IntegerUtils.round(this.basePrice / (1 + (amountOnTheMarket / 100.0)), 2);
    }

    public double getBuyPrice(){
        double margin = 10;
        return this.getSellPrice() * (1 + margin);
    }

    public void buyOne(Player player){
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        if(!serverPlayer.secureRemoveMoney(this.getBuyPrice())) return;

        // TODO: Make a method to ensure player gets the Item: if playerInventory is full, items will be sent to stashed, etc...
        player.getInventory().addItem(new ItemStack(this.material));

        this.amountOnTheMarket--;
    }

    public void increaseAmount(){
        this.amountOnTheMarket++;
    }

    public static void sellOne(Player player, Material materialToSell){
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        GlobalShopItem item = GlobalShop.getItem(materialToSell);
        if(item == null) return;

        serverPlayer.addMoney(item.getSellPrice());
        item.increaseAmount();
    }

    public void sellOne(Player player){
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        serverPlayer.addMoney(this.getSellPrice());

        player.sendMessage(new StringFactory(GlobalShopSuccess.ITEM_SOLD_SUCCESSFULLY.getMessage())
                .append(this.getSellPrice() + "$").setColor('f')
                .get());

        this.increaseAmount();
    }

    /**
     * Sells the specified number
     * @param player
     * @param amount
     */
    public void sellCustomAmount(Player player, long amount){
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        double cumulativePrice = 0.0;

        for(long i = 0; i < amount; i++){
            cumulativePrice += this.getSellPrice();
            serverPlayer.addMoney(this.getSellPrice());
            this.increaseAmount();
        }

        cumulativePrice = IntegerUtils.round(cumulativePrice, 2);

        player.sendMessage(new StringFactory()
                .append("[Server]").setColor('a')
                .append("Global shop:").setColor('e')
                .append("Sold").setColor('7')
                .append(String.valueOf(amount)).setColor('f')
                .append(material.name().toLowerCase(Locale.ROOT).replace("_", " "))
                .append("for").setColor('7')
                .append(cumulativePrice + "$").setColor('f')
                .get());
    }

    /**
     * Default getter for this class
     * @return The material linked to this GlobalShopItem instance
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Default getter for this class
     * @return The amount of items of the type of this GlobalShopItem that are being currently
     * sold on the market
     */
    public long getAmountOnTheMarket(){
        return this.amountOnTheMarket;
    }

    /**
     * Adds the specified number of items of this type to the market
     * @param amount The amount of items of this type that have to be added to the market
     */
    public void addToTheMarket(long amount){
        this.amountOnTheMarket += amount;
    }
}
