package com.minersstudios.msitem.utils;

import com.minersstudios.mscore.MSCore;
import com.minersstudios.msitem.items.RenameableItem;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class CustomItemUtils {
    private CustomItemUtils() {
        throw new IllegalStateException("Utility class");
    }

    @Contract("null, null -> null")
    public static @Nullable RenameableItem getRenameableItem(
            @Nullable ItemStack itemStack,
            @Nullable String renameText
    ) {
        if (
                itemStack == null
                || renameText == null
        ) return null;

        for (var renameableItem : MSCore.getCache().renameableItemMap.values()) {
            for (var renameableItemStack : renameableItem.getRenameableItemStacks()) {
                if (
                        renameableItemStack.getType() == itemStack.getType()
                        && StringUtils.startsWithIgnoreCase(renameText, renameableItem.getRenameText())
                ) {
                    return renameableItem;
                }
            }
        }
        return null;
    }
}
