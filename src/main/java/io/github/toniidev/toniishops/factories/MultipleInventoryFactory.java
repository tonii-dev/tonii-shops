package io.github.toniidev.toniishops.factories;

import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class MultipleInventoryFactory {
    private final Plugin main;
    private final List<Inventory> pages = new ArrayList<>();

    public MultipleInventoryFactory(List<ItemStack> items, String title, Plugin plugin, InventoryFactory startFactory) {
        this.main = plugin;

        /// (Used Chat GPT to use these Math# methods)
        /// Suddividere gli oggetti in gruppi di 21
        int totalPages = (int) Math.ceil((double) items.size() / 21);
        List<ItemStack> currentItems = new ArrayList<>();

        /// Create pages
        for (int pageNumber = 0; pageNumber < totalPages; pageNumber++) {
            int startIndex = pageNumber * 21;
            int endIndex = Math.min(startIndex + 21, items.size());
            currentItems = items.subList(startIndex, endIndex);

            /// Creazione dell'inventario per la pagina corrente
            InventoryFactory pageInventory = getBlankPageTemplate()
                    .addItem(currentItems);

            pages.add(finalizePage(pageNumber, title, items.size(), pageInventory));
        }
    }

    private InventoryFactory getBlankPageTemplate() {
        InventoryFactory factory = new InventoryFactory(6, " ", main)
                .fill(new ItemStackFactory(Material.WHITE_STAINED_GLASS_PANE)
                        .setName(" ").get());

        for (int i = 10; i < 17; i++) {
            factory.setItem(i, new ItemStack(Material.AIR));
        }
        for (int i = 19; i < 26; i++) {
            factory.setItem(i, new ItemStack(Material.AIR));
        }
        for (int i = 28; i < 35; i++) {
            factory.setItem(i, new ItemStack(Material.AIR));
        }

        return factory;
    }

    private Inventory finalizePage(int pageNumber, String title, int totalPages, InventoryFactory template) {
        template.setTitle(title);
        template.setItem(49, new ItemStackFactory(Material.COMPASS)
                .setName(StringUtils.formatColorCodes('&', "&eInventory page"))
                .addLoreLine(new StringFactory()
                        .append("Current page:").setColor('7')
                        .append(String.valueOf(pageNumber + 1)).setColor('f')
                        .get())
                .get());

        if (pageNumber > 0) {
            template.setItem(46, new ItemStackFactory(Material.RED_STAINED_GLASS_PANE)
                            .setName(StringUtils.formatColorCodes('&', "&cPrevious page"))
                            .addLoreLine(new StringFactory()
                                    .append("Navigate back to page").setColor('7')
                                    .append(String.valueOf(pageNumber)).setColor('e')
                                    .get())
                            .get())
                    .setAction(46, e -> {
                        e.getWhoClicked().openInventory(pages.get(pageNumber - 1));
                    });
            ;
        }
        if (pageNumber < totalPages - 1) {
            template.setItem(52, new ItemStackFactory(Material.GREEN_STAINED_GLASS_PANE)
                            .setName(StringUtils.formatColorCodes('&', "&aNext page"))
                            .addLoreLine(new StringFactory()
                                    .append("Navigate to page").setColor('7')
                                    .append(String.valueOf(pageNumber + 2)).setColor('e')
                                    .get())
                            .get())
                    .setAction(52, e -> {
                        e.getWhoClicked().openInventory(pages.get(pageNumber + 1));
                    });
        }

        return template
                .get();
    }

    public Inventory get() {
        return pages.get(0);
    }
}
