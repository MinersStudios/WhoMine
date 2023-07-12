package com.github.minersstudios.msessentials.commands.teleport;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        usage = " ꀑ §cИспользуй: /<command> [id/никнейм] [world name] [x] [y] [z]",
        description = "Телепортирует игрока на координаты в указанном мире, если координаты не указаны, телепортирует на точку спавна данного мира",
        permission = "msessentials.worldteleport",
        permissionDefault = PermissionDefault.OP
)
public class WorldTeleportCommand implements MSCommandExecutor {
    private static final String coordinatesRegex = "^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$";
    private static final CommandNode<?> COMMAND_NODE =
            literal("worldteleport")
            .then(
                    argument("id/никнейм", StringArgumentType.word())
                    .then(
                            argument("мир", DimensionArgument.dimension())
                            .then(argument("координаты", Vec3Argument.vec3()))
                    )
            ).build();

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
            ChatUtils.sendError(sender, Component.translatable("ms.error.player_not_found"));
            return true;
        }

        OfflinePlayer offlinePlayer = playerInfo.getOfflinePlayer();
        Player player = offlinePlayer.getPlayer();

        if (player == null) {
            ChatUtils.sendWarning(sender, Component.translatable("ms.error.player_not_online"));
            return true;
        }

        World world = Bukkit.getWorld(args[1]);

        if (world == null) {
            ChatUtils.sendWarning(sender, Component.translatable("ms.command.world_teleport.world_not_found"));
            return true;
        }

        Location spawnLoc = world.getSpawnLocation();
        double x = spawnLoc.getX();
        double y = spawnLoc.getY();
        double z = spawnLoc.getZ();

        if (args.length > 2) {
            if (
                    args.length != 5
                    || !args[2].matches(coordinatesRegex)
                    || !args[3].matches(coordinatesRegex)
                    || !args[4].matches(coordinatesRegex)
            ) return false;

            try {
                x = Double.parseDouble(args[2]);
                y = Double.parseDouble(args[3]);
                z = Double.parseDouble(args[4]);
            } catch (NumberFormatException e) {
                return false;
            }

            if (x > 29999984 || z > 29999984) {
                ChatUtils.sendWarning(sender, Component.translatable("ms.command.world_teleport.too_big_coordinates"));
                return true;
            }
        }

        player.teleportAsync(new Location(world, x, y, z), PlayerTeleportEvent.TeleportCause.PLUGIN);
        ChatUtils.sendFine(
                sender,
                Component.translatable(
                        "ms.command.world_teleport.sender.message",
                        playerInfo.getGrayIDGreenName(),
                        text(playerInfo.getNickname()),
                        text(world.getName()),
                        text(x),
                        text(y),
                        text(z)
                )
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                for (var player : Bukkit.getOnlinePlayers()) {
                    PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
                    int id = playerInfo.getID(false, false);

                    if (id != -1) {
                        completions.add(String.valueOf(id));
                    }

                    completions.add(player.getName());
                }
            }
            case 2 -> {
                for (var world : Bukkit.getWorlds()) {
                    if (world.equals(MSEssentials.getWorldDark())) continue;
                    completions.add(world.getName());
                }
            }
            case 3, 4, 5 -> {
                Location playerLoc = null;

                if (sender instanceof Player player && args[1].equals(player.getWorld().getName())) {
                    playerLoc = player.getLocation();
                }

                if (playerLoc != null) {
                    double coordinate = switch (args.length) {
                        case 3 -> playerLoc.x();
                        case 4 -> playerLoc.y();
                        default -> playerLoc.z();
                    };
                    double rounded = Math.round(coordinate * 100) / 100.0;

                    completions.add(String.valueOf(rounded));
                }
            }
        }
        return completions;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
