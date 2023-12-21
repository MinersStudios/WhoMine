package com.minersstudios.msessentials.command.impl.minecraft.admin;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.Config;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@Command(
        command = "setserverspawn",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [world name] [x] [y] [z]",
        description = "Устанавливает спавн сервера",
        permission = "msessentials.setserverspawn",
        permissionDefault = PermissionDefault.OP
)
public final class SetServerSpawn extends CommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("setserverspawn")
            .then(
                    argument("мир", DimensionArgument.dimension())
                    .then(
                            argument("координаты", Vec3Argument.vec3())
                            .then(
                                    argument("yaw", DoubleArgumentType.doubleArg())
                                    .then(argument("pitch", DoubleArgumentType.doubleArg()))
                            )
                    )
            )
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        final Config config = this.getPlugin().getConfiguration();
        final Server server = sender.getServer();

        switch (args.length) {
            case 0 -> {
                if (!(sender instanceof final Player player)) {
                    MSLogger.warning(
                            sender,
                            ERROR_ONLY_PLAYER_COMMAND
                    );
                    return true;
                }

                setNewLocation(config, sender, player.getLocation());
            }
            case 1 -> {
                final World world = server.getWorld(args[0]);

                if (world == null) {
                    MSLogger.warning(
                            sender,
                            COMMAND_SET_SERVER_SPAWN_WORLD_NOT_FOUND
                    );
                    return true;
                }

                setNewLocation(config, sender, world.getSpawnLocation());
            }
            case 4 -> {
                final World world = server.getWorld(args[0]);

                if (world == null) {
                    MSLogger.warning(
                            sender,
                            COMMAND_SET_SERVER_SPAWN_WORLD_NOT_FOUND
                    );
                    return true;
                }

                final double x, y, z;

                try {
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                    z = Double.parseDouble(args[3]);
                } catch (final NumberFormatException e) {
                    return false;
                }

                if (x > 29999984 || z > 29999984) {
                    MSLogger.warning(
                            sender,
                            COMMAND_SET_SERVER_SPAWN_TOO_BIG_COORDINATES
                    );
                    return true;
                }

                setNewLocation(config, sender, new Location(world, x, y, z));
            }
            case 6 -> {
                final World world = server.getWorld(args[0]);

                if (world == null) {
                    MSLogger.warning(
                            sender,
                            COMMAND_SET_SERVER_SPAWN_WORLD_NOT_FOUND
                    );
                    return true;
                }

                final double x, y, z;

                try {
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                    z = Double.parseDouble(args[3]);
                } catch (final NumberFormatException e) {
                    return false;
                }

                if (x > 29999984 || z > 29999984) {
                    MSLogger.warning(
                            sender,
                            COMMAND_SET_SERVER_SPAWN_TOO_BIG_COORDINATES
                    );
                    return true;
                }

                final float yaw, pitch;

                try {
                    yaw = Float.parseFloat(args[4]);
                    pitch = Float.parseFloat(args[5]);
                } catch (final NumberFormatException e) {
                    return false;
                }

                setNewLocation(config, sender, new Location(world, x, y, z, yaw, pitch));
            }
            default -> {
                return false;
            }
        }

        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }

    @Override
    public @NotNull List<String> onTabComplete(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        return switch (args.length) {
            case 1 -> {
                final var names = new ArrayList<String>();

                for (final var world : sender.getServer().getWorlds()) {
                    if (!WorldDark.isWorldDark(world)) {
                        names.add(world.getName());
                    }
                }

                yield names;
            }
            case 2, 3, 4 -> {
                Location playerLoc = null;

                if (
                        sender instanceof final Player player
                        && args[0].equals(player.getWorld().getName())
                ) {
                    playerLoc = player.getLocation();
                }

                if (playerLoc != null) {
                    final double coordinate = switch (args.length) {
                        case 2 -> playerLoc.x();
                        case 3 -> playerLoc.y();
                        default -> playerLoc.z();
                    };
                    final double roundedCoordinate = Math.round(coordinate * 100.0d) / 100.0d;

                    yield ImmutableList.of(String.valueOf(roundedCoordinate));
                }

                yield EMPTY_TAB;
            }
            case 5, 6 -> {
                Location playerLoc = null;

                if (
                        sender instanceof final Player player
                        && args[0].equals(player.getWorld().getName())
                ) {
                    playerLoc = player.getLocation();
                }

                if (playerLoc != null) {
                    final float degree = args.length == 5 ? playerLoc.getYaw() : playerLoc.getPitch();
                    final float roundedDegree = Math.round(degree * 100.0f) / 100.0f;

                    yield ImmutableList.of(String.valueOf(roundedDegree));
                }

                yield EMPTY_TAB;
            }
            default -> EMPTY_TAB;
        };
    }

    private static void setNewLocation(
            final @NotNull Config config,
            final @NotNull CommandSender sender,
            final @NotNull Location location
    ) {
        config.setSpawnLocation(location);
        MSLogger.fine(
                sender,
                COMMAND_SET_SERVER_SPAWN_SUCCESSFULLY_SET
                .args(
                        text(location.getWorld().getName()),
                        text(String.valueOf(location.x())),
                        text(String.valueOf(location.y())),
                        text(String.valueOf(location.z())),
                        text(String.valueOf(location.getYaw())),
                        text(String.valueOf(location.getPitch()))
                )
        );
    }
}
