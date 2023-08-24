package com.minersstudios.msessentials.commands.player.roleplay;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.util.BlockUtils;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.MSDecorUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;

import static com.minersstudios.msessentials.util.MessageUtils.RolePlayActionType.ME;
import static com.minersstudios.msessentials.util.MessageUtils.RolePlayActionType.TODO;
import static com.minersstudios.msessentials.util.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "fart",
        usage = " ꀑ §cИспользуй: /<command> [речь]",
        description = "Пукни вкусно на публику",
        playerOnly = true
)
public class FartCommand implements MSCommandExecutor {
    private final SecureRandom random = new SecureRandom();
    private static final CommandNode<?> COMMAND_NODE =
            literal("fart")
            .then(argument("speech", StringArgumentType.greedyString()))
            .build();

    private static final TranslatableComponent MUTED = translatable("ms.command.mute.already.receiver");

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        final Player player = (Player) sender;
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (playerInfo.isMuted()) {
            MSLogger.warning(player, MUTED);
            return true;
        }

        final Location location = player.getLocation();
        boolean withPoop =
                this.random.nextInt(10) == 0
                && location.clone().subtract(0.0d, 0.5d, 0.0d).getBlock().getType().isSolid()
                && BlockUtils.REPLACE.contains(location.clone().getBlock().getType());

        for (final var nearbyEntity : player.getWorld().getNearbyEntities(location.getBlock().getLocation().add(0.5d, 0.5d, 0.5d), 0.5d, 0.5d, 0.5d)) {
            if (nearbyEntity.getType() != EntityType.DROPPED_ITEM && nearbyEntity.getType() != EntityType.PLAYER) {
                withPoop = false;
                break;
            }
        }

        player.getWorld().playSound(location.add(0, 0.4, 0), Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1, 1);
        player.getWorld().spawnParticle(Particle.REDSTONE, location, 15, 0.0D, 0.0D, 0.0D, 0.5D, new Particle.DustOptions(Color.fromBGR(33, 54, 75), 10));

        if (withPoop) {
            MSDecorUtils.placeCustomDecor(
                    location.getBlock(),
                    player,
                    "msdecor:poop",
                    BlockFace.UP,
                    null,
                    ChatUtils.createDefaultStyledText("Какашка " + ChatUtils.serializeLegacyComponent(playerInfo.getDefaultName()))
            );
        }

        if (args.length > 0) {
            sendRPEventMessage(player, text(ChatUtils.extractMessage(args, 0)), text(withPoop ? "пукнув с подливой" : "пукнув"), TODO);
            return true;
        }

        sendRPEventMessage(player, playerInfo.getPlayerFile().getPronouns().getFartMessage().append(text(withPoop ? " с подливой" : "")), ME);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
