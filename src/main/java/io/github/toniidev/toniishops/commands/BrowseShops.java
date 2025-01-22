package io.github.toniidev.toniishops.commands;

import io.github.toniidev.toniishops.classes.Shop;
import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.factories.ItemStackFactory;
import io.github.toniidev.toniishops.strings.CommandError;
import io.github.toniidev.toniishops.strings.ConsoleString;
import io.github.toniidev.toniishops.strings.ShopError;
import io.github.toniidev.toniishops.utils.CommandUtils;
import io.github.toniidev.toniishops.utils.NumberUtils;
import io.github.toniidev.toniishops.utils.ItemUtils;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class BrowseShops implements CommandExecutor {
    private final Plugin main;

    /**
     * The Shop browsing command logic
     *
     * @param plugin The main plugin instance
     */
    public BrowseShops(Plugin plugin) {
        this.main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ConsoleString.COMMAND_NOT_EXECUTABLE_FROM_CONSOLE.getMessage());
            return true;
        }

        Permission permission = Bukkit.getPluginManager().getPermission("shop-browsing");
        assert permission != null;
        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(CommandError.MISSING_PERMISSIONS.getMessage() +
                    StringUtils.formatColorCodes('&', "&r&f" + permission.getName()));
            return true;
        }

        if(CommandUtils.checkBaseArgs(strings, player, command)) return true;

        List<Shop> allShops = Shop.shops;
        List<Shop> shopsToConsider = new ArrayList<>();
        for (Shop shop : allShops) {
            if (!shop.isOwner(player)) shopsToConsider.add(shop);
        }

        if (shopsToConsider.isEmpty()) {
            player.sendMessage(ShopError.NO_SHOPS_TO_BROWSE.getMessage());
            return true;
        }

        /// This item HAS to be cloned. To this ItemStack we will add some lore lines
        /// about the Location and the fixed price of the shop.
        ItemStack baseItem = new ItemStackFactory(Material.FILLED_MAP)
                .setName(StringUtils.formatColorCodes('&', "&3&lShop"))
                .addLoreLine("This is a shop created by a player.")
                .addBlankLoreLine()
                .get();

        InventoryFactory shopManagementFactory = new InventoryFactory(NumberUtils.findInventoryRowsToCreate(shopsToConsider.size()), "Browse shops", main)
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ")
                        .get())
                .setClicksAllowed(false);

        for (int i = 0; i < shopsToConsider.size(); i++) {
            if (!shopsToConsider.get(i).isOwner(player)) {
                ItemStackFactory itemStackFactory = new ItemStackFactory(baseItem.clone());

                if (shopsToConsider.get(i).getLocationPrivacyState()) {
                    itemStackFactory.addLoreLine(StringUtils.formatColorCodes('&', "Location: &f") + StringUtils.convertLocation(shopsToConsider.get(i).getLocation(), ',', '7'));
                }

                shopManagementFactory.setItem(i, itemStackFactory
                                .addLoreLine(StringUtils.formatColorCodes('&', "Owner: &f") + shopsToConsider.get(i).getOwner().getDisplayName())
                                .addLoreLine(StringUtils.formatColorCodes('&', "Price: &f" + shopsToConsider.get(i).getFixedPrice()))
                                .addLoreLine(StringUtils.formatColorCodes('&', "Serial: &8&k" + shopsToConsider.get(i).getSerial()))
                                .get())

                        .setAction(i, e -> {
                            ItemStack shopItemStack = e.getInventory().getItem(e.getRawSlot());
                            if (shopItemStack == null) return;

                            Shop shop = Shop.getShop(ItemUtils.isolateSerialCode(shopItemStack));
                            if (shop == null) return;

                            e.getWhoClicked().openInventory(shop.getShopCustomInventory(main));
                        });
            }
        }

        player.openInventory(shopManagementFactory.get());

        return true;
    }
}
