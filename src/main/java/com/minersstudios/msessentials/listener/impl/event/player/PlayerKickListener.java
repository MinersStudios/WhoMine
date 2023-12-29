package com.minersstudios.msessentials.listener.impl.event.player;

import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import com.minersstudios.msessentials.MSEssentials;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;

@EventListener
public class PlayerKickListener extends AbstractEventListener<MSEssentials> {
    private static final TranslatableComponent SERVER_RESTARTING =
            FORMAT_LEAVE_MESSAGE
            .arguments(
                    ON_DISABLE_MESSAGE_TITLE.color(NamedTextColor.RED).decorate(TextDecoration.BOLD),
                    ON_DISABLE_MESSAGE_SUBTITLE.color(NamedTextColor.GRAY)
            )
            .color(NamedTextColor.DARK_GRAY);

    @EventHandler
    public void onPlayerKick(final @NotNull PlayerKickEvent event) {
        if (event.getCause() == PlayerKickEvent.Cause.RESTART_COMMAND) {
            event.reason(SERVER_RESTARTING);
        }
    }
}
