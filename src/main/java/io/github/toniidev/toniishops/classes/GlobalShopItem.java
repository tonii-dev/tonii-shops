package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.enums.ShopItemType;
import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.factories.ItemStackFactory;
import io.github.toniidev.toniishops.factories.StringFactory;
import io.github.toniidev.toniishops.strings.GlobalShopSuccess;
import io.github.toniidev.toniishops.utils.IntegerUtils;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class GlobalShopItem {
    private long amountOnTheMarket;

    private final Material material;
    private final double basePrice;
    private final ShopItemType shopItemType;

    public HashMap<Player, Map.Entry<Long, Double>> sellHistory = new HashMap<>();
    public HashMap<Player, Map.Entry<Long, Double>> buyHistory = new HashMap<>();

    /**
     * Creates a new GlobalShopItem instance
     *
     * @param itemMaterial The material of the Item linked to this GlobalShopItem instance
     * @param price        The starting price at which this GlobalShopItem is sold
     * @param amount       The starting amount of Items of the specified material that will be sold
     */
    public GlobalShopItem(Material itemMaterial, double price, long amount, ShopItemType type) {
        this.material = itemMaterial;
        this.basePrice = price;
        this.amountOnTheMarket = amount;
        this.shopItemType = type;
    }

    /**
     * Default getter for this class
     *
     * @return The sell price of this GlobalShopItem instance based on how many items are actually being sold
     */
    public double getSellPrice() {
        return IntegerUtils.round(this.basePrice / (1 + (amountOnTheMarket / 100.0)), 2);
    }

    /**
     * Default getter for this class
     *
     * @return The buy price of this GlobalShopItem instance based on how many items are actually being sold
     */
    public double getBuyPrice() {
        double margin = 1.25;
        return IntegerUtils.round(this.getSellPrice() * (margin), 2);
    }

    /**
     * Default getter for this class
     *
     * @return Returns the ShopItemType of the Item linked to this GlobalShopItem instance
     */
    public ShopItemType getShopItemType() {
        return this.shopItemType;
    }

    /**
     * Buys one of the available items linked to this GlobalShopItem instance
     *
     * @param player The player that buys the items
     */
    public void buyOne(Player player) {
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        if (!serverPlayer.secureRemoveMoney(this.getBuyPrice())) return;

        // TODO: Make a method to ensure player gets the Item: if playerInventory is full, items will be sent to stashed, etc...
        player.getInventory().addItem(new ItemStack(this.material));

        buyHistory.put(player, new AbstractMap.SimpleEntry<>(1L, this.getBuyPrice()));

        this.amountOnTheMarket--;
    }

    /**
     * Increases the amount of Items of the Material linked to this GlobalShopItem instance
     * that are currently being sold on the market
     */
    private void increaseAmount() {
        this.amountOnTheMarket++;
    }

    /**
     * Sells one Item from the ItemStack that the specified player has in main hand
     *
     * @param player The player that is selling the items
     */
    public void sellOne(Player player) {
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        serverPlayer.addMoney(this.getSellPrice());

        player.sendMessage(new StringFactory(GlobalShopSuccess.ITEM_SOLD_SUCCESSFULLY.getMessage())
                .append(this.getSellPrice() + "$").setColor('f')
                .get());

        sellHistory.put(player, new AbstractMap.SimpleEntry<>(1L, this.getSellPrice()));

        this.increaseAmount();
    }

    /**
     * Adds the specified number of Items on the Global Server market
     *
     * @param player The player that sold these items
     * @param amount The amount of Items to sell on the shop
     */
    public void sellCustomAmount(Player player, long amount) {
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        double cumulativePrice = 0.0;

        for (long i = 0; i < amount; i++) {
            cumulativePrice += this.getSellPrice();
            serverPlayer.addMoney(this.getSellPrice());
            this.increaseAmount();
        }

        cumulativePrice = IntegerUtils.round(cumulativePrice, 2);
        sellHistory.put(player, new AbstractMap.SimpleEntry<>(1L, cumulativePrice));

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
     *
     * @return The material linked to this GlobalShopItem instance
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Default getter for this class
     *
     * @return The amount of items of the type of this GlobalShopItem that are being currently
     * sold on the market
     */
    public long getAmountOnTheMarket() {
        return this.amountOnTheMarket;
    }

    /**
     * Adds the specified number of items of this type to the market
     *
     * @param amount The amount of items of this type that have to be added to the market
     */
    public void addToTheMarket(long amount) {
        this.amountOnTheMarket += amount;
    }

    public HashMap<Player, Map.Entry<Long, Double>> getSellHistory() {
        return sellHistory;
    }

    public HashMap<Player, Map.Entry<Long, Double>> getBuyHistory() {
        return buyHistory;
    }

    public Inventory getSpecificItemView(Plugin plugin) {
        ItemStackFactory buyFactory = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&aBuy orders"));

        ItemStackFactory sellFactory = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&6Sell orders"));

        // TODO: Fix that only 1 lore line is showing

        List<Map.Entry<Player, Map.Entry<Long, Double>>> buyHistoryList = new ArrayList<>(this.getBuyHistory().entrySet());
        if (!buyHistoryList.isEmpty()) {
            for(int i = 1; i <= (Math.min(buyHistoryList.size(), 6)); i++){
                Map.Entry<Player, Map.Entry<Long, Double>> current = buyHistoryList.get(buyHistoryList.size() - i);
                System.out.println(current.getKey());
                sellFactory.addLoreLine(StringUtils.formatColorCodes('&', "&f" + current.getKey().getDisplayName() +
                        " &7sold &f" + current.getValue().getKey() + "x &7for &f" + current.getValue().getValue()));
            }
        } else buyFactory.addLoreLine(StringUtils.formatColorCodes('&', "&9No recent orders to show."));

        List<Map.Entry<Player, Map.Entry<Long, Double>>> sellHistoryList = new ArrayList<>(this.getSellHistory().entrySet());
        if (!sellHistoryList.isEmpty()) {
            for(int i = 1; i <= (Math.min(sellHistoryList.size(), 6)); i++){
                Map.Entry<Player, Map.Entry<Long, Double>> current = sellHistoryList.get(sellHistoryList.size() - i);
                System.out.println(current.getKey());
                sellFactory.addLoreLine(StringUtils.formatColorCodes('&', "&f" + current.getKey().getDisplayName() +
                        " &7sold &f" + current.getValue().getKey() + "x &7for &f" + current.getValue().getValue()));
            }
        } else sellFactory.addLoreLine(StringUtils.formatColorCodes('&', "&9No recent orders to show."));

        return new InventoryFactory(3, "Details", plugin)
                .setItem(10, new ItemStackFactory(Material.GOLDEN_HORSE_ARMOR)
                        .setName(StringUtils.formatColorCodes('&', "&aBuy instantly"))
                        .addLoreLine("This item will be sent to your inventory")
                        .addLoreLine("if you pay for it")
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "Price per unit: &6" + this.getBuyPrice() + "$"))
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to buy!"))
                        .get())
                .setItem(11, new ItemStackFactory(Material.HOPPER)
                        .setName(StringUtils.formatColorCodes('&', "&6Sell instantly"))
                        .addLoreLine(StringUtils.formatColorCodes('&', "&8/sellone, /sellall"))
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "Price per unit: &6" + this.getSellPrice() + "$"))
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to sell!"))
                        .get())

                .setItem(13, new ItemStackFactory(this.getMaterial())
                        .addLoreLine(this.getSubtitle())
                        .get())

                .setItem(15, buyFactory.get())
                .setItem(16, sellFactory.get())
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ").get())
                .setInventoryToShowOnClose(GlobalShop.getGUI(this.getShopItemType(), plugin))
                .get();
    }

    public String getSubtitle(){
        String value;

        switch (this.shopItemType){
            case ITEM -> value = StringUtils.formatColorCodes('&', "&8Item");
            case FOOD -> value = StringUtils.formatColorCodes('&', "&8Food");
            case DECORATIVE -> value = StringUtils.formatColorCodes('&', "&8Decoration");
            case BLOCK -> value = StringUtils.formatColorCodes('&', "&8Block");
            case ORE -> value = StringUtils.formatColorCodes('&', "&8Ore");
            case null, default -> value = StringUtils.formatColorCodes('&', "&8null");
        }

        return value;
    }
}
