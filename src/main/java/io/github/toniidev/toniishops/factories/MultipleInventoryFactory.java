package io.github.toniidev.toniishops.factories;

import io.github.toniidev.toniishops.classes.GlobalShop;
import io.github.toniidev.toniishops.interfaces.InventoryInterface;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class MultipleInventoryFactory {
    private final Plugin main;
    private final List<Inventory> pages;

    public MultipleInventoryFactory(List<ItemStack> items, String title, Plugin plugin, InventoryFactory startFactory) {
        this.main = plugin;
        this.pages = new ArrayList<>();

        int totalPages = (int) Math.ceil((double) items.size() / 21);

        // Create pages
        for (int pageNumber = 0; pageNumber < totalPages; pageNumber++) {
            int startIndex = pageNumber * 21;
            int endIndex = Math.min(startIndex + 21, items.size());
            List<ItemStack> currentItems = items.subList(startIndex, endIndex);

            // Create page inventory with items
            InventoryFactory pageInventory = createPageTemplate(startFactory)
                    .addItem(currentItems);

            pages.add(finalizePage(pageNumber, title, totalPages, pageInventory));
        }
    }

    private InventoryFactory createPageTemplate(InventoryFactory startFactory) {
        boolean clicksAllowed = startFactory.getClicksPermissionStatus(0);

        // Ensure actions is a mutable map
        Map<Integer, InventoryInterface> actions = startFactory.getActions() == null
                ? new HashMap<>()  // Use a mutable map instead of emptyMap
                : new HashMap<>(startFactory.getActions());

        Map<Integer, Inventory> redirects = startFactory.getRedirects() == null
                ? new HashMap<>()
                : startFactory.getRedirects();

        Inventory inventoryToShowOnClose = startFactory.getInventoryToShowOnClose();

        InventoryFactory factory = new InventoryFactory(6, " ", main)
                .fill(new ItemStackFactory(Material.GRAY_STAINED_GLASS_PANE).setName(" ").get())
                .setInventoryToShowOnClose(inventoryToShowOnClose)
                .setClicksAllowed(clicksAllowed)
                .setActions(actions)  // Ensure actions is mutable here
                .setRedirects(redirects);

        setAirItems(factory); // Set air items for the inventory template

        return factory;
    }

    private void setAirItems(InventoryFactory factory) {
        int[] airSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        for (int slot : airSlots) {
            factory.setItem(slot, new ItemStack(Material.AIR));
        }
    }

    private Inventory finalizePage(int pageNumber, String title, int totalPages, InventoryFactory template) {
        template.setTitle(title);

        // Set page navigation items
        setPageNavigationItems(pageNumber, totalPages, template);

        return template.get();
    }

    private void setPageNavigationItems(int pageNumber, int totalPages, InventoryFactory template) {
        template.setItem(49, new ItemStackFactory(Material.COMPASS)
                .setName(StringUtils.formatColorCodes('&', "&eInventory page"))
                .addLoreLine(new StringFactory()
                        .append("Current page:").setColor('7')
                        .append(String.valueOf(pageNumber + 1)).setColor('f')
                        .get())
                .get());

        if (pageNumber > 0) {
            template.setAction(46, e -> openPage(pageNumber - 1, template, e.getWhoClicked()));
            template.setItem(46, createNavigationItem(Material.RED_STAINED_GLASS_PANE,
                    StringUtils.formatColorCodes('&', "&cPrevious page"),
                    StringUtils.formatColorCodes('&', String.format("Navigate back to page &e%d", pageNumber))));
        }

        if (pageNumber < totalPages - 1) {
            template.setAction(52, e -> openPage(pageNumber + 1, template, e.getWhoClicked()));
            template.setItem(52, createNavigationItem(Material.GREEN_STAINED_GLASS_PANE,
                    StringUtils.formatColorCodes('&', "&aNext page"),
                    StringUtils.formatColorCodes('&', String.format("Navigate to page &e%d", pageNumber + 2))));
        }
    }

    private ItemStack createNavigationItem(Material material, String name, String lore) {
        return new ItemStackFactory(material)
                .setName(name)
                .addLoreLine(lore)
                .get();
    }

    private void openPage(int pageNumber, InventoryFactory template, HumanEntity player) {
        Inventory prev = template.getInventoryToShowOnClose();
        template.setInventoryToShowOnClose(null);
        player.openInventory(pages.get(pageNumber));
        template.setInventoryToShowOnClose(prev);
    }

    public Inventory get() {
        return pages.isEmpty() ? null : pages.get(0);
    }
}
