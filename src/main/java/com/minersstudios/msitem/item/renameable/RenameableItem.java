package com.minersstudios.msitem.item.renameable;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.util.MSCustomUtils;
import com.minersstudios.msitem.MSItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;

/**
 * The RenameableItem class represents a renameable item with
 * associated renames, white-listed players, and menu visibility.
 * It facilitates the creation, configuration, and management of
 * renameable items.
 */
public class RenameableItem {
    private final String key;
    private final RenameCollection renameCollection;
    private final Set<UUID> whiteList;
    private boolean showInRenameMenu;

    private RenameableItem(
            final @NotNull String key,
            final @NotNull RenameCollection renameCollection,
            final @NotNull Collection<UUID> whiteList,
            final boolean showInRenameMenu
    ) {
        this.key = key.toLowerCase(Locale.ENGLISH);
        this.renameCollection = renameCollection;
        this.showInRenameMenu = showInRenameMenu;
        this.whiteList = new HashSet<>(whiteList);

        if (showInRenameMenu) {
            MSItem.getCache().renameableItemsMenu.add(this);
        }
    }

    /**
     * Creates a new RenameableItem instance with the provided
     * parameters
     *
     * @param key              The unique key associated with
     *                         the item
     * @param showInRenameMenu Whether the item should appear
     *                         in the rename menu
     * @param renameCollection The collection of renames
     *                         associated with the item
     * @param whiteList        The set of players that are
     *                         allowed to rename the item
     *                         (empty set means all players
     *                         are allowed)
     * @return A new RenameableItem instance
     */
    public static @NotNull RenameableItem create(
            final @NotNull String key,
            final boolean showInRenameMenu,
            final @NotNull RenameCollection renameCollection,
            final @NotNull Collection<UUID> whiteList
    ) {
        return new RenameableItem(key, renameCollection, whiteList, showInRenameMenu);
    }

    /**
     * Creates a RenameableItem instance by loading its
     * configuration from a file
     *
     * @param file The configuration file to load
     * @return A RenameableItem instance loaded from the file,
     *         or null if loading fails
     */
    public static @Nullable RenameableItem fromFile(final @NotNull File file) {
        final YamlConfiguration renameableItemConfig;

        try {
            renameableItemConfig = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            MSLogger.log(Level.SEVERE, "Failed to load " + file.getName() + "!", e);
            return null;
        }

        final String fileName = file.getName();
        final String key = renameableItemConfig.getString("key");

        if (key == null) {
            MSLogger.severe("Key is not defined in " + fileName + "!");
            return null;
        }

        final RenameCollection renameCollection = new RenameCollection(key);
        final var itemsString = renameableItemConfig.getStringList("items");
        final var renamesString = renameableItemConfig.getStringList("renames");
        final var loreString = renameableItemConfig.getStringList("lore");
        final var lore = new ArrayList<Component>();
        final int customModelData = renameableItemConfig.getInt("custom-model-data", -1);
        final var whiteList = new HashSet<UUID>();

        if (customModelData < 0) {
            MSLogger.severe("Custom model data is not valid! (in " + fileName + ")");
            return null;
        }

        if (!loreString.isEmpty()) {
            for (var text : loreString) {
                lore.add(text(text));
            }
        }

        if (itemsString.isEmpty()) {
            itemsString.add(renameableItemConfig.getString("items"));
        }

        if (renamesString.isEmpty()) {
            renamesString.add(renameableItemConfig.getString("renames"));
        }

        for (var rename : renamesString) {
            renameCollection.addRename(rename);
        }

        for (final var item : itemsString) {
            final ItemStack itemStack;

            if (item.contains(":")) {
                itemStack = MSCustomUtils.getItemStack(item)
                        .map(ItemStack::new)
                        .orElse(null);
            } else {
                final Material material = Material.getMaterial(item);
                itemStack = material == null
                        ? null
                        : new ItemStack(material);
            }

            if (itemStack == null || itemStack.getType().isAir()) {
                MSLogger.severe("Item " + item + " is not valid! (in " + fileName + ")");
                return null;
            }

            if (renameCollection.isEmptyItems()) {
                final ItemStack mainItem = itemStack.clone();
                final ItemMeta meta = mainItem.getItemMeta();

                if (!lore.isEmpty()) {
                    meta.lore(lore);
                }

                meta.setCustomModelData(customModelData);
                mainItem.setItemMeta(meta);
                renameCollection.setMainItem(mainItem);
            }

            renameCollection.addItem(itemStack);
        }

        for (final var uuid : renameableItemConfig.getStringList("white-list")) {
            try {
                whiteList.add(UUID.fromString(uuid));
            } catch (IllegalArgumentException e) {
                MSLogger.severe("Invalid UUID " + uuid + " in white-list! (in " + fileName + ")");
            }
        }

        return new RenameableItem(
                key,
                renameCollection,
                whiteList,
                renameableItemConfig.getBoolean("show-in-rename-menu")
        );
    }

    /**
     * @return The unique key associated with the item
     */
    public @NotNull String getKey() {
        return this.key;
    }

    /**
     * @return The collection of renames associated with the item
     */
    public @NotNull RenameCollection getRenames() {
        return this.renameCollection;
    }

    /**
     * @return The set of uuids of players that are allowed to rename
     *         the item (empty set means all players are allowed)
     */
    public @NotNull Set<UUID> getWhiteList() {
        return this.whiteList;
    }

    /**
     * @return Whether the item should appear in the rename menu
     */
    public boolean isShowInRenameMenu() {
        return this.showInRenameMenu;
    }

    /**
     * Sets whether the item should appear in the rename menu
     *
     * @param showInRenameMenu The new value for the property
     */
    public void setShowInRenameMenu(final boolean showInRenameMenu) {
        this.showInRenameMenu = showInRenameMenu;
    }

    /**
     * @param player The player to check
     * @return Whether the player is allowed to rename the item
     *         (true if the white-list is empty)
     */
    public boolean isWhiteListed(final @Nullable OfflinePlayer player) {
        return this.whiteList.isEmpty()
                || (
                        player != null
                        && this.whiteList.contains(player.getUniqueId())
                );
    }

    /**
     * Creates a copy of the given item with the rename metadata
     * applied to it
     *
     * @param item   The item to get the rename for
     * @param rename The rename to get the item for
     * @return The item with the rename metadata applied to it,
     *         or null if the item is null, the rename is null
     *         or blank, the rename collection is not initialized,
     *         or the item is air
     * @see RenameCollection#craftRenamed(ItemStack, String)
     */
    @Contract("null, null -> null")
    public @Nullable ItemStack craftRenamed(
            final @Nullable ItemStack item,
            final @Nullable String rename
    ) {
        return this.renameCollection.craftRenamed(item, rename);
    }
}
