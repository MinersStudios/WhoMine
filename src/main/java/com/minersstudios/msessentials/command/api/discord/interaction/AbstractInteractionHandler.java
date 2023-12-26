package com.minersstudios.msessentials.command.api.discord.interaction;

import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractInteractionHandler<T extends Interaction> {
    private final MSEssentials plugin;
    private final T interaction;
    protected PlayerInfo playerInfo;

    public AbstractInteractionHandler(
            final @NotNull MSEssentials plugin,
            final @NotNull T interaction
    ) {
        this.plugin = plugin;
        this.interaction = interaction;
    }

    public final @NotNull MSEssentials getPlugin() {
        return this.plugin;
    }

    public final @NotNull T getInteraction() {
        return this.interaction;
    }

    public final @Nullable PlayerInfo getPlayerInfo() {
        if (this.playerInfo != null) {
            return this.playerInfo;
        }

        final User user = this.interaction.getUser();

        if (this.plugin.getCache().getDiscordManager().isVerified(user)) {
            this.playerInfo = PlayerInfo.fromDiscord(this.plugin, user.getIdLong());
        }

        return this.playerInfo;
    }

    abstract @NotNull String getCommandName();
}
