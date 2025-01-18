package io.github.toniidev.toniishops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Shop implements CommandExecutor {
    private final Plugin main;

    public Shop(Plugin plugin){
        main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // TODO: Add prerequisites

        if(!commandSender instanceof Player player){}

        player.openInventory(new InventoryFactory(5, "Global shop"))

        return false;
    }
}
