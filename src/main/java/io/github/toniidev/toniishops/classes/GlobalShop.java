package io.github.toniidev.toniishops.classes;

import io.github.toniidev.toniishops.enums.ShopItemType;
import io.github.toniidev.toniishops.factories.InventoryFactory;
import io.github.toniidev.toniishops.factories.ItemStackFactory;
import io.github.toniidev.toniishops.factories.MultipleInventoryFactory;
import io.github.toniidev.toniishops.factories.StringFactory;
import io.github.toniidev.toniishops.utils.ItemUtils;
import io.github.toniidev.toniishops.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.*;

/**
 * WARNING: This class contains some strings that aren't stored in any separate enum!
 */
public class GlobalShop {
    /**
     * Items of this list are loaded onEnable and
     * are saved onDisable
     * TODO: Save and load this list's items
     */
    public static List<GlobalShopItem> shop = new ArrayList<>();

    public static final Map<String, Double> ORE_PRICES = Map.of(
            "coal", 30.0,
            "iron", 50.0,
            "gold", 40.0,
            "redstone", 70.0,
            "lapis", 60.0,
            "diamond", 500.0,
            "emerald", 1000.0,
            "netherite", 1500.0
    );

    public static final Map<String, Double> DECORATION_PRICES = Map.ofEntries(
            Map.entry("pot", 30.0),
            Map.entry("flower", 10.0),
            Map.entry("banner", 50.0),
            Map.entry("peony", 15.0),
            Map.entry("lilac", 15.0),
            Map.entry("sunflower", 20.0),
            Map.entry("tulip", 12.0),
            Map.entry("dandelion", 8.0),
            Map.entry("cornflower", 12.0),
            Map.entry("allium", 12.0),
            Map.entry("daisy", 10.0),
            Map.entry("poppy", 10.0),
            Map.entry("skull", 100.0),
            Map.entry("item_frame", 40.0),
            Map.entry("painting", 60.0),
            Map.entry("lava", 50.0),
            Map.entry("water", 20.0)
    );

    public static final Map<String, Double> BLOCK_PRICES = Map.ofEntries(
            Map.entry("quartz", 500.0),
            Map.entry("stairs", 250.0),
            Map.entry("smooth", 200.0),
            Map.entry("stone", 50.0),
            Map.entry("cobblestone", 30.0),
            Map.entry("dirt", 10.0),
            Map.entry("grass", 15.0),
            Map.entry("terracotta", 300.0),
            Map.entry("concrete", 500.0),
            Map.entry("glass", 300.0),
            Map.entry("log", 200.0),
            Map.entry("wood", 200.0),
            Map.entry("planks", 50.0),
            Map.entry("glowstone", 200.0),
            Map.entry("lantern", 180.0),
            Map.entry("sandstone", 40.0),
            Map.entry("bricks", 50.0)
    );

    public static final Map<String, Double> FOOD_PRICES = Map.ofEntries(
            Map.entry("bread", 20.0),
            Map.entry("golden", 100.0),
            Map.entry("apple", 10.0),
            Map.entry("cooked", 35.0),
            Map.entry("chicken", 25.0),
            Map.entry("porkchop", 25.0),
            Map.entry("stew", 50.0),
            Map.entry("beetroot", 15.0),
            Map.entry("carrot", 10.0),
            Map.entry("potato", 15.0),
            Map.entry("cake", 60.0),
            Map.entry("cookie", 10.0),
            Map.entry("melon", 15.0),
            Map.entry("pufferfish", 40.0),
            Map.entry("salmon", 30.0),
            Map.entry("clownfish", 40.0),
            Map.entry("berries", 15.0)
    );

    public static final Map<String, Double> ITEM_PRICES = Map.ofEntries(
            Map.entry("stick", 5.0),
            Map.entry("paper", 10.0),
            Map.entry("book", 15.0),
            Map.entry("leather", 20.0),
            Map.entry("string", 15.0),
            Map.entry("feather", 10.0),
            Map.entry("fire_charge", 15.0),
            Map.entry("compass", 50.0),
            Map.entry("clock", 60.0),
            Map.entry("dust", 30.0),
            Map.entry("powder", 40.0),
            Map.entry("slimeball", 50.0),
            Map.entry("gunpowder", 20.0),
            Map.entry("pearl", 100.0),
            Map.entry("wart", 30.0)
    );

    public static final List<String> PROHIBITED_WORDS = List.of(
            "chestplate",
            "leggings",
            "helmet",
            "boots",
            "raw",
            "sword",
            "pickaxe",
            "hoe",
            "axe",
            "shovel",
            "trident",
            "horse",
            "bow",
            "totem",
            "block",
            "bottle",
            "on_a_stick",
            "redstone_wire",
            "redstone_wall_torch",
            "scrap",
            "nugget",
            "redstone_torch",
            "spawn_egg",
            "iron_bars",
            "ore",
            "lamp",
            "powder_snow",
            "powder",
            "seeds",
            "debug_stick",
            "written_book",
            "knowledge_book",
            "smithing",
            "melon_stem",
            "candle_cake",
            "salmon_bucket",
            "pufferfish",
            "poisonous",
            "enchanted_book",
            "potted",
            "wall",
            "skull",
            "pattern",
            "door",
            "trapdoor",
            "orange_banner",
            "magenta_banner",
            "light_blue_banner",
            "yellow_banner",
            "lime_banner",
            "pink_banner",
            "gray_banner",
            "light_gray_banner",
            "cyan_banner",
            "purple_banner",
            "blue_banner",
            "brown_banner",
            "green_banner",
            "red_banner",
            "black_banner",
            "golden_apple"
    );

    public static final List<Material> PROHIBITED_MATERIALS = List.of(
            Material.WATER,
            Material.LAVA,
            Material.LAVA_CAULDRON,
            Material.WATER_CAULDRON,
            Material.NETHER_QUARTZ_ORE,
            Material.QUARTZ
    );

    /**
     * Tells whether shop is selling the specified material or not
     *
     * @param material The material we need to know whether the shop is selling or not
     * @return true if the Material is actually being sold by the Global Shop, false if
     * it isn't
     */
    public static boolean contains(Material material) {
        return getItem(material) != null;
    }

    /**
     * Gets the GlobalShopItem instance linked to the specified material
     *
     * @param material The material we need the GlobalShopItem instance of
     * @return The GlobalShopItem linked to the specified material if it exists,
     * null if it doesn't exist
     */
    @Nullable
    public static GlobalShopItem getItem(Material material) {
        return GlobalShop.shop.stream()
                .filter(x -> x.getMaterial().equals(material))
                .findFirst().orElse(null);
    }

    /**
     * Initializes the shop by adding the specified number of items of all missing materials to it.
     * Excluded items: tools, ore blocks, raw ores
     * TODO: Add ores starting price
     *
     * @param amountOfItemsOfEachMaterialToAdd The exact amount of items of each missing material to add to the market
     */
    public static void initializeShop(long amountOfItemsOfEachMaterialToAdd) {
        int i = 0;

        for (Material material : Material.values()) {
            if (!GlobalShop.contains(material)) {
                if (canSell(material)) {
                    AbstractMap.SimpleEntry<ShopItemType, Double> info = getMaterialInfo(material);
                    assert info != null;

                    GlobalShopItem item = new GlobalShopItem(material, info.getValue(), amountOfItemsOfEachMaterialToAdd,
                            info.getKey());
                    GlobalShop.shop.add(item);
                    Bukkit.getLogger().info("Successfully added " + amountOfItemsOfEachMaterialToAdd + " items of " + material +
                            " to the global shop. Item type: " + item.getShopItemType());
                    i++;
                }
            }
        }

        Bukkit.getLogger().info("Added " + i + " items to the global shop.");
    }

    /**
     * Tells whether the specified Material can be sold on the GlobalShop or not
     *
     * @param material The Material we must know whether it can be sold on the GlobalShop or not
     * @return true if the Material can be sold, false if it can't
     */
    public static boolean canSell(Material material) {
        boolean allowedWord = true;
        for (String word : PROHIBITED_WORDS) {
            if (StringUtils.doesMaterialNameContainString(material.name(), word) ||
                    PROHIBITED_MATERIALS.contains(material)) allowedWord = false;
        }

        return getDefaultPrice(material) != 0.0 && allowedWord;
    }

    /**
     * Gets the default price that the specified material should be sold at
     *
     * @param material The Material we must get the default price of
     * @return The price for which the Material should be sold to if there are no other items of that
     * type on the Market
     */
    private static double getDefaultPrice(Material material) {
        AbstractMap.SimpleEntry<ShopItemType, Double> info = GlobalShop.getMaterialInfo(material);
        if (info == null) return 0.0;

        return info.getValue();
    }

    /**
     * Gets info about the specified material
     *
     * @param material The Material to search the info of
     * @return An AbstractMap whose key is the ShopItemType of the material, and
     * whose value is its price. It returns null if the Material is invalid
     */
    @Nullable
    private static AbstractMap.SimpleEntry<ShopItemType, Double> getMaterialInfo(Material material) {
        String materialName = material.name().toLowerCase();
        List<Map<String, Double>> maps = List.of(FOOD_PRICES, ITEM_PRICES, BLOCK_PRICES, ORE_PRICES, DECORATION_PRICES);
        List<ShopItemType> types = List.of(ShopItemType.FOOD, ShopItemType.ITEM, ShopItemType.BLOCK, ShopItemType.ORE, ShopItemType.DECORATIVE);

        for (int i = 0; i < maps.size(); i++) {
            for (Map.Entry<String, Double> entry : maps.get(i).entrySet()) {
                if (StringUtils.doesMaterialNameContainString(materialName, entry.getKey())) {
                    return new AbstractMap.SimpleEntry<>(types.get(i), entry.getValue());
                }
            }
        }
        return null;
    }

    /**
     * Gets the Inventory to show based on which type of ShopIcon the player has clicked
     * on. This is used on the HomeGUI to display each category inventory and to get the
     * Inventory to show on close when a GlobalShopItem specific inventory gets closed
     *
     * @param type   The ShopItemType to get the general Inventory of
     * @param plugin The main plugin instance
     * @return The Inventory that contains all the Items of the specified type
     */
    public static Inventory getGUI(ShopItemType type, Plugin plugin) {
        Inventory value;

        switch (type) {
            case ITEM -> value = getItemShopGUI(plugin);
            case FOOD -> value = getFoodShopGUI(plugin);
            case DECORATIVE -> value = getDecorationShopGUI(plugin);
            case BLOCK -> value = getBlockShopGUI(plugin);
            case ORE -> value = getOreShopGUI(plugin);
            case null, default -> value = Bukkit.createInventory(null, 9, " ");
        }

        return value;
    }

    /**
     * Gets the Inventory containing all the blocks that the GlobalShop sells
     *
     * @param main The main plugin instance
     * @return The Inventory containing all the blocks that the GlobalShop sells
     */
    private static Inventory getBlockShopGUI(Plugin main) {
        /// This procedure is used to get all the category shops!
        /// Create a list in which there are going to be all the items of the specific category type that the Shop sells
        /// In this case we are talking about blocks
        List<ItemStack> blocks = new ArrayList<>();

        /// Create the factory the MultipleInventory will be based on
        InventoryFactory factory = new InventoryFactory(6, "Block shop", main)
                /// Sets the action to execute on any item of the Inventory (in this case we are talking about MultipleInventory)
                .setGlobalAction(e -> {
                    if (e.getCurrentItem() == null) return;
                    /// Check if the ItemStack can be sold. If an ItemStack has the same name as a new ItemStack that is created
                    /// at the moment with the same material, it means it's not a Custom Item Stack so it can be sold.
                    if (canSell(e.getCurrentItem().getType()) &&
                            ItemUtils.doItemStacksHaveTheSameName(e.getCurrentItem(), new ItemStack(e.getCurrentItem().getType()))) {

                        GlobalShopItem item = GlobalShop.getItem(e.getCurrentItem().getType());
                        assert item != null;
                        /// Display the SpecificItemView on that item
                        e.getWhoClicked().openInventory(item.getSpecificItemView(main, e.getWhoClicked()));
                    }
                })
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(GlobalShop.getHomeGUI(main));

        /// Get all ShopItems, then filter them and only get the items of the category this function is linked to,
        /// then just add each of these to the list we created before
        for (GlobalShopItem item : GlobalShop.shop) {
            if (item.getShopItemType().equals(ShopItemType.BLOCK)) {
                blocks.add(new ItemStackFactory(item.getMaterial())
                        .addLoreLine(StringUtils.formatColorCodes('&', "&8Block"))
                        .addBlankLoreLine()
                        .addLoreLine(new StringFactory()
                                .append("Buy price:").setColor('7')
                                .append(item.getBuyPrice() + "$").setColor('6')
                                .get())
                        .addLoreLine(new StringFactory()
                                .append("Sell price:").setColor('7')
                                .append(item.getSellPrice() + "$").setColor('6')
                                .get())
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to view details!"))
                        .get());
            }
        }

        return new MultipleInventoryFactory(blocks, main, factory)
                .get();
    }

    /**
     * Gets the Inventory containing all the blocks that the GlobalShop sells
     *
     * @param main The main plugin instance
     * @return The Inventory containing all the blocks that the GlobalShop sells
     */
    public static Inventory getOreShopGUI(Plugin main) {
        List<ItemStack> ores = new ArrayList<>();
        InventoryFactory factory = new InventoryFactory(6, "Ore shop", main)
                .setGlobalAction(e -> {
                    if (e.getCurrentItem() == null) return;
                    if (canSell(e.getCurrentItem().getType()) &&
                            ItemUtils.doItemStacksHaveTheSameName(e.getCurrentItem(), new ItemStack(e.getCurrentItem().getType()))) {
                        GlobalShopItem item = GlobalShop.getItem(e.getCurrentItem().getType());
                        assert item != null;
                        e.getWhoClicked().openInventory(item.getSpecificItemView(main, e.getWhoClicked()));
                    }
                })
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(GlobalShop.getHomeGUI(main));

        for (GlobalShopItem item : GlobalShop.shop) {
            if (item.getShopItemType().equals(ShopItemType.ORE)) {
                ores.add(new ItemStackFactory(item.getMaterial())
                        .addLoreLine(StringUtils.formatColorCodes('&', "&8Ore"))
                        .addBlankLoreLine()
                        .addLoreLine(new StringFactory()
                                .append("Buy price:").setColor('7')
                                .append(item.getBuyPrice() + "$").setColor('6')
                                .get())
                        .addLoreLine(new StringFactory()
                                .append("Sell price:").setColor('7')
                                .append(item.getSellPrice() + "$").setColor('6')
                                .get())
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to view details!"))
                        .get());
            }
        }

        return new MultipleInventoryFactory(ores, main, factory)
                .get();
    }

    /**
     * Gets the Inventory containing all the items that the GlobalShop sells. Further
     * comments about the code are contained in GlobalShop.getBlockShopGUI()
     *
     * @param main The main plugin instance
     * @return The Inventory containing all the items that the GlobalShop sells
     */
    public static Inventory getItemShopGUI(Plugin main) {
        List<ItemStack> ores = new ArrayList<>();
        InventoryFactory factory = new InventoryFactory(6, "Item shop", main)
                .setGlobalAction(e -> {
                    if (e.getCurrentItem() == null) return;
                    if (canSell(e.getCurrentItem().getType()) &&
                            ItemUtils.doItemStacksHaveTheSameName(e.getCurrentItem(), new ItemStack(e.getCurrentItem().getType()))) {
                        GlobalShopItem item = GlobalShop.getItem(e.getCurrentItem().getType());
                        assert item != null;
                        e.getWhoClicked().openInventory(item.getSpecificItemView(main, e.getWhoClicked()));
                    }
                })
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(GlobalShop.getHomeGUI(main));

        for (GlobalShopItem item : GlobalShop.shop) {
            if (item.getShopItemType().equals(ShopItemType.ITEM)) {
                if (item.getMaterial() != null) {
                    ores.add(new ItemStackFactory(item.getMaterial())
                            .addLoreLine(StringUtils.formatColorCodes('&', "&8Item"))
                            .addBlankLoreLine()
                            .addLoreLine(new StringFactory()
                                    .append("Buy price:").setColor('7')
                                    .append(item.getBuyPrice() + "$").setColor('6')
                                    .get())
                            .addLoreLine(new StringFactory()
                                    .append("Sell price:").setColor('7')
                                    .append(item.getSellPrice() + "$").setColor('6')
                                    .get())
                            .addBlankLoreLine()
                            .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to view details!"))
                            .get());
                }
            }
        }

        return new MultipleInventoryFactory(ores, main, factory)
                .get();
    }

    /**
     * Gets the Inventory containing all the foods that the GlobalShop sells. Further
     * comments about the code are contained in GlobalShop.getBlockShopGUI()
     *
     * @param main The main plugin instance
     * @return The Inventory containing all the foods that the GlobalShop sells
     */
    public static Inventory getFoodShopGUI(Plugin main) {
        List<ItemStack> foods = new ArrayList<>();
        InventoryFactory factory = new InventoryFactory(6, "Food shop", main)
                .setGlobalAction(e -> {
                    if (e.getCurrentItem() == null) return;
                    if (canSell(e.getCurrentItem().getType()) &&
                            ItemUtils.doItemStacksHaveTheSameName(e.getCurrentItem(), new ItemStack(e.getCurrentItem().getType()))) {
                        GlobalShopItem item = GlobalShop.getItem(e.getCurrentItem().getType());
                        assert item != null;
                        e.getWhoClicked().openInventory(item.getSpecificItemView(main, e.getWhoClicked()));
                    }
                })
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(GlobalShop.getHomeGUI(main));

        for (GlobalShopItem item : GlobalShop.shop) {
            if (item.getShopItemType().equals(ShopItemType.FOOD)) {
                foods.add(new ItemStackFactory(item.getMaterial())
                        .addLoreLine(StringUtils.formatColorCodes('&', "&8Food"))
                        .addBlankLoreLine()
                        .addLoreLine(new StringFactory()
                                .append("Buy price:").setColor('7')
                                .append(item.getBuyPrice() + "$").setColor('6')
                                .get())
                        .addLoreLine(new StringFactory()
                                .append("Sell price:").setColor('7')
                                .append(item.getSellPrice() + "$").setColor('6')
                                .get())
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to view details!"))
                        .get());
            }
        }

        return new MultipleInventoryFactory(foods, main, factory)
                .get();
    }

    /**
     * Gets the Inventory containing all the decorative blocks that the GlobalShop sells. Further
     * comments about the code are contained in GlobalShop.getBlockShopGUI()
     *
     * @param main The main plugin instance
     * @return The Inventory containing all the decorative blocks that the GlobalShop sells
     */
    public static Inventory getDecorationShopGUI(Plugin main) {
        List<ItemStack> foods = new ArrayList<>();
        InventoryFactory factory = new InventoryFactory(6, "Decoration shop", main)
                .setGlobalAction(e -> {
                    if (e.getCurrentItem() == null) return;
                    if (canSell(e.getCurrentItem().getType()) &&
                            ItemUtils.doItemStacksHaveTheSameName(e.getCurrentItem(), new ItemStack(e.getCurrentItem().getType()))) {
                        GlobalShopItem item = GlobalShop.getItem(e.getCurrentItem().getType());
                        assert item != null;
                        e.getWhoClicked().openInventory(item.getSpecificItemView(main, e.getWhoClicked()));
                    }
                })
                .setClicksAllowed(false)
                .setInventoryToShowOnClose(GlobalShop.getHomeGUI(main));

        for (GlobalShopItem item : GlobalShop.shop) {
            if (item.getShopItemType().equals(ShopItemType.DECORATIVE)) {
                foods.add(new ItemStackFactory(item.getMaterial())
                        .addLoreLine(StringUtils.formatColorCodes('&', "&8Decoration"))
                        .addBlankLoreLine()
                        .addLoreLine(new StringFactory()
                                .append("Buy price:").setColor('7')
                                .append(item.getBuyPrice() + "$").setColor('6')
                                .get())
                        .addLoreLine(new StringFactory()
                                .append("Sell price:").setColor('7')
                                .append(item.getSellPrice() + "$").setColor('6')
                                .get())
                        .addBlankLoreLine()
                        .addLoreLine(StringUtils.formatColorCodes('&', "&eClick to view details!"))
                        .get());
            }
        }

        return new MultipleInventoryFactory(foods, main, factory)
                .get();
    }

    /**
     * Get the Inventory to display when a player opens the shop with the related command
     *
     * @param plugin The main Plugin instance
     * @return The Shop Home Inventory
     */
    public static Inventory getHomeGUI(Plugin plugin) {
        return new InventoryFactory(3, "Global shop", plugin)
                .setItem(11, ShopItemType.BLOCK.getIcon())
                .setItem(12, ShopItemType.ORE.getIcon())
                .setItem(13, ShopItemType.ITEM.getIcon())
                .setItem(14, ShopItemType.FOOD.getIcon())
                .setItem(15, ShopItemType.DECORATIVE.getIcon())

                .setAction(11, e -> e.getWhoClicked().openInventory(getGUI(ShopItemType.BLOCK, plugin)))
                .setAction(12, e -> e.getWhoClicked().openInventory(getGUI(ShopItemType.ORE, plugin)))
                .setAction(13, e -> e.getWhoClicked().openInventory(getGUI(ShopItemType.ITEM, plugin)))
                .setAction(14, e -> e.getWhoClicked().openInventory(getGUI(ShopItemType.FOOD, plugin)))
                .setAction(15, e -> e.getWhoClicked().openInventory(getGUI(ShopItemType.DECORATIVE, plugin)))

                .setClicksAllowed(false)
                .fill(new ItemStackFactory(Material.BLACK_STAINED_GLASS_PANE)
                        .setName(" ").get())
                .get();
    }
}
