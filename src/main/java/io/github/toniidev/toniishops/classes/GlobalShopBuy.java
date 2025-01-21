package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.enums.GlobalShopActionType;
import io.github.toniidev.toniishops.extendable.GlobalShopAction;
import org.bukkit.entity.Player;

public class GlobalShopBuy extends GlobalShopAction {
    public GlobalShopBuy(boolean multiple, long amount, Player player, double price) {
        super((multiple ? GlobalShopActionType.BUY_MULTIPLE : GlobalShopActionType.BUY_ONE), amount, player, price);
    }
}
