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
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

public class SellAll implements CommandExecutor {
    private final Plugin main;

    public SellAll(Plugin plugin){
        this.main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        /// Chill out, just use SellCustomAmount with Stack size!!
        return new SellCustomAmount(main).callAsAPlayer(commandSender, ((Player) commandSender).getInventory().getItemInMainHand());
    }
}
