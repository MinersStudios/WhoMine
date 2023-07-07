package com.github.minersstudios.msessentials.commands.other.discord;

import com.github.minersstudios.mscore.inventory.SingleInventory;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

public class LinkCommand {
    private static final Component TITLE = translatable("ms.menu.discord.title");
    private static final Component NUM_0 = translatable("ms.menu.discord.numbers.0");
    private static final Component NUM_1 = translatable("ms.menu.discord.numbers.1");
    private static final Component NUM_2 = translatable("ms.menu.discord.numbers.2");
    private static final Component NUM_3 = translatable("ms.menu.discord.numbers.3");
    private static final Component NUM_4 = translatable("ms.menu.discord.numbers.4");
    private static final Component NUM_5 = translatable("ms.menu.discord.numbers.5");
    private static final Component NUM_6 = translatable("ms.menu.discord.numbers.6");
    private static final Component NUM_7 = translatable("ms.menu.discord.numbers.7");
    private static final Component NUM_8 = translatable("ms.menu.discord.numbers.8");
    private static final Component NUM_9 = translatable("ms.menu.discord.numbers.9");

    public static void runCommand(
            @NotNull Player sender,
            @NotNull PlayerInfo playerInfo
    ) {
        short code = playerInfo.generateCode();
        sender.openInventory(
                SingleInventory.single(
                        TITLE
                        .append(generateNumbers(code))
                        .color(NamedTextColor.WHITE),
                        4
                )
        );
    }

    private static @NotNull Component generateNumbers(short code) {
        Component component = Component.text("뀔");

        for (char c : String.valueOf(code).toCharArray()) {
            switch (c) {
                case '0' -> component = component.append(NUM_0);
                case '1' -> component = component.append(NUM_1);
                case '2' -> component = component.append(NUM_2);
                case '3' -> component = component.append(NUM_3);
                case '4' -> component = component.append(NUM_4);
                case '5' -> component = component.append(NUM_5);
                case '6' -> component = component.append(NUM_6);
                case '7' -> component = component.append(NUM_7);
                case '8' -> component = component.append(NUM_8);
                case '9' -> component = component.append(NUM_9);
            }
        }

        return component;
    }
}
