package io.github.toniidev.toniishops.commands;

import io.github.toniidev.toniishops.classes.GlobalShop;
import io.github.toniidev.toniishops.classes.GlobalShopBuy;
import io.github.toniidev.toniishops.classes.GlobalShopItem;
import io.github.toniidev.toniishops.classes.ServerPlayer;
import io.github.toniidev.toniishops.extendable.GlobalShopAction;
import io.github.toniidev.toniishops.factories.ItemStackFactory;
import io.github.toniidev.toniishops.factories.StringFactory;
import io.github.toniidev.toniishops.secret.Secret;
import io.github.toniidev.toniishops.strings.CommandError;
import io.github.toniidev.toniishops.strings.ConsoleString;
import io.github.toniidev.toniishops.strings.GlobalShopError;
import io.github.toniidev.toniishops.utils.CommandUtils;
import io.github.toniidev.toniishops.utils.NumberUtils;
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

public class Buy implements CommandExecutor {
    private final Plugin main;

    public Buy(Plugin plugin){
        this.main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ConsoleString.COMMAND_NOT_EXECUTABLE_FROM_CONSOLE.getMessage());
            return true;
        }

        Permission permission = Bukkit.getPluginManager().getPermission("item-buying");
        assert permission != null;
        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(CommandError.MISSING_PERMISSIONS.getMessage() +
                    StringUtils.formatColorCodes('&', "&r&f" + permission.getName()));
            return true;
        }

        if (CommandUtils.checkBaseArgs(strings, player, command)) return true;

        /// Recognize if this has been launched from a player or not
        if (strings[0].equals(Secret.PASS.getString())) {
            /// IN THIS CASE IT HAS BEEN CALLED FROM THE CODE!!
            /// The received command is: /buy <password> <material_name> <amount>

            String materialName = strings[1];
            Material material = Material.getMaterial(materialName);
            assert material != null;

            long amount = Long.parseLong(strings[2]);

            /// Get ServerPlayer instance
            ServerPlayer serverPlayer = ServerPlayer.getPlayer(player);
            assert serverPlayer != null;

            /// Calculate how much the player should pay for that amount of items
            GlobalShopItem item = GlobalShop.getItem(material);
            assert item != null;

            player.openInventory(item.getConfirmGUI(new GlobalShopBuy(true, amount, player, item.getCumulativeBuyPrice(amount)), main));
        } else {
            /// IN THIS CASE IT HAS BEEN LAUNCHED FROM A PLAYER!!
            /// The received command is: /buy <material> <amount>

            /// Check if the amount is valid
        int amount;
        if(strings.length < 2) amount = 1;
        else{
            if(!NumberUtils.isInteger(strings[1])){
                player.sendMessage(GlobalShopError.INVALID_AMOUNT.getMessage());
                return true;
            }
            else amount = Integer.parseInt(strings[1]);
        }

            Material material = Material.getMaterial(strings[0].toUpperCase());

            /// We must check if the Item can be sold.
            /// if material == null, it means that the material does not exist
            if (material == null || material.equals(Material.AIR)) {
                List<String> candidates = new ArrayList<>();
                for(Material mat : Material.values()){
                    candidates.add(mat.name());
                }

                String mostAffine = StringUtils.findMostAffineString(strings[0], candidates).toLowerCase();
                player.sendMessage(new StringFactory(GlobalShopError.ITEM_NOT_EXISTING.getMessage())
                        .append(StringUtils.formatColorCodes('&', "&f&o" + mostAffine + "&r&7?")).setColor('7')
                        .get());
                return true;
            }

            if (!GlobalShop.canSell(material)) {
                player.sendMessage(GlobalShopError.ITEM_CANNOT_BE_SOLD.getMessage());
                return true;
            }

            /// We can just call the command as it was called from the plugin itself
            return this.call(commandSender, material, amount);
        }

        return true;
    }

    public boolean call(CommandSender sender, Material material, long amount) {
        String[] args = new String[]{
                Secret.PASS.getString(),
                material.name(),
                String.valueOf(amount)
        };

        return onCommand(sender, Bukkit.getPluginCommand("buy"), null, args);
    }

    public boolean callAsAPlayer(CommandSender sender, String materialName, long amount){
        String[] args = new String[]{
                materialName,
                String.valueOf(amount)
        };

        return onCommand(sender, Bukkit.getPluginCommand("buy"), null, args);
    }
}
