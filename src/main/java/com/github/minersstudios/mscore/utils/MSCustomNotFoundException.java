package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msitem.items.CustomItem;
import org.jetbrains.annotations.NotNull;

/**
 * Trows when {@link CustomBlockData} or {@link CustomDecorData} or {@link CustomItem} is not found
 */
public final class MSCustomNotFoundException extends RuntimeException {

    public MSCustomNotFoundException(@NotNull String message) {
        super(message);
    }
}
