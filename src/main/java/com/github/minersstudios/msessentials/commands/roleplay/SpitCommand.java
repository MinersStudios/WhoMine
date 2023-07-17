package com.github.minersstudios.msessentials.commands.roleplay;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.ME;
import static com.github.minersstudios.msessentials.utils.MessageUtils.RolePlayActionType.TODO;
import static com.github.minersstudios.msessentials.utils.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@MSCommand(
        command = "spit",
        usage = " ꀑ §cИспользуй: /<command> [речь]",
        description = "Покажи свою дерзость и плюнь кому-то в лицо"
)
public class SpitCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("spit")
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
            MSLogger.severe(sender, Component.translatable("ms.error.only_player_command"));
            return true;
        }

        World world = player.getWorld();
        Location location = player.getLocation();
        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (playerInfo.isMuted()) {
            MSLogger.warning(player, Component.translatable("ms.command.mute.already.receiver"));
            return true;
        }

        world.spawnEntity(
                location.toVector().add(location.getDirection().multiply(0.8d)).toLocation(world).add(0.0d, 1.0d, 0.0d),
                EntityType.LLAMA_SPIT
        ).setVelocity(player.getEyeLocation().getDirection().multiply(1));
        world.playSound(location, Sound.ENTITY_LLAMA_SPIT, SoundCategory.PLAYERS, 1.0f, 1.0f);

        if (args.length > 0) {
            sendRPEventMessage(player, text(ChatUtils.extractMessage(args, 0)), text("плюнув"), TODO);
            return true;
        }

        sendRPEventMessage(player, playerInfo.getPlayerFile().getPronouns().getSpitMessage(), ME);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
