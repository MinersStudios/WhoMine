package com.minersstudios.mscore.utils;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msdecor.MSDecor;
import com.minersstudios.msitem.MSItem;
import org.jetbrains.annotations.Contract;

public final class MSPluginUtils {

    @Contract(value = " -> fail")
    private MSPluginUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * @return True if MSDecor, MSBlock, MSItem have loaded custom decors / blocks / items
     */
    public static boolean isLoadedCustoms() {
        return MSDecor.getInstance().isLoadedCustoms()
                && MSBlock.getInstance().isLoadedCustoms()
                && MSItem.getInstance().isLoadedCustoms();
    }
}
