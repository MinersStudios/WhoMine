package com.minersstudios.msessentials.command.impl.minecraft.admin.teleport;

import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.utility.MSPlayerUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.minersstudios.mscore.locale.Translations.*;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "worldteleport",
        aliases = {
                "worldtp",
                "wtp",
                "teleportworld",
                "tpworld",
                "tpw"
        },
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [id/никнейм] [world name] [x] [y] [z]",
        description = "Телепортирует игрока на координаты в указанном мире, если координаты не указаны, телепортирует на точку спавна данного мира",
        permission = "msessentials.worldteleport",
        permissionDefault = PermissionDefault.OP
)
public final class WorldTeleportCommand extends AbstractCommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("worldteleport")
            .then(
                    argument("id/никнейм", StringArgumentType.word())
                    .then(
                            argument("мир", DimensionArgument.dimension())
                            .then(argument("координаты", Vec3Argument.vec3()))
                    )
            ).build();
    private static final int MAX_COORDINATE = 29999984;

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        if (args.length < 2) {
            return false;
        }

        final PlayerInfo playerInfo = PlayerInfo.fromString(this.getPlugin(), args[0]);

        if (playerInfo == null) {
            MSLogger.severe(
                    sender,
                    ERROR_PLAYER_NOT_FOUND.asTranslatable()
            );

            return true;
        }

        final Player player = playerInfo.getOnlinePlayer();

        if (player == null) {
            MSLogger.severe(
                    sender,
                    ERROR_PLAYER_NOT_FOUND.asTranslatable()
            );

            return true;
        }

        final World world = sender.getServer().getWorld(args[1]);

        if (world == null) {
            MSLogger.severe(
                    sender,
                    COMMAND_WORLD_TELEPORT_WORLD_NOT_FOUND.asTranslatable()
            );

            return true;
        }

        final double x;
        final double y;
        final double z;

        if (args.length > 2) {
            try {
                x = Double.parseDouble(args[2]);
                y = Double.parseDouble(args[3]);
                z = Double.parseDouble(args[4]);
            } catch (final NumberFormatException e) {
                return false;
            }

            if (
                    x > MAX_COORDINATE
                    || z > MAX_COORDINATE
            ) {
                MSLogger.severe(
                        sender,
                        COMMAND_WORLD_TELEPORT_TOO_BIG_COORDINATES.asTranslatable()
                );

                return true;
            }
        } else {
            final Location spawnLoc = world.getSpawnLocation();
            x = spawnLoc.getX();
            y = spawnLoc.getY();
            z = spawnLoc.getZ();
        }

        player.teleportAsync(new Location(world, x, y, z))
        .thenRun(
            () -> MSLogger.fine(
                    sender,
                    COMMAND_WORLD_TELEPORT_SENDER_MESSAGE.asTranslatable()
                    .arguments(
                            playerInfo.getGrayIDGreenName(),
                            text(playerInfo.getNickname()),
                            text(world.getName()),
                            text(x),
                            text(y),
                            text(z)
                    )
            )
        );

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        final MSEssentials plugin = this.getPlugin();

        return switch (args.length) {
            case 1 -> MSPlayerUtils.getLocalPlayerNames(plugin);
            case 2 -> {
                final var names = new ObjectArrayList<String>();

                for (final var world : sender.getServer().getWorlds()) {
                    if (!plugin.getCache().getWorldDark().isWorldDark(world)) {
                        names.add(world.getName());
                    }
                }

                yield names;
            }
            case 3, 4, 5 -> {
                Location playerLoc = null;

                if (
                        sender instanceof final Player player
                        && args[1].equals(player.getWorld().getName())
                ) {
                    playerLoc = player.getLocation();
                }

                if (playerLoc != null) {
                    final double coordinate = switch (args.length) {
                        case 3 -> playerLoc.x();
                        case 4 -> playerLoc.y();
                        default -> playerLoc.z();
                    };
                    final double roundedCoordinate = Math.round(coordinate * 100.0d) / 100.0d;

                    yield Collections.singletonList(String.valueOf(roundedCoordinate));
                }

                yield EMPTY_TAB;
            }
            default -> EMPTY_TAB;
        };
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
