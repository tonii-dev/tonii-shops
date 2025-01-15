package io.github.toniidev.toniishops.classes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GlobalShopItem {
    /**
     * Items of this list are loaded onEnable and
     * are saved onDisable
     * TODO: save and load this list's items
     */
    public static List<GlobalShopItem> globalShopItems = new ArrayList<>();

    private int amountOnTheMarket;

    private final Material material;
    private final double basePrice;
    private final double margin = 10;

    public GlobalShopItem(Material itemMaterial, double price, int amount){
        this.material = itemMaterial;
        this.basePrice = price;
        this.amountOnTheMarket = amount;
    }

    public double getSellPrice(){
        return this.basePrice / (1 + (amountOnTheMarket / 100.0));
    }

    public double getBuyPrice(){
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

    @Nullable
    public static GlobalShopItem getItem(Material material){
        return GlobalShopItem.globalShopItems.stream()
                .filter(x -> x.getMaterial().equals(material))
                .findFirst().orElse(null);
    }

    public static void sellOne(Player player, Material materialToSell){
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        GlobalShopItem item = GlobalShopItem.getItem(materialToSell);
        if(item == null) return;

        serverPlayer.addMoney(item.getSellPrice());
        item.increaseAmount();
    }

    /**
     * Default getter for this class
     * @return The material linked to this GlobalShopItem instance
     */
    public Material getMaterial() {
        return material;
    }
}
