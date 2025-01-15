package io.github.toniidev.toniishops.listeners;

import io.github.toniidev.toniishops.classes.ServerPlayer;
import io.github.toniidev.toniishops.classes.Shop;
import io.github.toniidev.toniishops.factories.BlockFactory;
import io.github.toniidev.toniishops.strings.ShopError;
import io.github.toniidev.toniishops.strings.ShopSuccess;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockListener implements Listener {
    private final Plugin main;

    /**
     * The BlockListener logic
     *
     * @param plugin The main plugin instance
     */
    public BlockListener(Plugin plugin) {
        this.main = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().getInventory().getItemInMainHand().equals(Shop.item)) return;
        new BlockFactory(e.getBlockPlaced().getLocation())
                .setTitle(StringUtils.formatColorCodes('&', "&f&l" + e.getPlayer().getDisplayName() + "&r&b shop"))
                .setSubtitle(StringUtils.formatColorCodes('&', "&e&lCLICK!"));
        Shop.shops.add(new Shop(e.getBlockPlaced().getLocation(), 200, e.getPlayer(), true));
        ServerPlayer.refreshScoreboard(e.getPlayer());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!Shop.isShop(e.getBlock().getLocation())) return;
        Shop shop = Shop.getShop(e.getBlock().getLocation());
        assert shop != null;

        ItemStack[] items = shop.getContents();

        e.setCancelled(true);

        if (!shop.isOwner(e.getPlayer())) {
            e.getPlayer().sendMessage(ShopError.NO_OWNER.getMessage());
            return;
        }

        BlockFactory.breakShop(e.getBlock().getLocation());

        new BukkitRunnable() {
            @Override
            public void run() {
                e.getBlock().setType(Material.AIR);
            }
        }.runTaskLater(main, 1L);

        shop.removeShop();

        e.getPlayer().sendMessage(ShopSuccess.SHOP_BROKE_SUCCESSFULLY.getMessage());

        if (items[0] != null) {
            for (ItemStack s : shop.getContents())
                if (s != null) e.getPlayer().getInventory().addItem(s);
            e.getPlayer().sendMessage(ShopSuccess.SHOP_ITEMS_SENT_TO_INVENTORY.getMessage());
        }

        ServerPlayer.refreshScoreboard(e.getPlayer());
    }
}
