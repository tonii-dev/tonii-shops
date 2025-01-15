package io.github.toniidev.toniishops.commands;

import io.github.toniidev.toniishops.factories.StringFactory;
import io.github.toniidev.toniishops.strings.CommandError;
import io.github.toniidev.toniishops.strings.CommandSuccess;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

/**
 * WARNING: This class contains some strings that aren't stored in any separate enum!
 */
public class GivePermission implements CommandExecutor {
    private final Plugin main;

    /**
     * The GivePermission command logic.
     *
     * @param plugin The main plugin instance.
     */
    public GivePermission(Plugin plugin) {
        this.main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(CommandError.COMMAND_NEEDS_OP.getMessage());

            return true;
        }

        if (args.length < 2 || args[0] == null || args[1] == null) {
            commandSender.sendMessage(new StringFactory(CommandError.INVALID_COMMAND_USAGE.getMessage())
                    .append(command.getUsage()).setColor('f')
                    .get());

            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            commandSender.sendMessage(new StringFactory()
                    .append("[tonii-shops]").setColor('a')
                    .append("Command:").setColor('e')
                    .append("Player").setColor('7')
                    .append(args[0]).setColor('f')
                    .append("not found").setColor('7')
                    .get());

            return true;
        }

        if (Bukkit.getPluginManager().getPermission(args[1]) == null) {
            commandSender.sendMessage(new StringFactory()
                    .append("[tonii-shops]").setColor('a')
                    .append("Command:").setColor('e')
                    .append("Permission").setColor('7')
                    .append(args[1]).setColor('f')
                    .append("not found").setColor('7')
                    .get());

            return true;
        }

        if (player.hasPermission(args[1])) {
            commandSender.sendMessage(CommandError.PLAYER_ALREADY_HAS_PERMISSION.getMessage());

            return true;
        }

        GivePermission.givePermission(player, args[1], main);
        commandSender.sendMessage(CommandSuccess.COMMAND_EXECUTED_SUCCESSFULLY.getMessage());
        return true;
    }

    /**
     * Gives a permission to a Player. Before using this class, I always make sure
     * that player != null, a permission named %permissionName% exists, and player does not have
     * that permission.
     *
     * @param player         The player we want to give the permission to
     * @param permissionName The name of the permission that we want to give to the player
     * @param plugin         The main plugin instance
     */
    public static void givePermission(Player player, String permissionName, Plugin plugin) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission(permissionName, true);
    }
}
