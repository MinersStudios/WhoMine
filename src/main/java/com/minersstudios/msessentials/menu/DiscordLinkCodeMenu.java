package com.minersstudios.msessentials.menu;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.inventory.SingleInventory;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class DiscordLinkCodeMenu {
    private static final Component TITLE = translatable("ms.menu.discord.title", ChatUtils.DEFAULT_STYLE);
    private static final List<TranslatableComponent> NUMBERS = ImmutableList.of(
            translatable("ms.menu.discord.numbers.0"),
            translatable("ms.menu.discord.numbers.1"),
            translatable("ms.menu.discord.numbers.2"),
            translatable("ms.menu.discord.numbers.3"),
            translatable("ms.menu.discord.numbers.4"),
            translatable("ms.menu.discord.numbers.5"),
            translatable("ms.menu.discord.numbers.6"),
            translatable("ms.menu.discord.numbers.7"),
            translatable("ms.menu.discord.numbers.8"),
            translatable("ms.menu.discord.numbers.9")
    );

    public static void open(@NotNull Player player) {
        SingleInventory.single(
                TITLE
                .append(generateNumbers(PlayerInfo.fromOnlinePlayer(player).generateCode()))
                .color(NamedTextColor.WHITE),
                4
        ).open(player);
    }

    private static @NotNull Component generateNumbers(short code) {
        return text("뀔").toBuilder()
                .append(
                        String.valueOf(code).chars()
                        .mapToObj(c -> NUMBERS.get(Character.getNumericValue((char) c)))
                        .toArray(Component[]::new)
                ).build();
    }
}
