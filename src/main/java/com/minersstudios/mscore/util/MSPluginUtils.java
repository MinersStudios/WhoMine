package com.minersstudios.mscore.util;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msitem.MSItem;
import org.jetbrains.annotations.Contract;

/**
 * Utility class for {@link MSPlugin} and its modules
 */
public final class MSPluginUtils {

    @Contract(value = " -> fail")
    private MSPluginUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * @return True if MSDecor, MSBlock, MSItem
     *         have loaded its custom decors / blocks / items
     * @see MSDecor#isLoadedCustoms()
     * @see MSBlock#isLoadedCustoms()
     * @see MSItem#isLoadedCustoms()
     */
    public static boolean isLoadedCustoms() {
        MSPlugin msDecor = MSDecor.getInstance();
        MSPlugin msBlock = MSBlock.getInstance();
        MSPlugin msItem = MSItem.getInstance();
        return msDecor != null
                && msBlock != null
                && msItem != null
                && msDecor.isLoadedCustoms()
                && msBlock.isLoadedCustoms()
                && msItem.isLoadedCustoms();
    }
}
