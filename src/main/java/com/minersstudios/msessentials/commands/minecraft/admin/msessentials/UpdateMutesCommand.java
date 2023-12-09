package com.minersstudios.msessentials.commands.minecraft.admin.msessentials;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class UpdateMutesCommand {
    private static final TranslatableComponent UPDATE_MUTES_SUCCESS = translatable("ms.command.msessentials.update_mutes.success");

    public static boolean runCommand(@NotNull CommandSender sender) {
        final long time = System.currentTimeMillis();

        MSEssentials.cache().getMuteMap().reloadMutes();
        MSLogger.fine(sender, UPDATE_MUTES_SUCCESS.args(text(System.currentTimeMillis() - time)));
        return true;
    }
}
