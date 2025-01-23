package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.commands.Buy;
import io.github.toniidev.toniishops.commands.SellCustomAmount;
import io.github.toniidev.toniishops.enums.GlobalShopActionType;
import io.github.toniidev.toniishops.enums.ShopItemType;
import io.github.toniidev.toniishops.extendable.GlobalShopAction;
import io.github.toniidev.toniishops.factories.*;
import io.github.toniidev.toniishops.strings.GlobalShopError;
import io.github.toniidev.toniishops.utils.NumberUtils;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
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

    public final List<GlobalShopBuy> buyHistory = new ArrayList<>();
    public final List<GlobalShopSell> sellHistory = new ArrayList<>();

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
     * Converts all the GlobalShopBuy items contained in this.buyHistory into a List of ItemStacks
     *
     * @return A List of ItemStacks, each of which is linked to a GlobalShopBuy. Every ItemStack
     * has this.material as Material and other info in its lore
     */
    public List<ItemStack> getBuyHistory() {
        List<ItemStack> value = new ArrayList<>();
        for (GlobalShopBuy buy : buyHistory) {
            value.add(new ItemStackFactory(new ItemStack(this.getMaterial(), Math.min(64, (int) buy.getAmount())))
                    .setName(StringUtils.formatColorCodes('&', "&aItem buy"))
                    .addLoreLine("Informations about this action")
                    .addBlankLoreLine()
                    .addLoreLine(StringUtils.formatColorCodes('&', "Player: &f" + buy.getPlayer().getDisplayName()))
                    .addLoreLine(StringUtils.formatColorCodes('&', "Price: &f" + buy.getPrice() + "$"))
                    .addLoreLine(StringUtils.formatColorCodes('&', "Amount: &f" + buy.getAmount() + "x"))
                    .get());
        }

        return value;
    }

    /**
     * Converts all the GlobalShopSell items contained in this.sellHistory into a List of ItemStacks
     *
     * @return A List of ItemStacks, each of which is linked to a GlobalShopSell. Every ItemStack
     * has this.material as Material and other info in its lore
     */
    public List<ItemStack> getSellHistory() {
        List<ItemStack> value = new ArrayList<>();
        for (GlobalShopSell sell : sellHistory) {
            value.add(new ItemStackFactory(new ItemStack(this.getMaterial(), Math.min(64, (int) sell.getAmount())))
                    .setName(StringUtils.formatColorCodes('&', "&6Item sell"))
                    .addLoreLine("Informations about this action")
                    .addBlankLoreLine()
                    .addLoreLine(StringUtils.formatColorCodes('&', "Player: &f" + sell.getPlayer().getDisplayName()))
                    .addLoreLine(StringUtils.formatColorCodes('&', "Price: &f" + sell.getPrice() + "$"))
                    .addLoreLine(StringUtils.formatColorCodes('&', "Amount: &f" + sell.getAmount() + "x"))
                    .get());
        }

        return value;
    }

    /**
     * Default getter for this class
     *
     * @return The sell price of this GlobalShopItem instance based on how many items are actually being sold
     */
    public double getSellPrice() {
        return NumberUtils.round(this.basePrice / (1 + (amountOnTheMarket / 100.0)), 2);
    }

    /**
     * Default getter for this class
     *
     * @return The buy price of this GlobalShopItem instance based on how many items are actually being sold
     */
    public double getBuyPrice() {
        double margin = 1.25;
        return NumberUtils.round(this.getSellPrice() * (margin), 2);
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
     * Increases the amount of Items of the Material linked to this GlobalShopItem instance
     * that are currently being sold on the market
     */
    public void increaseAmount() {
        this.amountOnTheMarket++;
    }

    /**
     * Decreases the amount of Items of the Material linked to this GlobalShopItem instance
     * that are currently being sold on the market
     */
    public void decreaseAmount() {
        this.amountOnTheMarket--;
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

        cumulativePrice = NumberUtils.round(cumulativePrice, 2);
        sellHistory.add(new GlobalShopSell(true, amount, player, cumulativePrice));

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
     * Removes the specified number of Items from the Global Server market
     *
     * @param player The player that bought these items
     * @param amount The amount of Items to buy from the shop
     */
    public void buyCustomAmount(Player player, long amount) {
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        double cumulativePrice = 0.0;

        for (long i = 0; i < amount; i++) {
            cumulativePrice += this.getBuyPrice();
            serverPlayer.removeMoney(this.getBuyPrice());
            this.decreaseAmount();
        }

        cumulativePrice = NumberUtils.round(cumulativePrice, 2);
        buyHistory.add(new GlobalShopBuy(true, amount, player, cumulativePrice));

        player.sendMessage(new StringFactory()
                .append("[Server]").setColor('a')
                .append("Global shop:").setColor('e')
                .append("Bought").setColor('7')
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
     * Default setter for this class
     * @param amount The amount to set the amount of items of this GlobalShopItem to
     */
    public void setAmountOnTheMarket(long amount) {
        this.amountOnTheMarket = amount;
    }

    /**
     * Searches the specified player's inventory to get how many items of this type are contained into his Inventory
     * @param player The player to search the Inventory of
     * @return The exact amount of items of this type that are contained into the specified player's inventory
     */
    public int getPresenceInPlayerInventory(HumanEntity player) {
        int value = 0;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null) {
                if (itemStack.getType().equals(this.getMaterial())) value += itemStack.getAmount();
            }
        }

        return value;
    }

    /**
     * Gets the view where the specified player can interact with the Item.
     * He can see the buy and sell history of the item and can choose to open buy and sell Inventories
     * @param plugin The main plugin instance
     * @param player The player that should see this Inventory
     * @return The Inventory linked to a SpecifiedItem, in which the player can interact with the Item
     */
    public Inventory getSpecificItemView(Plugin plugin, HumanEntity player) {
        ItemStackFactory sellFactory = new ItemStackFactory(Material.HOPPER)
                .setName(StringUtils.formatColorCodes('&', "&6Sell instantly"))
                .addLoreLine(StringUtils.formatColorCodes('&', "&8/sellone, /sellall, /sellc <amount>"))
                .addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', "Price per unit: &6" + this.getSellPrice() + "$"))
                .addLoreLine(StringUtils.formatColorCodes('&', "Inventory: " + (getPresenceInPlayerInventory(player) == 0 ? "&cNone" : "&6" + getPresenceInPlayerInventory(player) + "&7x")));

        if (getPresenceInPlayerInventory(player) != 0) {
            sellFactory.addBlankLoreLine()
                    .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to select amount!"));
        }

        return new InventoryFactory(3, "Details", plugin)
                .setItem(10, new ItemStackFactory(Material.GOLD_INGOT)
                        .setName(StringUtils.formatColorCodes('&', "&aBuy instantly"))
                        .addLoreLine("This item will be sent to your")
                        .addLoreLine("inventory if you pay for it")
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "Price per unit: &6" + this.getBuyPrice() + "$"))
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to select amount!"))
                        .get())
                .setItem(11, sellFactory.get())

                .setItem(13, new ItemStackFactory(this.getMaterial())
                        .addLoreLine(this.getSubtitle())
                        .get())

                .setItem(15, new ItemStackFactory(Material.FILLED_MAP)
                        .setName(StringUtils.formatColorCodes('&', "&aBuy history"))
                        .addLoreLine(StringUtils.formatColorCodes('&', (buyHistory.isEmpty() ? "&9No recent orders to show." : "&9Click to browse!")))
                        .get())
                .setItem(16, new ItemStackFactory(Material.FILLED_MAP)
                        .setName(StringUtils.formatColorCodes('&', "&6Sell history"))
                        .addLoreLine(StringUtils.formatColorCodes('&', (sellHistory.isEmpty() ? "&9No recent orders to show." : "&9Click to browse!")))
                        .get())
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ").get())
                .setInventoryToShowOnClose(GlobalShop.getGUI(this.getShopItemType(), plugin))
                .setClicksAllowed(false)

                .setAction(10, e -> e.getWhoClicked().openInventory(getSelectAmountGUI(new GlobalShopBuy(false, 1, (Player) e.getWhoClicked(),
                        getFinalPrice(GlobalShopActionType.BUY_ONE, 1)), plugin, e.getWhoClicked())))
                .setAction(11, e -> {
                    if (getPresenceInPlayerInventory(e.getWhoClicked()) != 0) {
                        e.getWhoClicked().openInventory(getSelectAmountGUI(new GlobalShopSell(false, 1, (Player) e.getWhoClicked(),
                                getFinalPrice(GlobalShopActionType.SELL_ONE, 1)), plugin, e.getWhoClicked()));
                    }
                })
                .setAction(15, e -> {
                    InventoryFactory historyFactory = new InventoryFactory(6, "History", plugin)
                            .setClicksAllowed(false)
                            .setInventoryToShowOnClose(getSpecificItemView(plugin, player));

                    if (!this.getBuyHistory().isEmpty()) {
                        e.getWhoClicked().openInventory(new MultipleInventoryFactory(this.getBuyHistory(), plugin, historyFactory)
                                .get());
                    }
                })
                .setAction(16, e -> {
                    InventoryFactory historyFactory = new InventoryFactory(6, "History", plugin)
                            .setClicksAllowed(false)
                            .setInventoryToShowOnClose(getSpecificItemView(plugin, player));

                    if (!this.getSellHistory().isEmpty()) {
                        e.getWhoClicked().openInventory(new MultipleInventoryFactory(this.getSellHistory(), plugin, historyFactory)
                                .get());
                    }
                })

                .get();
    }

    /**
     * Gets the price that someone receives if he tries to sell the specified amount of items
     * @param amount The amount based on which the price should be calculated
     * @return The price that someone would receive if he tried to sell the specified amount of items of this GlobalShopItem
     */
    public Double getCumulativeSellPrice(long amount) {
        double value = 0;
        long prevAmountOnTheMarket = this.getAmountOnTheMarket();

        for (int i = 0; i < amount; i++) {
            value += getSellPrice();
            this.increaseAmount();
        }

        this.setAmountOnTheMarket(prevAmountOnTheMarket);
        return NumberUtils.round(value, 2);
    }

    /**
     * Gets the price that should be paid if someone tries to buy the specified amount of items
     * @param amount The amount based on which the price should be calculated
     * @return The price that should be paid if someone should buy the specified amount of items of this GlobalShopItem
     */
    public Double getCumulativeBuyPrice(long amount) {
        double value = 0;
        long prevAmountOnTheMarket = this.getAmountOnTheMarket();

        for (int i = 0; i < amount; i++) {
            value += getBuyPrice();
            this.amountOnTheMarket--;
        }

        this.setAmountOnTheMarket(prevAmountOnTheMarket);
        return NumberUtils.round(value, 2);
    }

    /**
     * Calculates the money that the specified action should give/take
     * @param action The action type of which we want to calculate the price
     * @param customAmount The amount of items that the action involves
     * @return The final price of the action
     */
    private Double getFinalPrice(GlobalShopActionType action, long customAmount) {
        double value = 0;
        switch (action) {
            case BUY_ONE, BUY_MULTIPLE -> value = getCumulativeBuyPrice(customAmount);
            case SELL_ONE, SELL_MULTIPLE -> value = getCumulativeSellPrice(customAmount);
        }
        return value;
    }

    /**
     * Calculates the money that the specified action should give/take
     * @param action The action of which we want to calculate the price
     * @param customAmount The amount of items that the action involves
     * @return The final price of the action
     */
    private Double getFinalPrice(GlobalShopAction action, long customAmount) {
        return getFinalPrice(action.getType(), customAmount);
    }

    /**
     * Gets the Inventory where players can decide how many items buy or sell
     * @param action The GlobalShopAction to handle. If it's a buy action the Inventory
     *               will have certain aspects, if it's a sell action the Inventory will have
     *               other aspects
     * @param plugin The main plugin instance
     * @param player The Player that has to choose the amount of items that he wants to work with
     * @return The Inventory in which the specified player can choose the amount of items to interact with
     */
    public Inventory getSelectAmountGUI(GlobalShopAction action, Plugin plugin, HumanEntity player) {
        if ((action.getType().equals(GlobalShopActionType.BUY_ONE) || action.getType().equals(GlobalShopActionType.BUY_MULTIPLE)) &&
                action.getAmount() > this.getAmountOnTheMarket())
            return getSelectAmountGUI(action.setAmount(getAmountOnTheMarket())
                    .setPrice(getFinalPrice(action, getAmountOnTheMarket())), plugin, player);
        if (action.getType().equals(GlobalShopActionType.SELL_MULTIPLE) && action.getAmount() > getPresenceInPlayerInventory(player))
            return getSelectAmountGUI(action.setAmount(getPresenceInPlayerInventory(player))
                    .setPrice(getFinalPrice(action, getPresenceInPlayerInventory(player))), plugin, player);

        ItemStack buy = new ItemStackFactory(Material.LIME_STAINED_GLASS_PANE)
                .setName(StringUtils.formatColorCodes('&', "&aBuy"))
                .addLoreLine("Buy the desidered amount of items")
                .addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', "&bSelected amount: &e" + action.getAmount() + "&bx"))
                .addLoreLine(StringUtils.formatColorCodes('&', "&bPrice: &e" + action.getPrice() + "$"))
                .get();

        ItemStack sell = new ItemStackFactory(Material.YELLOW_STAINED_GLASS_PANE)
                .setName(StringUtils.formatColorCodes('&', "&6Sell"))
                .addLoreLine("Sell the desidered amount of items")
                .addBlankLoreLine()
                .addLoreLine(StringUtils.formatColorCodes('&', "&bSelected amount: &e" + action.getAmount() + "&bx"))
                .addLoreLine(StringUtils.formatColorCodes('&', "&bPrice: &e" + action.getPrice() + "$"))
                .get();

        InventoryFactory factory = new InventoryFactory(3, " ", plugin)
                .setAction(14, e -> {
                    e.getWhoClicked().closeInventory();
                    new InputFactory((Player) e.getWhoClicked(), plugin)
                            .setAction(e1 -> {
                                if (!NumberUtils.isInteger(e1.getMessage())) {
                                    e1.getPlayer().sendMessage(GlobalShopError.INVALID_AMOUNT.getMessage());
                                    e1.getPlayer().openInventory(getSelectAmountGUI(action, plugin, player));
                                    return;
                                }
                                if (Integer.parseInt(e1.getMessage()) > getAmountOnTheMarket()) {
                                    e1.getPlayer().sendMessage(GlobalShopError.NOT_ENOUGH_ITEMS_SELLING.getMessage());
                                    e1.getPlayer().openInventory(getSelectAmountGUI(action, plugin, player));
                                    return;
                                }

                                e1.getPlayer().openInventory(getSelectAmountGUI(action.setAmount(Integer.parseInt(e1.getMessage()))
                                        .setPrice(getFinalPrice(action, Integer.parseInt(e1.getMessage()))), plugin, player));
                            });
                });

        if (action instanceof GlobalShopBuy) {
            factory.setTitle("Buy items")
                    .setItem(10, new ItemStackFactory(this.getMaterial())
                            .setName(StringUtils.formatColorCodes('&', "&aBuy only &eone&a!"))
                            .addLoreLine(this.getSubtitle())
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "Price: &6" + this.getFinalPrice(action, 1) + "$"))
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to select this amount!"))
                            .get())
                    .setItem(12, new ItemStackFactory(new ItemStack(this.getMaterial(), 64))
                            .setName(StringUtils.formatColorCodes('&', "&aBuy a stack!"))
                            .addLoreLine(this.getSubtitle())
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "Per unit: &6" + this.getFinalPrice(action, 1) + "$"))
                            .addLoreLine(StringUtils.formatColorCodes('&', "Price: &6" + this.getFinalPrice(action, 64) + "$"))
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to select this amount!"))
                            .get())
                    .setItem(14, new ItemStackFactory(Material.OAK_SIGN)
                            .setName(StringUtils.formatColorCodes('&', "&aCustom amount"))
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "Buy up to &a" + getAmountOnTheMarket() + "&7x"))
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to specify!"))
                            .get())
                    .setItem(16, buy)

                    .setAction(10, e -> e.getWhoClicked().openInventory(this.getSelectAmountGUI
                            (action.setAmount(1)
                                    .setPrice(getFinalPrice(action, 1)), plugin, player)))
                    .setAction(12, e -> e.getWhoClicked().openInventory(this.getSelectAmountGUI
                            (action.setAmount(64)
                                    .setPrice(getFinalPrice(action, 64)), plugin, player)));

            if (getAmountOnTheMarket() > 0) {
                factory.setAction(16, e -> e.getWhoClicked().openInventory(getConfirmGUI(action
                        .setPrice(getFinalPrice(action, action.getAmount())), plugin)));
            }
        }
        if (action instanceof GlobalShopSell) {
            factory.setTitle("Sell items")
                    .setItem(10, new ItemStackFactory(this.getMaterial())
                            .setName(StringUtils.formatColorCodes('&', "&aSell only &eone&a!"))
                            .addLoreLine(this.getSubtitle())
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "Price: &6" + this.getFinalPrice(action, 1) + "$"))
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to select this amount!"))
                            .get())
                    .setItem(11, new ItemStackFactory(new ItemStack(this.getMaterial(), 64))
                            .setName(StringUtils.formatColorCodes('&', "&aSell a stack!"))
                            .addLoreLine(this.getSubtitle())
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "Per unit: &6" + this.getFinalPrice(action, 1) + "$"))
                            .addLoreLine(StringUtils.formatColorCodes('&', "Price: &6" + this.getFinalPrice(action, 64) + "$"))
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to select this amount!"))
                            .get())
                    .setItem(12, new ItemStackFactory(new ItemStack(this.getMaterial(), Math.min(64, getPresenceInPlayerInventory(player))))
                            .setName(StringUtils.formatColorCodes('&', "&aSell all!"))
                            .addLoreLine(this.getSubtitle())
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "Amount: &6" + getPresenceInPlayerInventory(player) + "&7x"))
                            .addLoreLine(StringUtils.formatColorCodes('&', "Per unit: &6" + this.getFinalPrice(action, 1) + "$"))
                            .addLoreLine(StringUtils.formatColorCodes('&', "Price: &6" + this.getFinalPrice(action, getPresenceInPlayerInventory(player)) + "$"))
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to select this amount!"))
                            .get())
                    .setItem(14, new ItemStackFactory(Material.OAK_SIGN)
                            .setName(StringUtils.formatColorCodes('&', "&aCustom amount"))
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "Sell up to &a" + getPresenceInPlayerInventory(player) + "&7x"))
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to specify!"))
                            .get())
                    .setItem(16, sell)

                    .setAction(10, e -> e.getWhoClicked().openInventory(this.getSelectAmountGUI
                            (action.setAmount(1)
                                    .setPrice(getFinalPrice(action, 1)), plugin, player)))
                    .setAction(11, e -> e.getWhoClicked().openInventory(this.getSelectAmountGUI
                            (action.setAmount(64)
                                    .setPrice(getFinalPrice(action, 64)), plugin, player)))
                    .setAction(12, e -> e.getWhoClicked().openInventory(this.getSelectAmountGUI
                            (action.setAmount(getPresenceInPlayerInventory(player))
                                    .setPrice(getFinalPrice(action, getPresenceInPlayerInventory(player))), plugin, player)))
                    .setAction(16, e -> e.getWhoClicked().openInventory(getConfirmGUI(action
                            .setPrice(getFinalPrice(action, action.getAmount())), plugin)));
        }

        return factory.fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ")
                        .get())
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(this.getSpecificItemView(plugin, player))
                .get();
    }

    /**
     * Gets the GUI where a certain action is confirmed
     * @param action The action that has to be confirmed
     * @param plugin The main plugin instance
     * @return An Inventory where a certain player can confirm the specified action
     */
    public Inventory getConfirmGUI(GlobalShopAction action, Plugin plugin) {
        String word = (action.getType().equals(GlobalShopActionType.BUY_ONE) || action.getType().equals(GlobalShopActionType.BUY_MULTIPLE)) ?
                "buy" : "sell";

        return new InventoryFactory(3, "Confirm", plugin)
                .setClicksAllowed(false)
                .setItem(13, new ItemStackFactory(new ItemStack(this.getMaterial(), Math.min(64, (int) action.getAmount())))
                        .setName(StringUtils.formatColorCodes('&', "&aCustom amount"))
                        .addLoreLine(this.getSubtitle())
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "Amount: &6" + action.getAmount() + "&7x"))
                        .addLoreLine(StringUtils.formatColorCodes('&', "Price: &6" + action.getPrice() + "$"))
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to " + word))
                        .get())
                .setAction(13, e -> {
                    switch (action.getType()) {
                        case SELL_ONE, SELL_MULTIPLE -> {
                            new SellCustomAmount(plugin).call(e.getWhoClicked(), getMaterial(), action.getAmount());
                            this.sellCustomAmount(action.getPlayer(), action.getAmount());

                            /// Remove sold items
                            /// We must remove from the player Inventory <amount> of Items that have the type <material>
                            int remainingToRemove = (int) action.getAmount();

                            for (ItemStack itemStack : action.getPlayer().getInventory().getContents()) {
                                if (itemStack == null || !itemStack.getType().equals(material)) continue;

                                int stackAmount = itemStack.getAmount();

                                if (stackAmount > remainingToRemove) {
                                    /// Reduce the stack size and finish removal
                                    itemStack.setAmount(stackAmount - remainingToRemove);
                                    break;
                                } else {
                                    /// Remove the entire stack and continue
                                    remainingToRemove -= stackAmount;
                                    action.getPlayer().getInventory().removeItem(itemStack);
                                }

                                if (remainingToRemove <= 0) break;
                            }
                        }
                        case BUY_ONE, BUY_MULTIPLE -> {
                            new Buy(plugin).callAsAPlayer(action.getPlayer(), getMaterial().name(), action.getAmount());
                            this.buyCustomAmount(action.getPlayer(), action.getAmount());

                            /// Securely add the bought items to the player's inventory
                            ServerPlayer player = ServerPlayer.getPlayer(action.getPlayer());
                            assert player != null;
                            for (int i = 0; i < action.getAmount(); i++) {
                                player.addItemToInventory(new ItemStack(this.getMaterial()));
                            }

                            action.getPlayer().closeInventory();
                        }
                    }

                    e.getWhoClicked().closeInventory();
                })
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ")
                        .get())
                .setInventoryToShowOnClose(getSelectAmountGUI(action.setPrice(getFinalPrice(action, action.getAmount())), plugin, action.getPlayer()))
                .get();
    }

    /**
     * Gets a string, that contains this GlobalShopItem type, that can be displayed under the object as a lore line
     * @return The subtitle to show under the ItemStack of this GlobalShopItem type
     */
    public String getSubtitle() {
        String value;

        switch (this.shopItemType) {
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
