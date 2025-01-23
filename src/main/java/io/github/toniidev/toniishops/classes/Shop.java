package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.factories.ItemStackFactory;
import io.github.toniidev.toniishops.utils.ItemUtils;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Shop {
    /**
     * Items of this list are loaded onEnable and
     * are saved onDisable
     * TODO: Save and load this list's items
     */
    public final static List<Shop> shops = new ArrayList<>();

    /**
     * The base Shop item that is usually gave to a player when he uses the command
     * /create-shop. To prevent any conflict, it is recommended to give this item
     * using Shop.item#clone()
     */
    public static ItemStack item = new ItemStackFactory(Material.CHEST)
            .setName(StringUtils.formatColorCodes('&', "&9&lShop"))
            .addLoreLine("Create a shop all the other")
            .addLoreLine("players can buy from! You must")
            .addLoreLine("choose a fixed price for any item")
            .addLoreLine("in this chest.")
            .addBlankLoreLine()
            .addLoreLine(StringUtils.formatColorCodes('&', "&eCreate shop  &6&lRIGHT CLICK"))
            .get();

    private final Location location;
    private final double fixedPrice;
    private final UUID shopOwner;
    private final String serial;
    private final boolean isLocationPublic;

    /**
     * Creates a shop instance
     *
     * @param shopLocation               The location of the chest block that is linked to this shop
     * @param shopPrice                  The price that the owner chose for any item in the chest
     * @param owner                      The player that placed the chest and chose the price. He will get all the profit
     * @param showLocationToOtherPlayers Choose whether the location should be shown to other players or not.
     *                                   true if the location can be shown to other players, false if it cannot
     */
    public Shop(Location shopLocation, double shopPrice, Player owner, boolean showLocationToOtherPlayers) {
        this.location = shopLocation;
        this.fixedPrice = shopPrice;
        this.shopOwner = owner.getUniqueId();
        this.serial = StringUtils.generateSerialCode(4, 4, '-');
        this.isLocationPublic = showLocationToOtherPlayers;
    }

    /**
     * Returns the Shop instance linked to the chest in that location
     *
     * @param location The location of the chest
     * @return The Shop linked to the chest located in the specified location, null if there
     * is no Shop in that specific location
     */
    @Nullable
    public static Shop getShop(Location location) {
        return shops.stream()
                .filter(x -> x.getLocation().equals(location))
                .findFirst().orElse(null);
    }

    /**
     * Returns the Shop instance that has the specified serial code
     *
     * @param serialCode The serial code of the Shop instance we must get
     * @return null if there are no shops with the specified serial, the Shop
     * with that serial if it exists
     */
    @Nullable
    public static Shop getShop(String serialCode) {
        return shops.stream()
                .filter(x -> x.getSerial().equals(serialCode))
                .findFirst().orElse(null);
    }

    /**
     * Says whether there is a Shop in the specified location or not
     *
     * @param location The location where we want to know if there is a shop or not
     * @return true if there is a Shop in that specified location, false if there isn't
     */
    public static boolean isShop(Location location) {
        return getShop(location) != null;
    }

    /**
     * Gets all the Shops that the specified player has
     *
     * @param player The player of which we want to get the shops of which he is owner
     * @return A List containing all the shops that the specified player owns
     */
    @Nullable
    public static List<Shop> getPlayerShops(Player player) {
        List<Shop> playerShops = new ArrayList<>();

        for (int i = 0; i < (long) Shop.shops.size(); i++) {
            if (Shop.shops.get(i).getOwner().equals(player)) {
                playerShops.add(Shop.shops.get(i));
            }
        }

        if (playerShops.isEmpty()) return null;
        return playerShops;
    }

    /**
     * Gets the contents of the chest linked to this Shop instance
     *
     * @return The items that the shop owner is selling
     */
    public ItemStack[] getContents() {
        return getShopInventory().getContents();
    }

    /**
     * Gets the Inventory of the chest linked to this Shop instance
     *
     * @return The instance of the Inventory that is linked to the chest that is located in %this%.location
     */
    public Inventory getShopInventory() {
        Chest chest = (Chest) this.location.getBlock().getState();
        return chest.getBlockInventory();
    }

    /**
     * Gets the Inventory that has to be shown to anyone that has to buy from this shop.
     * So, it will be called any time that this shop is going to be interacted by everyone but
     * the owner of the shop
     *
     * @param main The main plugin instance
     * @return The Inventory that has to be shown to any player that tries to interact with
     * this Shop but the owner. It is different from %this%#getShopInventory() because it
     * also has a logic behind it, that has been built with an InventoryFactory. It implements
     * buy features, and it customizes the Inventory, like adding glass in white spaces.
     */
    public Inventory getShopCustomInventory(Plugin main) {
        InventoryFactory factory = new InventoryFactory(this.getShopInventory().getSize() / 9,
                StringUtils.formatColorCodes('&', "&l" + this.getOwner().getDisplayName() + "&r shop"), main);

        for (int i = 0; i < this.getShopInventory().getSize(); i++) {
            factory.setItem(i, this.getShopInventory().getItem(i));

            Shop shop = this;
            Inventory realShopInventory = this.getShopInventory();

            factory.setAction(i, e -> {
                ServerPlayer player = ServerPlayer.getPlayer((Player) e.getWhoClicked());
                assert player != null;

                assert e.getClickedInventory() != null;
                ItemStack clicked = e.getClickedInventory().getItem(e.getRawSlot());
                ItemStack clickedOnRealInventory = realShopInventory.getItem(e.getRawSlot());
                assert clicked != null;
                assert clickedOnRealInventory != null;

                if (clicked.getType().equals(Material.WHITE_STAINED_GLASS_PANE)) return;

                /*if (player.getMoney() < shop.getFixedPrice()) {
                    e.getWhoClicked().sendMessage(ShopError.NOT_ENOUGH_MONEY.getMessage());
                    return;
                }*/

                // TODO: Test if this works
                if(!player.secureRemoveMoney(shop.getFixedPrice())) return;

                int newAmount = clicked.getAmount() - 1;

                ItemStack bought = clicked.clone();
                bought.setAmount(1);

                if (newAmount != 0) {
                    clicked.setAmount(newAmount);
                    clickedOnRealInventory.setAmount(newAmount);
                } else {
                    clicked.setType(Material.WHITE_STAINED_GLASS_PANE);
                    ItemUtils.rename(clicked, " ");
                    realShopInventory.setItem(e.getRawSlot(), new ItemStack(Material.AIR));
                }

                player.removeMoney(shop.getFixedPrice());
                player.getPlayer().getInventory().addItem(bought);

                ServerPlayer owner = ServerPlayer.getPlayer(shop.getOwner());

                assert owner != null;
                owner.addMoney(shop.getFixedPrice());

                player.refreshScoreboard();
            });
        }

        factory.fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                .setName(" ")
                .get());

        factory.setClicksAllowed(false);
        return factory.get();
    }

    /**
     * Tells whether the specified player is the owner of the Shop or not
     *
     * @param player The player we want to know if is the Shop owner or not
     * @return true if the specified player is the owner of the shop, false if the player isn't the owner of the shop
     */
    public boolean isOwner(Player player) {
        return this.shopOwner.equals(player.getUniqueId());
    }

    /**
     * Default getter for this class
     *
     * @return The Shop location
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Default getter for this class
     *
     * @return The price for any item in this Shop
     */
    public double getFixedPrice() {
        return this.fixedPrice;
    }

    /**
     * Default getter for this class
     *
     * @return true if the location of this shop can be shown to other players,
     * false if it can't be shown to other players
     */
    public boolean getLocationPrivacyState() {
        return this.isLocationPublic;
    }

    /**
     * Default getter for this class
     *
     * @return The owner of this Shop
     */
    public Player getOwner() {
        return Bukkit.getPlayer(this.shopOwner);
    }

    public String getSerial() {
        return this.serial;
    }

    /**
     * Removes this Shop instance from the list.
     * It is usually called when the Shop is manually disabled by the Player
     */
    public void removeShop() {
        Shop.shops.remove(this);
    }
}
