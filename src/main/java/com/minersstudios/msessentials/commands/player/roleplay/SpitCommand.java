package com.minersstudios.msessentials.commands.player.roleplay;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
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

import static com.minersstudios.msessentials.util.MessageUtils.RolePlayActionType.ME;
import static com.minersstudios.msessentials.util.MessageUtils.RolePlayActionType.TODO;
import static com.minersstudios.msessentials.util.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "spit",
        usage = " ꀑ §cИспользуй: /<command> [речь]",
        description = "Покажи свою дерзость и плюнь кому-то в лицо",
        playerOnly = true
)
public class SpitCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE =
            literal("spit")
            .then(argument("speech", StringArgumentType.greedyString()))
            .build();

    private static final TranslatableComponent MUTED = translatable("ms.command.mute.already.receiver");

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        Player player = (Player) sender;
        World world = player.getWorld();
        Location location = player.getLocation();
        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

        if (playerInfo.isMuted()) {
            MSLogger.warning(player, MUTED);
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
