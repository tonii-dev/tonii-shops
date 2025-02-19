package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.factories.ScoreboardFactory;
import io.github.toniidev.toniishops.strings.GeneralMessage;
import io.github.toniidev.toniishops.strings.ShopError;
import io.github.toniidev.toniishops.utils.NumberUtils;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerPlayer {
    /**
     * Items of this lists are loaded onEnable and
     * are saved onDisable
     * TODO: Save and load this lists' items
     */
    public static List<ServerPlayer> serverPlayers = new ArrayList<>();

    private final List<ItemStack> stashed = new ArrayList<>();
    private final UUID playerUniqueID;
    private double money = 40000;

    /**
     * Creates a new ServerPlayer instance
     *
     * @param player The Player linked to this ServerPlayer instance
     */
    public ServerPlayer(Player player) {
        this.playerUniqueID = player.getUniqueId();
        this.refreshScoreboard();
    }

    /**
     * Default getter for this class
     * @return A List of ItemStacks containing all the ItemStacks that could not be assigned to the player
     * because his Inventory was full
     */
    public List<ItemStack> getStashed() {
        return stashed;
    }

    /**
     * Adds an ItemStack to the Stashed ItemStacks list
     * @param itemStack The ItemStack to add to the list
     */
    public void addItemStackToStashed(ItemStack itemStack){
        stashed.add(itemStack);
    }

    /**
     * Removes one Item from an ItemStack of the specified material from the Stashed ItemStacks list
     * @param material The Material of the Item to remove from the ItemStack present in the list
     */
    public void removeItemStackFromStashed(Material material){
        ItemStack itemStack = stashed.stream()
                .filter(x -> x.getType().equals(material))
                .findFirst().orElse(null);
        if(itemStack == null) return;
        if(itemStack.getAmount() > 1) itemStack.setAmount(itemStack.getAmount() - 1);
        else stashed.remove(itemStack);
    }

    /**
     * Adds the specified ItemStack to the Player linked to this ServerPlayer instance. If the player does not have enough inventory
     * space, it will be sent to /stashed inventory
     * @param itemStack The ItemStack that has to be added to the player Inventory
     */
    public void addItemToInventory(ItemStack itemStack){
        if(this.getPlayer().getInventory().firstEmpty() == -1){
            this.addItemStackToStashed(itemStack);
            this.getPlayer().sendMessage(GeneralMessage.NOT_ENOUGH_INVENTORY_SPACE.getMessage());
        }
        else this.getPlayer().getInventory().addItem(itemStack);
    }

    /**
     * Tells whether this player is saved in ServerPlayer.serverPlayers or not.
     * If it's not, it means the player is new
     *
     * @param player The player we need to know whether is new or not
     * @return true if the player is not saved in ServerPlayer.serverPlayers list,
     * false if the player is saved in ServerPlayer.serverPlayers list
     */
    public static boolean isPlayerNew(Player player) {
        /// this statement is null => serverPlayers doesn't contain the specified player => the player is new => true
        /// this statement is not null => serverPlayers contains the specified player => the player isn't new => false
        return ServerPlayer.getPlayer(player) == null;
    }

    /**
     * Default getter for this class
     *
     * @return The Player linked to this ServerPlayer instance
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(this.playerUniqueID);
    }

    /**
     * Default getter for this class
     *
     * @return The balance of the Player linked to this ServerPlayer instance
     */
    public double getMoney() {
        return this.money;
    }

    /**
     * Default setter for this class
     *
     * @param value The new value of the balance of the Player linked to this ServerPlayer instance
     */
    public void setMoney(double value) {
        this.money = value;
        this.refreshScoreboard();
    }

    /**
     * Adds the specified value to the balance of the Player linked to this ServerPlayer instance
     *
     * @param value The amount of money to add to the balance of the Player linked to this ServerPlayer instance
     */
    public void addMoney(double value) {
        this.setMoney(this.getMoney() + value);
    }

    /**
     * Removes the specified value from the balance of the Player linked to this ServerPlayer instance
     *
     * @param value The amount of money to remove from the balance of the Player linked to this ServerPlayer instance
     */
    public void removeMoney(double value) {
        this.setMoney(this.getMoney() - value);
    }

    /**
     * A secure way to remove money from someone's balance. It notifies the Player if he doesn't have enough money
     *
     * @param value The amount of money to remove from Player balance
     * @return true if the operation is successful, false if it's not
     */
    public boolean secureRemoveMoney(double value) {
        if (this.getMoney() < value) {
            this.getPlayer().sendMessage(ShopError.NOT_ENOUGH_MONEY.getMessage());
            return false;
        }

        this.removeMoney(value);
        return true;
    }

    /**
     * Gets the ServerInstance linked to the specified player, if it has one
     *
     * @param player The player we need the ServerPlayer instance of
     * @return The ServerPlayer instance linked to the specified player if it exists, null if it doesn't exist
     */
    @Nullable
    public static ServerPlayer getPlayer(Player player) {
        return ServerPlayer.serverPlayers.stream()
                .filter(x -> x.getPlayer().equals(player))
                .findFirst().orElse(null);
    }

    /**
     * Default getter for this class.
     *
     * @return The scoreboard factory of the Player linked to this ServerPlayer instance
     */
    public ScoreboardFactory getScoreboardFactory() {
        List<Shop> shops = Shop.getPlayerShops(this.getPlayer());

        String shopLine;
        if (shops == null) shopLine = StringUtils.formatColorCodes('&', "&fActive shops: &b0");
        else shopLine = StringUtils.formatColorCodes('&', "&fActive shops: &b" + shops.size());

        return new ScoreboardFactory(this.getPlayer(), StringUtils.formatColorCodes('&', "&b&lWorld&f&lWide"))
                .addLine("　")
                .addLine(StringUtils.formatColorCodes('&', "&fMoney: &a" + NumberUtils.round(this.money, 2) + "$"))
                .addLine(shopLine)
                .addBlankLine()
                .addLine(StringUtils.formatColorCodes('&', "&fLocation: &r" + StringUtils.convertLocation(this.getPlayer().getLocation(), ',', '7', 'e')));
    }

    /**
     * Refreshes the scoreboard of the Player linked to this ServerPlayer instance
     */
    public void refreshScoreboard() {
        this.getScoreboardFactory().display();
    }

    /**
     * Static method that refreshes the scoreboard of a Player
     *
     * @param player The Player of which we must refresh the scoreboard
     */
    public static void refreshScoreboard(Player player) {
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        serverPlayer.refreshScoreboard();
    }
}
