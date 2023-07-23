package com.minersstudios.msblock.commands;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msessentials.player.PlayerInfo;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class GiveCommand {
    private static final TranslatableComponent PLAYER_NOT_FOUND = translatable("ms.error.player_not_found");
    private static final TranslatableComponent PLAYER_NOT_ONLINE = translatable("ms.error.player_not_online");
    private static final TranslatableComponent WRONG_BLOCK = translatable("ms.command.msblock.give.wrong_block");
    private static final TranslatableComponent WRONG_FORMAT = translatable("ms.error.wrong_format");
    private static final TranslatableComponent GIVE_SUCCESS = translatable("ms.command.msblock.give.success");

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull ... args
    ) {
        if (args.length < 3) return false;

        String playerArg = args[1];
        String blockArg = args[2];
        String amountArg = args.length == 4 ? args[3] : "1";
        PlayerInfo playerInfo = PlayerInfo.fromString(playerArg);

        if (playerInfo == null) {
            MSLogger.severe(sender, PLAYER_NOT_FOUND);
            return true;
        }

        Player player = playerInfo.getOnlinePlayer();

        if (player == null) {
            MSLogger.warning(sender, PLAYER_NOT_ONLINE);
            return true;
        }

        CustomBlockData customBlockData = MSPlugin.getGlobalCache().customBlockMap.getByPrimaryKey(blockArg);

        if (customBlockData == null) {
            MSLogger.severe(sender, WRONG_BLOCK);
            return true;
        }

        int amount;

        try {
            amount = Integer.parseInt(amountArg);
        } catch (NumberFormatException ignore) {
            MSLogger.severe(sender, WRONG_FORMAT);
            return true;
        }

        ItemStack itemStack = customBlockData.craftItemStack();
        itemStack.setAmount(amount);

        player.getInventory().addItem(itemStack);
        MSLogger.info(
                sender,
                GIVE_SUCCESS.args(
                        text(amount),
                        itemStack.displayName(),
                        playerInfo.getDefaultName()
                )
        );
        return true;
    }
}
