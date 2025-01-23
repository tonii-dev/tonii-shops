package io.github.toniidev.toniishops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class SellItem implements CommandExecutor {
    private final Plugin main;

    public SellItem(Plugin plugin){
        this.main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        /// Chill out, just use SellCustomAmount with 1 as amount!!
        return new SellCustomAmount(main).callAsAPlayer(commandSender, 1);
    }
}
