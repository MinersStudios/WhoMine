package com.minersstudios.msessentials.command.impl.minecraft.player.roleplay;

import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.msessentials.utility.MessageUtils.RolePlayActionType.ME;
import static com.minersstudios.msessentials.utility.MessageUtils.RolePlayActionType.TODO;
import static com.minersstudios.msessentials.utility.MessageUtils.sendRPEventMessage;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.kyori.adventure.text.Component.text;

@Command(
        command = "spit",
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command> [речь]",
        description = "Покажи свою дерзость и плюнь кому-то в лицо",
        playerOnly = true
)
public final class SpitCommand extends CommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE =
            literal("spit")
            .then(argument("speech", StringArgumentType.greedyString()))
            .build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        final Player player = (Player) sender;
        final World world = player.getWorld();
        final Location location = player.getLocation();
        final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(this.getPlugin(), player);

        if (playerInfo.isMuted()) {
            MSLogger.warning(
                    player,
                    LanguageRegistry.Components.COMMAND_MUTE_ALREADY_RECEIVER
            );
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
