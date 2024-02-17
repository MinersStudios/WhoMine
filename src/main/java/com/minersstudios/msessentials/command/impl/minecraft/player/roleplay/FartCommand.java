package com.minersstudios.msessentials.command.impl.minecraft.player.roleplay;

import com.minersstudios.mscore.command.api.AbstractCommandExecutor;
import com.minersstudios.mscore.command.api.MSCommand;
import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.BlockUtils;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.mscustoms.custom.decor.CustomDecorType;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;

import static com.minersstudios.msessentials.utility.MessageUtils.RolePlayActionType.ME;
import static com.minersstudios.msessentials.utility.MessageUtils.RolePlayActionType.TODO;
import static com.minersstudios.msessentials.utility.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "fart",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [речь]",
        description = "Пукни вкусно на публику",
        playerOnly = true
)
public final class FartCommand extends AbstractCommandExecutor<MSEssentials> {
    private final SecureRandom random = new SecureRandom();
    private static final CommandNode<?> COMMAND_NODE =
            literal("fart")
            .then(argument("speech", StringArgumentType.greedyString()))
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        final Player player = (Player) sender;
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(this.getPlugin(), player);

        if (playerInfo.isMuted()) {
            MSLogger.warning(
                    player,
                    Translations.COMMAND_MUTE_ALREADY_RECEIVER.asTranslatable()
            );

            return true;
        }

        final Location location = player.getLocation();
        final World world = player.getWorld();
        boolean withPoop =
                this.random.nextInt(10) == 0
                && location.clone().subtract(0.0d, 0.5d, 0.0d).getBlock().getType().isSolid()
                && BlockUtils.isReplaceable(location.clone().getBlock().getType());

        for (final var nearbyEntity : world.getNearbyEntities(location.getBlock().getLocation().add(0.5d, 0.5d, 0.5d), 0.5d, 0.5d, 0.5d)) {
            if (nearbyEntity.getType() != EntityType.DROPPED_ITEM && nearbyEntity.getType() != EntityType.PLAYER) {
                withPoop = false;
                break;
            }
        }

        world.playSound(location.add(0, 0.4, 0), Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1, 1);
        world.spawnParticle(Particle.REDSTONE, location, 15, 0.0D, 0.0D, 0.0D, 0.5D, new Particle.DustOptions(Color.fromBGR(33, 54, 75), 10));

        if (withPoop) {
            CustomDecorType.POOP.getCustomDecorData()
            .place(
                    MSPosition.of(location.toBlockLocation()),
                    player,
                    BlockFace.UP,
                    null,
                    ChatUtils.createDefaultStyledText("Какашка " + ChatUtils.serializeLegacyComponent(playerInfo.getDefaultName()))
            );
        }

        if (args.length > 0) {
            sendRPEventMessage(
                    player,
                    text(ChatUtils.extractMessage(args, 0)),
                    text(withPoop ? "пукнув с подливой" : "пукнув"),
                    TODO
            );
            return true;
        }

        sendRPEventMessage(
                player,
                playerInfo.getPlayerFile().getPronouns()
                        .getFartMessage()
                        .append(text(withPoop ? " с подливой" : "")),
                ME
        );

        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
