package io.github.toniidev.toniishops.factories;

import io.github.toniidev.toniishops.interfaces.InventoryInterface;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class MultipleInventoryFactory {
    private final List<Inventory> pages;

    private final int[] airSlots = {2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 24, 25, 29,
            30, 31, 32, 33, 34, 38, 39, 40, 41, 42, 43, 47, 48, 49, 50, 51, 52};

    public MultipleInventoryFactory(List<ItemStack> items, Plugin plugin, InventoryFactory startFactory) {
        this.pages = new ArrayList<>();

        int totalPages = (int) Math.ceil((double) items.size() / airSlots.length);

        // Create pages
        for (int pageNumber = 0; pageNumber < totalPages; pageNumber++) {
            int startIndex = pageNumber * airSlots.length;
            int endIndex = Math.min(startIndex + airSlots.length, items.size());
            List<ItemStack> currentItems = items.subList(startIndex, endIndex);

            // Create page inventory with items
            InventoryFactory pageInventory = createPageTemplate(startFactory)
                    .addItem(currentItems);

            pages.add(finalizePage(pageNumber, totalPages, pageInventory));
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

        InventoryFactory factory = new InventoryFactory(6, startFactory.getTitle(), startFactory.getMainPluginInstance())
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE).setName(" ").get())
                .setInventoryToShowOnClose(inventoryToShowOnClose)
                .setClicksAllowed(clicksAllowed)
                .setActions(actions)  // Ensure actions is mutable here
                .setRedirects(redirects);

        setAirItems(factory); // Set air items for the inventory template

        return factory;
    }

    private void setAirItems(InventoryFactory factory) {
        for (int slot : airSlots) {
            factory.setItem(slot, new ItemStack(Material.AIR));
        }
    }

    private Inventory finalizePage(int pageNumber, int totalPages, InventoryFactory template) {
        // Set page navigation items
        setPageNavigationItems(pageNumber, totalPages, template);

        return template.get();
    }

    private void setPageNavigationItems(int pageNumber, int totalPages, InventoryFactory template) {
        template.setItem(9, new ItemStackFactory(Material.COMPASS)
                .setName(StringUtils.formatColorCodes('&', "&eInventory page"))
                .addLoreLine(new StringFactory()
                        .append("Current page:").setColor('7')
                        .append(String.valueOf(pageNumber + 1)).setColor('f')
                        .append("of").setColor('7')
                        .append(String.valueOf(totalPages)).setColor('f')
                        .get())
                .get());

        if (pageNumber > 0) {
            template.setAction(27, e -> openPage(pageNumber - 1, e.getWhoClicked()));
            template.setItem(27, createNavigationItem(Material.ARROW,
                    StringUtils.formatColorCodes('&', "&cPrevious page"),
                    StringUtils.formatColorCodes('&', String.format("Navigate back to page &e%d", pageNumber))));
        }

        if (pageNumber < totalPages - 1) {
            template.setAction(36, e -> openPage(pageNumber + 1, e.getWhoClicked()));
            template.setItem(36, createNavigationItem(Material.SPECTRAL_ARROW,
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

    private void openPage(int pageNumber, HumanEntity player) {
        player.openInventory(pages.get(pageNumber));
    }

    public Inventory get() {
        return pages.isEmpty() ? null : pages.getFirst();
    }
}
