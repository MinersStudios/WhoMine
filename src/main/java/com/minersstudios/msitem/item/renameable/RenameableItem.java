package com.minersstudios.msitem.item.renameable;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.MSCustomUtils;
import com.minersstudios.msitem.MSItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;

public class RenameableItem {
    private final String key;
    private final RenameCollection renameCollection;
    private final Set<OfflinePlayer> whiteList = new HashSet<>();
    private boolean showInRenameMenu;

    private RenameableItem(
            @NotNull String key,
            @NotNull RenameCollection renameCollection,
            @NotNull Collection<OfflinePlayer> whiteList,
            boolean showInRenameMenu
    ) {
        this.key = key.toLowerCase(Locale.ENGLISH);
        this.renameCollection = renameCollection;
        this.showInRenameMenu = showInRenameMenu;

        this.whiteList.addAll(whiteList);

        if (showInRenameMenu) {
            MSItem.getCache().renameableItemsMenu.add(this);
        }
    }

    public static @NotNull RenameableItem create(
            @NotNull String key,
            boolean showInRenameMenu,
            @NotNull RenameCollection renameCollection,
            @NotNull Collection<OfflinePlayer> whiteList
    ) {
        return new RenameableItem(key, renameCollection, whiteList, showInRenameMenu);
    }

    public static @Nullable RenameableItem fromFile(@NotNull File file) {
        YamlConfiguration renameableItemConfig;

        try {
            renameableItemConfig = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            MSLogger.log(Level.SEVERE, "Failed to load " + file.getName() + "!", e);
            return null;
        }

        String fileName = file.getName();
        String key = renameableItemConfig.getString("key");

        if (key == null) {
            MSLogger.severe("Key is not defined in " + fileName + "!");
            return null;
        }

        RenameCollection renameCollection = new RenameCollection(key);
        var itemsString = renameableItemConfig.getStringList("items");
        var renamesString = renameableItemConfig.getStringList("renames");
        var loreString = renameableItemConfig.getStringList("lore");
        var lore = new LinkedList<Component>();
        int customModelData = renameableItemConfig.getInt("custom-model-data", -1);
        var whiteList = new HashSet<OfflinePlayer>();

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

        for (var item : itemsString) {
            ItemStack itemStack;

            if (item.contains(":")) {
                itemStack = MSCustomUtils.getItemStack(item)
                        .map(ItemStack::new)
                        .orElse(null);
            } else {
                Material material = Material.getMaterial(item);
                itemStack = material == null
                        ? null
                        : new ItemStack(material);
            }

            if (itemStack == null || itemStack.getType().isAir()) {
                MSLogger.severe("Item " + item + " is not valid! (in " + fileName + ")");
                return null;
            }

            if (renameCollection.isEmptyItems()) {
                ItemStack mainItem = itemStack.clone();
                ItemMeta meta = mainItem.getItemMeta();

                if (!lore.isEmpty()) {
                    meta.lore(lore);
                }

                meta.setCustomModelData(customModelData);
                mainItem.setItemMeta(meta);
                renameCollection.setMainItem(mainItem);
            }

            renameCollection.addItem(itemStack);
        }

        for (var uuid : renameableItemConfig.getStringList("white-list")) {
            try {
                whiteList.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
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

    public @NotNull String getKey() {
        return this.key;
    }

    public @NotNull RenameCollection getRenames() {
        return this.renameCollection;
    }

    public @NotNull @UnmodifiableView Set<OfflinePlayer> whiteListedPlayers() {
        return Collections.unmodifiableSet(this.whiteList);
    }

    public void setWhiteListedPlayers(@NotNull Set<OfflinePlayer> whiteList) {
        this.whiteList.clear();
        this.whiteList.addAll(whiteList);
    }

    public boolean isShowInRenameMenu() {
        return this.showInRenameMenu;
    }

    public void setShowInRenameMenu(boolean showInRenameMenu) {
        this.showInRenameMenu = showInRenameMenu;
    }

    public boolean isWhiteListed(@Nullable OfflinePlayer player) {
        return this.whiteList.isEmpty()
                || this.whiteList.contains(player);
    }

    @Contract("null, null -> null")
    public @Nullable ItemStack craftRenamed(
            @Nullable ItemStack item,
            @Nullable String rename
    ) {
        return this.renameCollection.craftRenamed(item, rename);
    }
}
