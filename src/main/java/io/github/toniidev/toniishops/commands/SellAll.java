package io.github.toniidev.toniishops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
