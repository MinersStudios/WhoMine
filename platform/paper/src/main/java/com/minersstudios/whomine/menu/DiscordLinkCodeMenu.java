package com.minersstudios.whomine.menu;

import com.minersstudios.wholib.paper.inventory.CustomInventory;
import com.minersstudios.wholib.paper.inventory.holder.AbstractInventoryHolder;
import com.minersstudios.wholib.paper.inventory.holder.InventoryHolder;
import com.minersstudios.wholib.utility.ChatUtils;
import com.minersstudios.wholib.utility.Font;
import com.minersstudios.wholib.paper.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static com.minersstudios.wholib.locale.Translations.*;
import static net.kyori.adventure.text.Component.text;

@InventoryHolder
public final class DiscordLinkCodeMenu extends AbstractInventoryHolder {
    private static final Component TITLE = MENU_DISCORD_TITLE.asTranslatable().style(ChatUtils.DEFAULT_STYLE);
    private static final List<TranslatableComponent> NUMBERS = Arrays.asList(
            MENU_DISCORD_NUMBERS_0.asTranslatable(),
            MENU_DISCORD_NUMBERS_1.asTranslatable(),
            MENU_DISCORD_NUMBERS_2.asTranslatable(),
            MENU_DISCORD_NUMBERS_3.asTranslatable(),
            MENU_DISCORD_NUMBERS_4.asTranslatable(),
            MENU_DISCORD_NUMBERS_5.asTranslatable(),
            MENU_DISCORD_NUMBERS_6.asTranslatable(),
            MENU_DISCORD_NUMBERS_7.asTranslatable(),
            MENU_DISCORD_NUMBERS_8.asTranslatable(),
            MENU_DISCORD_NUMBERS_9.asTranslatable()
    );

    public DiscordLinkCodeMenu() {
        super("discord_link_code");
    }

    @Override
    protected @Nullable CustomInventory createCustomInventory() {
        return null; // creates on open
    }

    @Override
    public void open(final @NotNull Player player) {
        CustomInventory.single(
                TITLE
                .append(
                        generateNumbers(
                                PlayerInfo
                                .fromOnlinePlayer(this.getPlugin(), player)
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
