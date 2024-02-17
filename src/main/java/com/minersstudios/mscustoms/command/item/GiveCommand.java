package com.minersstudios.mscustoms.command.item;

import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.mscustoms.custom.item.CustomItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class GiveCommand {

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final String @NotNull ... args
    ) {
        if (args.length < 3) {
            return false;
        }

        final String playerArg = args[1];
        final String itemArg = args[2];
        final String amountArg = args.length == 4 ? args[3] : "1";
        final PlayerInfo playerInfo = PlayerInfo.fromString(MSEssentials.singleton(), playerArg);

        if (playerInfo == null) {
            MSLogger.severe(
                    sender,
                    Translations.ERROR_PLAYER_NOT_FOUND.asTranslatable()
            );
            return true;
        }

        final Player player = playerInfo.getOnlinePlayer();

        if (player == null) {
            MSLogger.warning(
                    sender,
                    Translations.ERROR_PLAYER_NOT_ONLINE.asTranslatable()
            );
            return true;
        }

        CustomItem.fromKey(itemArg).ifPresentOrElse(
                customItem -> {
                    final int amount;

                    try {
                        amount = Integer.parseInt(amountArg);
                    } catch (final NumberFormatException ignored) {
                        MSLogger.severe(
                                sender,
                                Translations.ERROR_WRONG_FORMAT.asTranslatable()
                        );
                        return;
                    }

                    final ItemStack itemStack = customItem.getItem();

                    itemStack.setAmount(amount);
                    player.getInventory().addItem(itemStack);
                    MSLogger.fine(
                            sender,
                            Translations.COMMAND_MSITEM_GIVE_SUCCESS.asTranslatable()
                            .arguments(
                                    text(amount),
                                    itemStack.displayName(),
                                    playerInfo.getDefaultName()
                            )
                    );
                },
                () -> MSLogger.severe(
                        sender,
                        Translations.COMMAND_MSITEM_GIVE_WRONG_ITEM.asTranslatable()
                )
        );

        return true;
    }
}
