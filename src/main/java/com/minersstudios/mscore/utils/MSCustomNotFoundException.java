package com.minersstudios.mscore.utils;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msdecor.customdecor.CustomDecorData;
import com.minersstudios.msitem.items.CustomItem;
import org.jetbrains.annotations.NotNull;

/**
 * Trows when {@link CustomBlockData} or {@link CustomDecorData} or {@link CustomItem} is not found
 */
public final class MSCustomNotFoundException extends RuntimeException {

    public MSCustomNotFoundException(@NotNull String message) {
        super(message);
    }
}
