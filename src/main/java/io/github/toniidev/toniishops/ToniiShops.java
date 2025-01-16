package io.github.toniidev.toniishops;

import io.github.toniidev.toniishops.classes.GlobalShop;
import io.github.toniidev.toniishops.commands.*;
import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.listeners.BlockListener;
import io.github.toniidev.toniishops.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ToniiShops extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        PluginCommand givePermission = Bukkit.getPluginCommand("give-permission");
        PluginCommand removePermission = Bukkit.getPluginCommand("remove-permission");
        PluginCommand manageShops = Bukkit.getPluginCommand("manage-shops");
        PluginCommand browseShops = Bukkit.getPluginCommand("browse-shops");
        PluginCommand createShop = Bukkit.getPluginCommand("create-shop");
        PluginCommand sellItem = Bukkit.getPluginCommand("sell-item");
        PluginCommand sellAll = Bukkit.getPluginCommand("sell-all");

        assert givePermission != null;
        assert removePermission != null;
        assert manageShops != null;
        assert browseShops != null;
        assert createShop != null;
        assert sellItem != null;
        assert sellAll != null;

        givePermission.setExecutor(new GivePermission(this));
        removePermission.setExecutor(new RemovePermission(this));
        manageShops.setExecutor(new ManageShops(this));
        browseShops.setExecutor(new BrowseShops(this));
        createShop.setExecutor(new CreateShop());
        sellItem.setExecutor(new SellItem());
        sellAll.setExecutor(new SellAll());

        Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryFactory(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        GlobalShop.initializeShop(200);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
