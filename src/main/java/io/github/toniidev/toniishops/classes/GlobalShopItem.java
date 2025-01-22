package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.commands.SellCustomAmount;
import io.github.toniidev.toniishops.enums.GlobalShopActionType;
import io.github.toniidev.toniishops.enums.ShopItemType;
import io.github.toniidev.toniishops.extendable.GlobalShopAction;
import io.github.toniidev.toniishops.factories.InputFactory;
import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.factories.ItemStackFactory;
import io.github.toniidev.toniishops.factories.StringFactory;
import io.github.toniidev.toniishops.strings.GlobalShopError;
import io.github.toniidev.toniishops.strings.GlobalShopSuccess;
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

        buyHistory.add(new GlobalShopBuy(false, 1, player, this.getBuyPrice()));

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

        sellHistory.add(new GlobalShopSell(false, 1, player, this.getSellPrice()));

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

    public void setAmountOnTheMarket(long amount) {
        this.amountOnTheMarket = amount;
    }

    public List<GlobalShopSell> getSellHistory() {
        return sellHistory;
    }

    public List<GlobalShopBuy> getBuyHistory() {
        return buyHistory;
    }

    public int getPresenceInPlayerInventory(HumanEntity player) {
        int value = 0;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null) {
                if (itemStack.getType().equals(this.getMaterial())) value += itemStack.getAmount();
            }
        }

        return value;
    }

    public Inventory getSpecificItemView(Plugin plugin, HumanEntity player) {
        ItemStackFactory buyHistoryFactory = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&aBuy history"));

        ItemStackFactory sellHistoryFactory = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&6Sell history"));

        if (!buyHistory.isEmpty()) {
            for (int i = 1; i <= (Math.min(buyHistory.size(), 21)); i++) {
                GlobalShopBuy current = buyHistory.get(buyHistory.size() - i);
                buyHistoryFactory.addLoreLine(StringUtils.formatColorCodes('&', "&9[" + (buyHistory.size() - i) + "] " + "&f" + current.getPlayer().getDisplayName() +
                        " &7sold &f" + current.getAmount() + "x &7for &f" + current.getPrice() + "$"));
            }
        } else buyHistoryFactory.addLoreLine(StringUtils.formatColorCodes('&', "&9No recent orders to show."));

        if (!sellHistory.isEmpty()) {
            for (int i = 1; i <= (Math.min(sellHistory.size(), 21)); i++) {
                GlobalShopSell current = sellHistory.get(sellHistory.size() - i);
                sellHistoryFactory.addLoreLine(StringUtils.formatColorCodes('&', "&9[" + (sellHistory.size() - i) + "] " + "&f" + current.getPlayer().getDisplayName() +
                        " &7sold &f" + current.getAmount() + "x &7for &f" + current.getPrice() + "$"));
            }
        } else sellHistoryFactory.addLoreLine(StringUtils.formatColorCodes('&', "&9No recent orders to show."));

        ItemStackFactory sellFactory = new ItemStackFactory(Material.HOPPER)
                .setName(StringUtils.formatColorCodes('&', "&6Sell instantly"))
                .addLoreLine(StringUtils.formatColorCodes('&', "&8/sellone, /sellall"))
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

                .setItem(15, buyHistoryFactory.get())
                .setItem(16, sellHistoryFactory.get())
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

                /// Ok, now we know how to execute commands without the player actually executes them. Perfect.
                /// Must make the SellAll and the SellItem command independent of the Item that the player is holding
                /// in main hand and specify the Item that has to be sold. We should also create a new command, /sell-custom-amount
                /// or something like this. All good.
                /*.setAction(11, e -> {
                    new SellAll().onCommand((CommandSender) e.getWhoClicked(), Bukkit.getPluginCommand("sell-all"), null, args);
                })*/

                .get();
    }

    private Double getCumulativeSellPrice(long amount) {
        double value = 0;
        long prevAmountOnTheMarket = this.getAmountOnTheMarket();

        for (int i = 0; i < amount; i++) {
            value += getSellPrice();
            this.increaseAmount();
        }

        this.setAmountOnTheMarket(prevAmountOnTheMarket);
        return NumberUtils.round(value, 2);
    }

    private Double getCumulativeBuyPrice(long amount) {
        double value = 0;
        long prevAmountOnTheMarket = this.getAmountOnTheMarket();

        for (int i = 0; i < amount; i++) {
            value += getBuyPrice();
            this.amountOnTheMarket--;
        }

        this.setAmountOnTheMarket(prevAmountOnTheMarket);
        return NumberUtils.round(value, 2);
    }

    private Double getFinalPrice(GlobalShopActionType action, long customAmount) {
        double value = 0;
        switch (action) {
            case BUY_ONE, BUY_MULTIPLE -> value = getCumulativeBuyPrice(customAmount);
            case SELL_ONE, SELL_MULTIPLE -> value = getCumulativeSellPrice(customAmount);
        }
        return value;
    }

    private Double getFinalPrice(GlobalShopAction action, long customAmount) {
        return getFinalPrice(action.getType(), customAmount);
    }

    public Inventory getSelectAmountGUI(GlobalShopAction action, Plugin plugin, HumanEntity player) {
        if ((action.getType().equals(GlobalShopActionType.BUY_ONE) || action.getType().equals(GlobalShopActionType.BUY_MULTIPLE)) &&
                action.getAmount() > this.getAmountOnTheMarket())
            return getSelectAmountGUI(action.setAmount(getAmountOnTheMarket())
                    .setPrice(getFinalPrice(action, getAmountOnTheMarket())), plugin, player);
        if (action.getType().equals(GlobalShopActionType.SELL_MULTIPLE) && action.getAmount() > getPresenceInPlayerInventory(player))
            return getSelectAmountGUI(action.setAmount(getAmountOnTheMarket())
                    .setPrice(getFinalPrice(action, getAmountOnTheMarket())), plugin, player);

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
                factory.setAction(16, e -> e.getWhoClicked().openInventory(getConfirmGUI(action, plugin)));
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
                    .setAction(16, e -> e.getWhoClicked().openInventory(getConfirmGUI(action, plugin)));
        }

        return factory.fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ")
                        .get())
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(this.getSpecificItemView(plugin, player))
                .get();
    }

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
                        case SELL_ONE, SELL_MULTIPLE -> new SellCustomAmount().callAsAPlayer(e.getWhoClicked(), action.getAmount());
                        case BUY_ONE, BUY_MULTIPLE -> {
                            ServerPlayer player = ServerPlayer.getPlayer((Player) e.getWhoClicked());
                            assert player != null;

                            if (!player.secureRemoveMoney(action.getPrice())) return;

                            for (int i = 0; i < action.getAmount(); i++) {
                                this.amountOnTheMarket--;
                                e.getWhoClicked().getInventory().addItem(new ItemStack(this.getMaterial()));
                            }
                        }
                    }

                    e.getWhoClicked().closeInventory();
                })
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ")
                        .get())
                .setInventoryToShowOnClose(getSelectAmountGUI(action, plugin, action.getPlayer()))
                .get();
    }

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
