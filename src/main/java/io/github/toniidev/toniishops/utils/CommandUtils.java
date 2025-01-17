package io.github.toniidev.toniishops.utils;

import io.github.toniidev.toniishops.strings.CommandString;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandUtils {
    public static boolean checkBaseArgs(String[] args, Player player, Command command) {
        switch (args[0].replace("--", "")) {
            case "desc", "description", "d" -> {
                player.sendMessage(CommandString.COMMAND_DESCRIPTION.getFinalMessage(Bukkit.getPluginCommand(command.getName())));
                return true;
            }
            case "usage", "u" -> {
                player.sendMessage(CommandString.COMMAND_USAGE.getFinalMessage(Bukkit.getPluginCommand(command.getName())));
                return true;
            }
        }

        return false;
    }
}
