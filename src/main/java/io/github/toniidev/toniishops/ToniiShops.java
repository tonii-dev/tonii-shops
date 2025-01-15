package io.github.toniidev.toniishops;

import io.github.toniidev.toniishops.commands.*;
import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.listener.BlockListener;
import io.github.toniidev.toniishops.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ToniiShops extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginCommand("give-permission").setExecutor(new GivePermission(this));
        Bukkit.getPluginCommand("remove-permission").setExecutor(new RemovePermission(this));
        Bukkit.getPluginCommand("manage-shops").setExecutor(new ManageShops(this));
        Bukkit.getPluginCommand("browse-shops").setExecutor(new BrowseShops(this));
        Bukkit.getPluginCommand("create-shop").setExecutor(new CreateShop());

        Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryFactory(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
