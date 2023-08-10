package com.minersstudios.msessentials.commands.admin.ban;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "unban",
        aliases = {"pardon"},
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм]",
        description = "Разбанить игрока",
        permission = "msessentials.ban",
        permissionDefault = PermissionDefault.OP
)
public class UnBanCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("unban")
            .then(argument("id/никнейм", StringArgumentType.word()))
            .build();

    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length == 0) return false;

        PlayerInfo playerInfo = PlayerInfo.fromString(args[0]);

        if (playerInfo == null) {
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        playerInfo.pardon(sender);
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length == 1) {
            var completions = new ArrayList<String>();
            ProfileBanList banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
            Set<BanEntry<PlayerProfile>> entries = banList.getEntries();

            for (var entry : entries) {
                PlayerProfile playerProfile = entry.getBanTarget();

                if (
                        playerProfile.getName() != null
                        && playerProfile.getId() != null
                ) {
                    UUID uuid = playerProfile.getId();
                    String name = playerProfile.getName();
                    int id = PlayerInfo.fromProfile(uuid, name).getID(false, false);

                    if (id != -1) {
                        completions.add(String.valueOf(id));
                    }

                    completions.add(name);
                }
            }

            return completions;
        }

        return EMPTY_TAB;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
