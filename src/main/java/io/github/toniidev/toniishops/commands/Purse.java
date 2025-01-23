package io.github.toniidev.toniishops.commands;

import io.github.toniidev.toniishops.classes.ServerPlayer;
import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.factories.ItemStackFactory;
import io.github.toniidev.toniishops.factories.MultipleInventoryFactory;
import io.github.toniidev.toniishops.strings.CommandError;
import io.github.toniidev.toniishops.strings.ConsoleString;
import io.github.toniidev.toniishops.utils.CommandUtils;
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

public class Purse implements CommandExecutor {
    private final Plugin main;

    public Purse(Plugin plugin){
        this.main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ConsoleString.COMMAND_NOT_EXECUTABLE_FROM_CONSOLE.getMessage());
            return true;
        }

        Permission permission = Bukkit.getPluginManager().getPermission("purse-management");
        assert permission != null;
        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(CommandError.MISSING_PERMISSIONS.getMessage() +
                    StringUtils.formatColorCodes('&', "&r&f" + permission.getName()));
            return true;
        }

        if(CommandUtils.checkBaseArgs(strings, player, command)) return true;

        List<ItemStack> itemStacks = new ArrayList<>();
        ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
        assert serverPlayer != null;

        for(int i = 0; i < serverPlayer.getMoney(); i++){
            itemStacks.add(new ItemStackFactory(Material.EMERALD, (serverPlayer.getMoney() - i >= 64 ? 64 : (int) serverPlayer.getMoney() - i))
                    .setName("1$")
                    .addLoreLine("Currency item. You can use this")
                    .addLoreLine("to buy anything you can imagine")
                    .get());
        }

        player.openInventory(new MultipleInventoryFactory(itemStacks, main, new InventoryFactory(6, "Purse", main))
                .get());

        return true;
    }
}
