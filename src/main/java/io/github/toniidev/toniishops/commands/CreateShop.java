package io.github.toniidev.toniishops.commands;

import io.github.toniidev.toniishops.classes.Shop;
import io.github.toniidev.toniishops.strings.ConsoleString;
import io.github.toniidev.toniishops.strings.CommandError;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Gives the player a chest that he can place to create a shop
 */
public class CreateShop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage(ConsoleString.COMMAND_NOT_EXECUTABLE_FROM_CONSOLE.getMessage());
            return true;
        }

        Permission permission = Bukkit.getPluginManager().getPermission("shop-creation");
        assert permission != null;
        if(!commandSender.hasPermission(permission)){
            commandSender.sendMessage(CommandError.MISSING_PERMISSIONS.getMessage() +
                    StringUtils.formatColorCodes('&', "&r&f" + permission.getName()));
            return true;
        }

        player.getInventory().addItem(Shop.item.clone());
        return true;
    }
}
