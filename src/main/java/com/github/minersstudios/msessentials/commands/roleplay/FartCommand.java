package com.github.minersstudios.msessentials.commands.roleplay;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.BlockUtils;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;

import static com.github.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.ME;
import static com.github.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.TODO;
import static com.github.minersstudios.msessentials.utils.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "fart",
        usage = " ꀑ §cИспользуй: /<command> [речь]",
        description = "Пукни вкусно на публику"
)
public class FartCommand implements MSCommandExecutor {
    private final SecureRandom random = new SecureRandom();
    private static final CommandNode<?> COMMAND_NODE =
            literal("fart")
            .then(argument("речь", StringArgumentType.greedyString()))
            .build();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (!(sender instanceof Player player)) {
            ChatUtils.sendError(sender, Component.translatable("ms.error.only_player_command"));
            return true;
        }

        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (playerInfo.isMuted()) {
            ChatUtils.sendWarning(player, Component.translatable("ms.command.mute.already.receiver"));
            return true;
        }

        Location location = player.getLocation();
        boolean withPoop =
                this.random.nextInt(10) == 0
                && location.clone().subtract(0.0d, 0.5d, 0.0d).getBlock().getType().isSolid()
                && BlockUtils.REPLACE.contains(location.clone().getBlock().getType());

        for (var nearbyEntity : player.getWorld().getNearbyEntities(location.getBlock().getLocation().add(0.5d, 0.5d, 0.5d), 0.5d, 0.5d, 0.5d)) {
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
