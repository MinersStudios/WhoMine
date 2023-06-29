package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.msdecor.MSDecor;
import com.github.minersstudios.msitem.MSItem;
import org.jetbrains.annotations.Contract;

@SuppressWarnings("unused")
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
