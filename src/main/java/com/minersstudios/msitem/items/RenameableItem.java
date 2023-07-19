package com.minersstudios.msitem.items;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.mscore.utils.MSItemUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.minersstudios.mscore.utils.ChatUtils.createDefaultStyledText;

public class RenameableItem {
    private @NotNull NamespacedKey namespacedKey;
    private @NotNull String renameText;
    private List<ItemStack> renameableItemStacks;
    private @NotNull ItemStack resultItemStack;
    private boolean showInRenameMenu;
    private final @NotNull Set<OfflinePlayer> whiteList = new HashSet<>();

    public RenameableItem(
            @NotNull NamespacedKey namespacedKey,
            @NotNull String renameText,
            @NotNull List<ItemStack> renameableItemStacks,
            @NotNull ItemStack resultItemStack,
            boolean showInRenameMenu,
            @NotNull Set<OfflinePlayer> whiteList
    ) {
        this.namespacedKey = namespacedKey;
        this.renameText = renameText;
        this.showInRenameMenu = showInRenameMenu;
        this.renameableItemStacks = renameableItemStacks;
        this.resultItemStack = resultItemStack;
        ItemMeta itemMeta = this.resultItemStack.getItemMeta();
        itemMeta.displayName(createDefaultStyledText(renameText));
        itemMeta.getPersistentDataContainer().set(
                MSItemUtils.CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.getNamespacedKey().getKey()
        );
        this.resultItemStack.setItemMeta(itemMeta);
        this.whiteList.addAll(whiteList);
        if (showInRenameMenu) {
            MSCore.getCache().renameableItemsMenu.add(this);
        }
    }

    @Contract("null, null -> null")
    public @Nullable ItemStack createRenamedItem(@Nullable ItemStack itemStack, @Nullable String renameText) {
        if (renameText == null || itemStack == null) return null;
        ItemStack newItemStack = itemStack.clone();
        ItemMeta itemMeta = newItemStack.getItemMeta();
        itemMeta.displayName(createDefaultStyledText(renameText));
        itemMeta.lore(this.resultItemStack.lore());
        itemMeta.setCustomModelData(this.resultItemStack.getItemMeta().getCustomModelData());
        itemMeta.getPersistentDataContainer().set(
                MSItemUtils.CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.getNamespacedKey().getKey()
        );
        newItemStack.setItemMeta(itemMeta);
        return newItemStack;
    }

    public @NotNull NamespacedKey getNamespacedKey() {
        return this.namespacedKey;
    }

    public void setNamespacedKey(@NotNull NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public @NotNull String getRenameText() {
        return this.renameText;
    }

    public void setRenameText(@NotNull String renameText) {
        this.renameText = renameText;
    }

    public @NotNull List<ItemStack> getRenameableItemStacks() {
        return this.renameableItemStacks;
    }

    public void setRenameableItemStacks(@NotNull List<ItemStack> renameableItemStack) {
        this.renameableItemStacks = renameableItemStack;
    }

    public @NotNull ItemStack getResultItemStack() {
        return this.resultItemStack;
    }

    public void setResultItemStack(@NotNull ItemStack resultItemStack) {
        this.resultItemStack = resultItemStack;
    }

    public boolean isShowInRenameMenu() {
        return this.showInRenameMenu;
    }

    public void setShowInRenameMenu(boolean showInRenameMenu) {
        this.showInRenameMenu = showInRenameMenu;
    }

    public boolean isWhiteListed(@Nullable OfflinePlayer player) {
        return this.whiteList.isEmpty() || this.whiteList.contains(player);
    }

    public @NotNull Set<OfflinePlayer> getWhiteListedPlayers() {
        return this.whiteList;
    }
}
