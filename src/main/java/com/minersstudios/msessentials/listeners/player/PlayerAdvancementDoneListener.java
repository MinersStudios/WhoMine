package com.minersstudios.msessentials.listeners.player;

import com.minersstudios.mscore.listener.event.MSListener;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.kyori.adventure.text.Component.text;

@MSListener
public class PlayerAdvancementDoneListener extends AbstractMSListener {

    @EventHandler
    public void onPlayerAdvancementDone(@NotNull PlayerAdvancementDoneEvent event) {
        AdvancementDisplay advancementDisplay = event.getAdvancement().getDisplay();

        if (advancementDisplay == null || event.message() == null) return;

        AdvancementDisplay.Frame frame = advancementDisplay.frame();
        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(event.getPlayer());
        Component title = advancementDisplay.title();
        Component description = advancementDisplay.description();

        event.message(
                Component.space()
                .append(Component.translatable(
                "chat.type.advancement." + frame.name().toLowerCase(Locale.ROOT),
                playerInfo.getDefaultName(),
                text("[").append(title).append(text("]")).color(frame.color())
                .hoverEvent(HoverEvent.showText(title.append(Component.newline().append(description)).color(frame.color()))))
                .color(NamedTextColor.GRAY))
        );
    }
}
