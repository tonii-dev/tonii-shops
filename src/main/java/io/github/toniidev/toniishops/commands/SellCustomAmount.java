package io.github.toniidev.toniishops.commands;

import io.github.toniidev.toniishops.classes.GlobalShop;
import io.github.toniidev.toniishops.classes.GlobalShopItem;
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

public class SellCustomAmount implements CommandExecutor {

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

        if (CommandUtils.checkBaseArgs(strings, player, command)) return true;

        /// Recognize if this has been launched from a player or not
        if (strings[0].equals(Secret.PASS.getString())) {
            /// IN THIS CASE IT HAS BEEN CALLED FROM THE CODE!!
            /// The received command is: /sellc <password> <material_name> <amount>

            String materialName = strings[1];
            Material material = Material.getMaterial(materialName);
            long amount = Long.parseLong(strings[2]);

            assert material != null;

            /// Check if the player has that item
            if (player.getInventory().contains(material)) {
                /// In this case, this command is launched directly from the Global Shop.
                /// So, the Material is 100% capable of being sold.
                /// We only need to check whether the player has the specified amount or not

                int available = 0;
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if(itemStack != null){
                        if (itemStack.getType().equals(material)) available += itemStack.getAmount();
                    }
                }
                if (available < amount) {
                    player.sendMessage(GlobalShopError.NOT_ENOUGH_ITEMS.getMessage());
                    return true;
                }

                GlobalShopItem item = GlobalShop.getItem(material);
                assert item != null;

                item.sellCustomAmount(player, amount);

                /// Remove sold items
                /// We must remove from the player Inventory <amount> of Items that have the type <material>
                int remainingToRemove = (int) amount;

                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack == null || !itemStack.getType().equals(material)) continue;

                    int stackAmount = itemStack.getAmount();

                    if (stackAmount > remainingToRemove) {
                        /// Reduce the stack size and finish removal
                        itemStack.setAmount(stackAmount - remainingToRemove);
                        break;
                    } else {
                        /// Remove the entire stack and continue
                        remainingToRemove -= stackAmount;
                        player.getInventory().removeItem(itemStack);
                    }

                    if (remainingToRemove <= 0) break;
                }
            } else {
                player.sendMessage(GlobalShopError.ITEM_NOT_OWNED.getMessage());
                return true;
            }
        } else {
            /// IN THIS CASE IT HAS BEEN LAUNCHED FROM A PLAYER!!
            /// The received command is: /sellc <amount>

            /// Check if the amount is valid
            if(!NumberUtils.isInteger(strings[0])){
                player.sendMessage(GlobalShopError.INVALID_AMOUNT.getMessage());
                return true;
            }

            /// We must check if the Item can be sold.
            /// In this case, we must refer to the Item the player is holding in main hand.
            if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                player.sendMessage(GlobalShopError.INVALID_ITEM.getMessage());
                return true;
            }

            if (!GlobalShop.canSell(player.getInventory().getItemInMainHand().getType())) {
                player.sendMessage(GlobalShopError.ITEM_CANNOT_BE_SOLD.getMessage());
                return true;
            }

            /// We can just call the command as it was called from the plugin itself
            return this.call(commandSender, player.getInventory().getItemInMainHand().getType(), Integer.parseInt(strings[0]));
        }

        return true;
    }

    public boolean call(CommandSender sender, Material material, long amount) {
        String[] args = new String[]{
                Secret.PASS.getString(),
                material.name(),
                String.valueOf(amount)
        };

        return onCommand(sender, Bukkit.getPluginCommand("sell-custom-amount"), null, args);
    }

    public boolean callAsAPlayer(CommandSender sender, long amount){
        String[] args = new String[]{
                String.valueOf(amount)
        };

        return onCommand(sender, Bukkit.getPluginCommand("sell-custom-amount"), null, args);
    }

    public boolean callAsAPlayer(CommandSender sender, ItemStack itemStackToGetAmountFrom){
        if(itemStackToGetAmountFrom.getType().equals(Material.AIR)) return true;

        return callAsAPlayer(sender, itemStackToGetAmountFrom.getAmount());
    }
}
