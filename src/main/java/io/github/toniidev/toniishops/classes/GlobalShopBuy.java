package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.enums.GlobalShopActionType;
import io.github.toniidev.toniishops.extendable.GlobalShopAction;
import org.bukkit.entity.Player;

public class GlobalShopBuy extends GlobalShopAction {
    /**
     * Creates a Buy GlobalShopAction.
     * @param multiple Whether the player bought one or more items
     * @param amount The amount the player has bought
     * @param player The player linked to this action
     * @param price The price to pay for this action to be completed
     */
    public GlobalShopBuy(boolean multiple, long amount, Player player, double price) {
        super((multiple ? GlobalShopActionType.BUY_MULTIPLE : GlobalShopActionType.BUY_ONE), amount, player, price);
    }
}
