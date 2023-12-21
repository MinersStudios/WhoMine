package com.minersstudios.msessentials.command.impl.minecraft.admin.ban;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ban.ProfileBanList;
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

@Command(
        command = "unban",
        aliases = {"pardon"},
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [id/никнейм]",
        description = "Разбанить игрока",
        permission = "msessentials.ban",
        permissionDefault = PermissionDefault.OP
)
public final class UnBanCommand extends CommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("unban")
            .then(argument("id/никнейм", StringArgumentType.word()))
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length == 0) {
            return false;
        }

        final PlayerInfo playerInfo = PlayerInfo.fromString(this.getPlugin(), args[0]);

        if (playerInfo == null) {
            MSLogger.severe(
                    sender,
                    LanguageRegistry.Components.ERROR_PLAYER_NOT_FOUND
            );
            return true;
        }

        playerInfo.pardon(sender);

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length == 1) {
            final var completions = new ArrayList<String>();
            final PlayerInfoMap playerInfoMap = this.getPlugin().getCache().getPlayerInfoMap();
            final ProfileBanList banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
            final Set<BanEntry<PlayerProfile>> entries = banList.getEntries();

            for (final var entry : entries) {
                final PlayerProfile playerProfile = entry.getBanTarget();
                final UUID uuid = playerProfile.getId();
                final String name = playerProfile.getName();

                if (
                        name != null
                        && uuid != null
                ) {
                    final int id =
                            playerInfoMap
                            .get(uuid, name)
                            .getID(false, false);

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
