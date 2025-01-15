package io.github.toniidev.toniishops.factories;

import io.github.toniidev.toniishops.interfaces.InventoryInterface;
import io.github.toniidev.toniishops.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inventory factory. This class is going to be used to create any
 * custom inventory with custom properties and custom handling. This
 * class also contains some Listeners, to handle the Inventory actions.
 */
public class InventoryFactory implements Listener {

    /**
     * This list is temporary, it means that it will be reset when the plugin enables or
     * disables. But that's ok, because we don't need its items to be always available: any
     * time we show a Player this Inventory, it is created again.
     */
    private final static List<InventoryFactory> factories = new ArrayList<>();

    /// It cannot be final since if we want to set a new title for this Inventory
    private Inventory inventory;
    /// It cannot be final since the blank constructor does not set this
    private Plugin main;

    private final Map<Integer, Inventory> redirects = new HashMap<>();
    private final Map<Integer, InventoryInterface> actions = new HashMap<>();
    private final List<Integer> slotsThatDoNotAcceptClicks = new ArrayList<>();

    private boolean isClosingAllowed = true;
    private Inventory inventoryToShowOnClose = null;

    /**
     * Initialize a reference to this. It is only used to register Listener
     */
    public InventoryFactory() {

    }

    /**
     * Creates an InventoryFactory starting from an already existing Inventory instance
     *
     * @param existingInventory The existing Inventory instance that the InventoryFactory will work with
     * @param plugin            The main plugin instance
     */
    public InventoryFactory(Inventory existingInventory, Plugin plugin) {
        this.inventory = existingInventory;
        this.main = plugin;

        InventoryFactory.factories.add(this);
    }

    /**
     * Creates an InventoryFactory instance starting from a new Inventory.
     *
     * @param rows   The rows that the Inventory will have. Max rows = 6
     * @param title  The title of the Inventory
     * @param plugin The main plugin instance
     */
    public InventoryFactory(int rows, String title, Plugin plugin) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.main = plugin;

        InventoryFactory.factories.add(this);
    }

    /**
     * Puts the specified ItemStack in the specified slot
     *
     * @param slot      The slot where the ItemStack should be put
     * @param itemStack The ItemStack that should be put in the specified slot
     * @return This InventoryFactory instance
     */
    public InventoryFactory setItem(int slot, ItemStack itemStack) {
        this.inventory.setItem(slot, itemStack);
        return this;
    }

    /**
     * Fills the empty Inventory slots with the specified ItemStack. It is usually white glass.
     *
     * @param itemStack The ItemStack that has to be used to fill the empty Inventory slots
     * @return This InventoryFactory instance
     */
    public InventoryFactory fill(ItemStack itemStack) {
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) == null) this.inventory.setItem(i, itemStack);
        }

        return this;
    }

    /**
     * Sets a new title for the specified Inventory. It makes it by creating a new Inventory
     * and copying its contents
     *
     * @param title The new title for the Inventory
     * @return This InventoryFactory instance
     */
    public InventoryFactory setTitle(String title) {
        this.inventory = InventoryUtils.cloneInventory(this.inventory, title);
        return this;
    }

    /**
     * Sets whether the specified slot of the Inventory that this InventoryFactory instance is editing can be clicked or not.
     * To edit clicks permission status on ALL slots, it must be used %instance%#setClicksAllowed(boolean value)
     *
     * @param slot  The slots of which we must edit the clicks permission status
     * @param value true if we want that clicks are enabled on this slot, false if we want clicks to be disabled on this slot
     * @return This InventoryFactory instance
     */
    public InventoryFactory setClicksAllowed(int slot, boolean value) {
        /// If it says that clicks are not allowed, we add the given slot
        /// to the list of the slots on which clicks are not enabled.
        if (!value) {
            if (!slotsThatDoNotAcceptClicks.contains(slot)) {
                slotsThatDoNotAcceptClicks.add(slot);
            }
        } else { /// if it says that clicks are allowed, we remove this slot from the list
            if (slotsThatDoNotAcceptClicks.contains(slot)) {
                slotsThatDoNotAcceptClicks.remove(slot);
            }
        }

        return this;
    }

    /**
     * Sets whether ALL the slots of the Inventory that this InventoryFactory instance is editing can be clicked or not.
     * To edit clicks permission status on certain slots, it must be used %instance%#setClicksAllowed(int slot, boolean value)
     *
     * @param value true (default value) if the slots can be clicked, false if they can't
     * @return This InventoryFactory instance
     */
    public InventoryFactory setClicksAllowed(boolean value) {
        /// If it says that clicks are not allowed, we add all the Inventory
        /// slots to the list of the slots on which clicks are not enabled.
        if (!value) {
            for (int i = 0; i < this.inventory.getSize(); i++) {
                if (!slotsThatDoNotAcceptClicks.contains(i)) {
                    slotsThatDoNotAcceptClicks.add(i);
                }
            }
        } else { /// if it says that clicks are allowed, we remove all the slots that should be disabled from the list
            for (int i = 0; i < this.inventory.getSize(); i++) {
                if (slotsThatDoNotAcceptClicks.contains(i)) {
                    slotsThatDoNotAcceptClicks.remove(i);
                }
            }
        }

        return this;
    }

    /**
     * Sets whether the Inventory that this InventoryFactory instance is editing can be closed or not
     *
     * @param value true if the Inventory can be close, false if it can't be closed
     * @return This InventoryFactory instance
     */
    public InventoryFactory setClosingAllowed(boolean value) {
        this.isClosingAllowed = value;
        return this;
    }

    /**
     * Sets the Inventory that has to be shown when the Inventory that this InventoryFactory is editing gets closed
     *
     * @param inventory The Inventory that has to be shown on close
     * @return This InventoryFactory instance
     */
    public InventoryFactory setInventoryToShowOnClose(Inventory inventory) {
        this.inventoryToShowOnClose = inventory;
        return this;
    }

    /**
     * Sets an action to execute when the specified slot gets clicked
     *
     * @param slot   The slot to click to execute the specified action
     * @param action The action to execute when the specified slot gets clicked
     * @return This InventoryFactory instance
     */
    public InventoryFactory setAction(int slot, InventoryInterface action) {
        this.actions.put(slot, action);
        return this;
    }

    /**
     * Says whether clicks are enabled on a specified slot or not
     *
     * @param slot The slot on which we must understand whether clicks are enabled or not
     * @return true if clicks are allowed on this slot, false if clicks aren't allowed on this slot
     */
    public boolean getClicksPermissionStatus(int slot) {
        /// slotsThatDoNotAcceptClicks.contains(slot) => !clickPermissionStatus
        /// !slotsThatDoNotAcceptClicks.contains(slot) => clickPermissionStatus
        return !slotsThatDoNotAcceptClicks.contains(slot);
    }

    /**
     * Default getter for this class
     *
     * @return true if closing the inventory is allowed, false if closing the inventory isn't allowed
     */
    public boolean getClosePermissionStatus() {
        return this.isClosingAllowed;
    }

    /**
     * Default getter for this class
     *
     * @return The Inventory that has to be shown when the Inventory that this InventoryFactory instance is editing
     * gets closed. If there is no Inventory that has to be shown on close, it returns null
     */
    @Nullable
    public Inventory getInventoryToShowOnClose() {
        return this.inventoryToShowOnClose;
    }

    /**
     * Default getter for this class
     *
     * @return A Map of Inventories to open when certain slots are clicked. If there are no redirects, it returns null
     */
    @Nullable
    public Map<Integer, Inventory> getRedirects() {
        if (this.redirects.isEmpty()) return null;
        return this.redirects;
    }

    /**
     * Default getter for this class
     *
     * @return A Map of actions to execute when certain slots are clicked. If there are no actions, it returns null
     */
    @Nullable
    public Map<Integer, InventoryInterface> getActions() {
        if (this.actions.isEmpty()) return null;
        return this.actions;
    }

    /**
     * Default getter for this class
     *
     * @return It returns the created Inventory. It should only be called when the Inventory creation comes to an end
     */
    public Inventory get() {
        return this.inventory;
    }

    public Plugin getMainPluginInstance() {
        return this.main;
    }

    /**
     * Gets the InventoryFactory behind an Inventory
     *
     * @param inventory The Inventory we must get the factory behind
     * @return null if the Inventory doesn't have an InventoryFactory, the InventoryFactory
     * behind the specified Inventory if it has one
     */
    @Nullable
    public static InventoryFactory getFactory(Inventory inventory) {
        return InventoryFactory.factories.stream()
                .filter(x -> x.get().equals(inventory))
                .findFirst().orElse(null);
    }

    /**
     * Tells whether the specified Inventory is a custom inventory or not.
     * An Inventory is considered a "custom inventory" if it was built by an InventoryFactory.
     * If it was built with an InventoryFactory, it means "factories" list contains that
     * specified inventory
     *
     * @param inventory The Inventory we must check whether is a custom inventory or not
     * @return true if the Inventory is a custom inventory, false if it isn't
     */
    public static boolean isCustomInventory(Inventory inventory) {
        /// getFactory(inventory) == null => !factories.contains(inventory)
        /// getFactory(inventory) != null => factories.contains(inventory)
        return getFactory(inventory) != null;
    }

    /**
     * Handling InventoryClickEvent to disable clicks on the slots
     * on which clicks are disabled
     */
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!InventoryFactory.isCustomInventory(e.getClickedInventory())) return;

        InventoryFactory factory = InventoryFactory.getFactory(e.getClickedInventory());
        assert factory != null;
        if (!factory.getClicksPermissionStatus(e.getRawSlot())) e.setCancelled(true);
    }

    /**
     * Handling InventoryClickEvent to open Inventories linked to slots.
     * This action is called "redirect"
     */
    @EventHandler
    public void onRedirect(InventoryClickEvent e) {
        if (!InventoryFactory.isCustomInventory(e.getInventory())) return;

        InventoryFactory factory = InventoryFactory.getFactory(e.getInventory());
        assert factory != null;

        if (factory.getRedirects() == null) return;
        if (!factory.getRedirects().containsKey(e.getRawSlot())) return;
        e.getWhoClicked().openInventory(factory.getRedirects().get(e.getRawSlot()));
    }

    /**
     * Handling InventoryClickEvent to execute actions linked to slots
     */
    @EventHandler
    public void onAction(InventoryClickEvent e) {
        if (!InventoryFactory.isCustomInventory(e.getInventory())) return;

        InventoryFactory factory = InventoryFactory.getFactory(e.getInventory());
        assert factory != null;

        if (factory.getActions() == null) return;
        if (!factory.getActions().containsKey(e.getRawSlot())) return;

        factory.getActions().get(e.getRawSlot()).run(e);
    }

    /**
     * Handling InventoryCloseEvent to open Inventory that is scheduled to open
     * when a CustomInventory gets closed
     */
    @EventHandler
    public void onCloseInventoryToShowAnother(InventoryCloseEvent e) {
        if (!InventoryFactory.isCustomInventory(e.getInventory())) return;

        InventoryFactory factory = InventoryFactory.getFactory(e.getInventory());
        assert factory != null;

        if (factory.getInventoryToShowOnClose() == null) return;

        /// We must use a BukkitRunnable because the InventoryCloseEvent is not synced with the server
        /// if I remember well. Btw, if it is synced, it works even in this way, so I prefer to use it
        /// this way
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getPlayer().openInventory(factory.getInventoryToShowOnClose());
            }
        }.runTaskLater(factory.getMainPluginInstance(), 1L);
    }

    /**
     * Handling InventoryCloseEvent to disable Inventory close
     */
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!InventoryFactory.isCustomInventory(e.getInventory())) return;

        InventoryFactory factory = InventoryFactory.getFactory(e.getInventory());
        assert factory != null;

        if (factory.getClosePermissionStatus()) return;

        /// We must use a BukkitRunnable because the InventoryCloseEvent is not synced with the server
        /// if I remember well. Btw, if it is synced, it works even in this way, so I prefer to use it
        /// this way
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getPlayer().openInventory(factory.get());
            }
        }.runTaskLater(factory.getMainPluginInstance(), 1L);
    }
}
