package io.github.toniidev.toniishops;

import io.github.toniidev.toniishops.classes.GlobalShop;
import io.github.toniidev.toniishops.commands.*;
import io.github.toniidev.toniishops.factories.InputFactory;
import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.listeners.BlockListener;
import io.github.toniidev.toniishops.listeners.PlayerListener;
import io.github.toniidev.toniishops.utils.InitializeUtils;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ToniiShops extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup
        new InitializeUtils(new GivePermission(this), "give-permission").initialize();
        new InitializeUtils(new RemovePermission(this), "remove-permission").initialize();
        new InitializeUtils(new ManageShops(this), "manage-shops").initialize();
        new InitializeUtils(new BrowseShops(this), "browse-shops").initialize();
        new InitializeUtils(new CreateShop(), "create-shop").initialize();
        new InitializeUtils(new SellItem(this), "sell-item").initialize();
        new InitializeUtils(new SellAll(this), "sell-all").initialize();
        new InitializeUtils(new Shop(this), "open-shop").initialize();
        new InitializeUtils(new Purse(this), "open-purse").initialize();
        new InitializeUtils(new SellCustomAmount(this), "sell-custom-amount").initialize();
        new InitializeUtils(new Buy(this), "buy").initialize();

        Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryFactory(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new InputFactory(this), this);

        GlobalShop.initializeShop(200);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
