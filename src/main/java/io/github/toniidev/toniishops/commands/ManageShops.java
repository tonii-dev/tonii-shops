package io.github.toniidev.toniishops.commands;

import io.github.toniidev.toniishops.classes.Shop;
import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.factories.ItemStackFactory;
import io.github.toniidev.toniishops.strings.ConsoleString;
import io.github.toniidev.toniishops.strings.CommandError;
import io.github.toniidev.toniishops.strings.ShopError;
import io.github.toniidev.toniishops.utils.IntegerUtils;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ManageShops implements CommandExecutor {
    private final Plugin main;

    /**
     * The Shop management command logic
     *
     * @param plugin The main plugin instance
     */
    public ManageShops(Plugin plugin) {
        main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ConsoleString.COMMAND_NOT_EXECUTABLE_FROM_CONSOLE.getMessage());
            return true;
        }

        Permission permission = Bukkit.getPluginManager().getPermission("shop-management");
        assert permission != null;
        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(CommandError.MISSING_PERMISSIONS.getMessage() +
                    StringUtils.formatColorCodes('&', "&r&f" + permission.getName()));
            return true;
        }

        List<Shop> playerShops = Shop.getPlayerShops(player);
        if (playerShops == null) {
            player.sendMessage(ShopError.NO_SHOPS.getMessage());
            return true;
        }

        /// This item HAS to be cloned. To this ItemStack we will add some lore lines
        /// about the Location and the fixed price of the shop.
        ItemStack baseItem = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&3&lShop"))
                .addLoreLine("This is a shop that you own.")
                .addBlankLoreLine()
                .get();

        InventoryFactory shopManagementFactory = new InventoryFactory(IntegerUtils.findInventoryRowsToCreate(playerShops.size()), "Shop management", main);
        for (int i = 0; i < playerShops.size(); i++) {
            shopManagementFactory.setItem(i, new ItemStackFactory(baseItem.clone())
                    .addLoreLine(StringUtils.formatColorCodes('&', "Location: &f") + StringUtils.convertLocation(playerShops.get(i).getLocation(), ',', '7'))
                    .addLoreLine(StringUtils.formatColorCodes('&', "Price: &f" + playerShops.get(i).getFixedPrice()))
                    .addLoreLine(StringUtils.formatColorCodes('&', "Serial: &8&k" + playerShops.get(i).getSerial()))
                    .get());
        }

        player.openInventory(shopManagementFactory
                .fill(new ItemStackFactory(Material.WHITE_STAINED_GLASS_PANE)
                        .setName(" ")
                        .get())
                .setClicksAllowed(false)
                .get());

        return true;
    }
}
