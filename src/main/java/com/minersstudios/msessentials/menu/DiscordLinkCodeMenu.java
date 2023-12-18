package com.minersstudios.msessentials.menu;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.language.LanguageRegistry;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class DiscordLinkCodeMenu {
    private static final Component TITLE = LanguageRegistry.Components.MENU_DISCORD_TITLE.style(ChatUtils.DEFAULT_STYLE);
    private static final List<TranslatableComponent> NUMBERS = ImmutableList.of(
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_0,
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_1,
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_2,
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_3,
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_4,
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_5,
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_6,
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_7,
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_8,
            LanguageRegistry.Components.MENU_DISCORD_NUMBERS_9
    );

    public static void open(
            final @NotNull MSEssentials plugin,
            final @NotNull Player player
    ) {
        CustomInventory.single(
                TITLE
                .append(
                        generateNumbers(
                                PlayerInfo
                                .fromOnlinePlayer(plugin, player)
                                .generateCode()
                        )
                )
                .color(NamedTextColor.WHITE),
                4
        ).open(player);
    }

    private static @NotNull Component generateNumbers(final short code) {
        return text(Font.Chars.PIXEL_SPLIT_M111).toBuilder()
                .append(
                        String.valueOf(code).chars()
                        .mapToObj(c -> NUMBERS.get(Character.getNumericValue((char) c)))
                        .toArray(Component[]::new)
                ).build();
    }
}
