package com.github.minersstudios.msitem.commands;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msitem.items.RenamesMenu;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@MSCommand(
        command = "renames",
        aliases = {"optifine", "rename", "renameables"},
        usage = " ꀑ §cИспользуй: /<command>",
        description = "Открывает меню с переименованиями предметов"
)
public class RenamesCommand implements MSCommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull ... args) {
        if (args.length > 0) return false;
        if (!(sender instanceof Player player)) {
            ChatUtils.sendError(sender, Component.translatable("ms.error.only_player_command"));
            return true;
        }

        RenamesMenu.open(player);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return literal("renames").build();
    }
}
