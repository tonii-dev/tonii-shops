package io.github.toniidev.toniishops.commands;

import io.github.toniidev.toniishops.classes.GlobalShop;
import io.github.toniidev.toniishops.classes.GlobalShopItem;
import io.github.toniidev.toniishops.strings.CommandError;
import io.github.toniidev.toniishops.strings.ConsoleString;
import io.github.toniidev.toniishops.strings.GlobalShopError;
import io.github.toniidev.toniishops.utils.CommandUtils;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class SellItem implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ConsoleString.COMMAND_NOT_EXECUTABLE_FROM_CONSOLE.getMessage());
            return true;
        }

        Permission permission = Bukkit.getPluginManager().getPermission("item-selling");
        assert permission != null;
        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(CommandError.MISSING_PERMISSIONS.getMessage() +
                    StringUtils.formatColorCodes('&', "&r&f" + permission.getName()));
            return true;
        }

        if(CommandUtils.checkBaseArgs(strings, player, command)) return true;

        if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            player.sendMessage(GlobalShopError.INVALID_ITEM.getMessage());
            return true;
        }

        if(!GlobalShop.canSell(player.getInventory().getItemInMainHand().getType())){
            player.sendMessage(GlobalShopError.ITEM_CANNOT_BE_SOLD.getMessage());
            return true;
        }

        GlobalShopItem item = GlobalShop.getItem(player.getInventory().getItemInMainHand().getType());
        assert item != null;

        item.sellOne(player);
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

        return true;
    }
}
