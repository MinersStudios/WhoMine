package com.minersstudios.msessentials.commands.admin.teleport;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.utils.MSPlayerUtils;
import com.minersstudios.msessentials.world.WorldDark;
import com.mojang.brigadier.arguments.StringArgumentType;
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
        command = "worldteleport",
        aliases = {
                "worldtp",
                "wtp",
                "teleportworld",
                "tpworld",
                "tpw"
        },
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [world name] [x] [y] [z]",
        description = "Телепортирует игрока на координаты в указанном мире, если координаты не указаны, телепортирует на точку спавна данного мира",
        permission = "msessentials.worldteleport",
        permissionDefault = PermissionDefault.OP
)
public class WorldTeleportCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("worldteleport")
            .then(
                    argument("id/никнейм", StringArgumentType.word())
                    .then(
                            argument("мир", DimensionArgument.dimension())
                            .then(argument("координаты", Vec3Argument.vec3()))
                    )
            ).build();

    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");
    private static final TranslatableComponent WORLD_NOT_FOUND = translatable("ms.command.world_teleport.world_not_found");
    private static final TranslatableComponent TOO_BIG_COORDINATES = translatable("ms.command.world_teleport.too_big_coordinates");
    private static final TranslatableComponent TELEPORT_SUCCESS = translatable("ms.command.world_teleport.sender.message");

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (args.length < 2) return false;

        PlayerInfo playerInfo = PlayerInfo.fromString(args[0]);

        if (playerInfo == null) {
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        Player player = playerInfo.getOnlinePlayer();

        if (player == null) {
            MSLogger.warning(sender, PLAYER_NOT_FOUND);
            return true;
        }

        World world = sender.getServer().getWorld(args[1]);

        if (world == null) {
            MSLogger.warning(sender, WORLD_NOT_FOUND);
            return true;
        }
        
        double x, y, z;

        if (args.length > 2) {
            try {
                x = Double.parseDouble(args[2]);
                y = Double.parseDouble(args[3]);
                z = Double.parseDouble(args[4]);
            } catch (NumberFormatException e) {
                return false;
            }

            if (x > 29999984 || z > 29999984) {
                MSLogger.warning(sender, TOO_BIG_COORDINATES);
                return true;
            }
        } else {
            Location spawnLoc = world.getSpawnLocation();
            x = spawnLoc.getX();
            y = spawnLoc.getY();
            z = spawnLoc.getZ();
        }

        player.teleportAsync(new Location(world, x, y, z))
        .thenRun(
            () -> MSLogger.fine(
                    sender,
                    TELEPORT_SUCCESS.args(
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
        return switch (args.length) {
            case 1 -> MSPlayerUtils.getLocalPlayerNames();
            case 2 -> {
                Server server = sender.getServer();
                yield server.getWorlds().stream()
                        .filter(world -> !WorldDark.isWorldDark(world))
                        .map(World::getName)
                        .toList();
            }
            case 3, 4, 5 -> {
                Location playerLoc = null;

                if (
                        sender instanceof Player player
                        && args[1].equals(player.getWorld().getName())
                ) {
                    playerLoc = player.getLocation();
                }

                if (playerLoc != null) {
                    double coordinate = switch (args.length) {
                        case 3 -> playerLoc.x();
                        case 4 -> playerLoc.y();
                        default -> playerLoc.z();
                    };
                    double roundedCoordinate = Math.round(coordinate * 100.0d) / 100.0d;

                    yield List.of(String.valueOf(roundedCoordinate));
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
