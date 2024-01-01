package com.minersstudios.mscustoms.command.item;

import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.menu.RenamesMenu;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@Command(
        command = "renames",
        aliases = {"optifine", "rename", "renameables"},
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command>",
        description = "Открывает меню с переименованиями предметов",
        playerOnly = true
)
public final class RenamesCommand extends AbstractCommandExecutor<MSCustoms> {
    private static final CommandNode<?> COMMAND_NODE = literal("renames").build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        RenamesMenu.open((Player) sender);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
