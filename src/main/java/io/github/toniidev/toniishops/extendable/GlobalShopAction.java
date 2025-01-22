package io.github.toniidev.toniishops.extendable;

import io.github.toniidev.toniishops.enums.GlobalShopActionType;
import org.bukkit.entity.Player;

public class GlobalShopAction {
    private GlobalShopActionType type;
    private long amount;
    private Player player;
    private double price;

    public GlobalShopAction(GlobalShopActionType type, long amount, Player player, double price) {
        this.type = type;
        this.amount = amount;
        this.player = player;
        this.price = price;
    }

    public GlobalShopActionType getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    public Player getPlayer() {
        return player;
    }

    public double getPrice() {
        return price;
    }

    public GlobalShopAction setAmount(long amount) {
        this.amount = amount;
        return this;
    }

    public GlobalShopAction setPrice(double price) {
        this.price = price;
        return this;
    }
}
