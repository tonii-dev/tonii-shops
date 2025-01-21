package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.enums.GlobalShopActionType;
import io.github.toniidev.toniishops.extendable.GlobalShopAction;
import org.bukkit.entity.Player;

public class GlobalShopSell extends GlobalShopAction {
    public GlobalShopSell(boolean multiple, long amount, Player player, double price) {
        super((multiple ? GlobalShopActionType.SELL_MULTIPLE : GlobalShopActionType.SELL_ONE), amount, player, price);
    }
}
