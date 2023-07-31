package com.minersstudios.msessentials.commands.admin;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.Config;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.world.WorldDark;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "setserverspawn",
        usage = " ꀑ §cИспользуй: /<command> [world name] [x] [y] [z]",
        description = "Устанавливает спавн сервера",
        permission = "msessentials.setserverspawn",
        permissionDefault = PermissionDefault.OP
)
public class SetServerSpawn implements MSCommandExecutor {
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

    private static final TranslatableComponent ONLY_PLAYER_COMMAND = translatable("ms.error.only_player_command");
    private static final TranslatableComponent WORLD_NOT_FOUND = translatable("ms.command.set_server_spawn.world_not_found");
    private static final TranslatableComponent TOO_BIG_COORDINATES = translatable("ms.command.set_server_spawn.too_big_coordinates");
    private static final TranslatableComponent SPAWN_SET = translatable("ms.command.set_server_spawn.successfully_set");

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        Server server = sender.getServer();

        switch (args.length) {
            case 0 -> {
                if (!(sender instanceof Player player)) {
                    MSLogger.warning(sender, ONLY_PLAYER_COMMAND);
                    return true;
                }

                setNewLocation(sender, player.getLocation());
            }
            case 1 -> {
                World world = server.getWorld(args[0]);

                if (world == null) {
                    MSLogger.warning(sender, WORLD_NOT_FOUND);
                    return true;
                }

                setNewLocation(sender, world.getSpawnLocation());
            }
            case 4 -> {
                World world = server.getWorld(args[0]);

                if (world == null) {
                    MSLogger.warning(sender, WORLD_NOT_FOUND);
                    return true;
                }

                double x, y, z;

                try {
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                    z = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    return false;
                }

                if (x > 29999984 || z > 29999984) {
                    MSLogger.warning(sender, TOO_BIG_COORDINATES);
                    return true;
                }

                setNewLocation(sender, new Location(world, x, y, z));
            }
            case 6 -> {
                World world = server.getWorld(args[0]);

                if (world == null) {
                    MSLogger.warning(sender, WORLD_NOT_FOUND);
                    return true;
                }

                double x, y, z;

                try {
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                    z = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    return false;
                }

                if (x > 29999984 || z > 29999984) {
                    MSLogger.warning(sender, TOO_BIG_COORDINATES);
                    return true;
                }

                float yaw, pitch;

                try {
                    yaw = Float.parseFloat(args[4]);
                    pitch = Float.parseFloat(args[5]);
                } catch (NumberFormatException e) {
                    return false;
                }

                setNewLocation(sender, new Location(world, x, y, z, yaw, pitch));
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
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        return switch (args.length) {
            case 1 -> {
                Server server = sender.getServer();
                yield server.getWorlds().stream()
                        .filter(world -> !WorldDark.isWorldDark(world))
                        .map(World::getName)
                        .toList();
            }
            case 2, 3, 4 -> {
                Location playerLoc = null;

                if (
                        sender instanceof Player player
                        && args[0].equals(player.getWorld().getName())
                ) {
                    playerLoc = player.getLocation();
                }

                if (playerLoc != null) {
                    double coordinate = switch (args.length) {
                        case 2 -> playerLoc.x();
                        case 3 -> playerLoc.y();
                        default -> playerLoc.z();
                    };
                    double roundedCoordinate = Math.round(coordinate * 100.0d) / 100.0d;

                    yield List.of(String.valueOf(roundedCoordinate));
                }

                yield EMPTY_TAB;
            }
            case 5, 6 -> {
                Location playerLoc = null;

                if (
                        sender instanceof Player player
                        && args[0].equals(player.getWorld().getName())
                ) {
                    playerLoc = player.getLocation();
                }

                if (playerLoc != null) {
                    float degree = args.length == 5 ? playerLoc.getYaw() : playerLoc.getPitch();
                    float roundedDegree = Math.round(degree * 100.0f) / 100.0f;

                    yield List.of(String.valueOf(roundedDegree));
                }

                yield EMPTY_TAB;
            }
            default -> EMPTY_TAB;
        };
    }

    private static void setNewLocation(
            @NotNull CommandSender sender,
            @NotNull Location location
    ) {
        Config config = MSEssentials.getConfiguration();
        config.spawnLocation = location;

        config.save();
        MSLogger.fine(
                sender,
                SPAWN_SET.args(
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