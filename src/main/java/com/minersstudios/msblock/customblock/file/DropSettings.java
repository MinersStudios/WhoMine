package com.minersstudios.msblock.customblock.file;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record DropSettings(
        @NotNull ItemStack item,
        int experience
) {}
