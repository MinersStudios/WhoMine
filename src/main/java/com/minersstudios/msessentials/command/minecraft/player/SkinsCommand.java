package com.minersstudios.msessentials.command.minecraft.player;

import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.menu.SkinsMenu;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Command(
        command = "skins",
        aliases = {"skins"},
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command>",
        description = "Открывает меню с крафтами кастомных предметов/декора/блоков",
        playerOnly = true
)
public final class SkinsCommand extends CommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE = LiteralArgumentBuilder.literal("skins").build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        SkinsMenu.open(this.getPlugin(), (Player) sender);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}