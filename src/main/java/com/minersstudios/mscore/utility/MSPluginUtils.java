package com.minersstudios.mscore.utility;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msitem.MSItem;
import org.jetbrains.annotations.Contract;

/**
 * Utility class for {@link MSPlugin} and its modules
 */
public final class MSPluginUtils {

    @Contract(" -> fail")
    private MSPluginUtils() throws AssertionError {
        throw new AssertionError("Utility class");
    }

    /**
     * @return True if MSDecor, MSBlock, MSItem
     *         have loaded its custom decors / blocks / items
     */
    public static boolean isLoadedCustoms() {
        try {
            final MSDecor msDecor = MSDecor.singleton();
            final MSBlock msBlock = MSBlock.singleton();
            final MSItem msItem = MSItem.singleton();

            return msDecor != null
                    && msBlock != null
                    && msItem != null
                    && msDecor.containsStatus(MSDecor.LOADED_DECORATIONS)
                    && msBlock.containsStatus(MSBlock.LOADED_BLOCKS)
                    && msItem.containsStatus(MSItem.LOADED_ITEMS);
        } catch (final NoClassDefFoundError e) {
            return false;
        }
    }
}
