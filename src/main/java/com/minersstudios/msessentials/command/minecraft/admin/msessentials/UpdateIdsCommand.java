package com.minersstudios.msessentials.command.minecraft.admin.msessentials;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class UpdateIdsCommand {
    private static final TranslatableComponent UPDATE_IDS_SUCCESS = translatable("ms.command.msessentials.update_ids.success");

    public static boolean runCommand(
            final @NotNull MSEssentials plugin,
            final @NotNull CommandSender sender
    ) {
        final long time = System.currentTimeMillis();
        final Cache cache = plugin.getCache();

        cache.getIdMap().reloadIds();
        cache.getPlayerInfoMap().playerInfos().forEach(PlayerInfo::initNames);
        MSLogger.fine(sender, UPDATE_IDS_SUCCESS.args(text(System.currentTimeMillis() - time)));

        return true;
    }
}
