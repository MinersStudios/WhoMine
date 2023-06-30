package com.github.minersstudios.msblock.commands;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class GiveCommand {

    public static boolean runCommand(
            @NotNull CommandSender sender,
            String @NotNull ... args
    ) {
        if (args.length < 3) return false;
        if (args[1].length() > 2) {
            int amount = 1;
            Player player = Bukkit.getPlayer(args[1]);
            CustomBlockData customBlockData = MSCore.getCache().customBlockMap.getByPrimaryKey(args[2]);

            if (player == null) {
                ChatUtils.sendError(sender, translatable("ms.error.player_not_found"));
                return true;
            }

            if (customBlockData == null) {
                ChatUtils.sendError(sender, translatable("ms.command.msblock.give.wrong_block"));
                return true;
            }

            if (args.length == 4) {
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException ignore) {
                    ChatUtils.sendError(sender, translatable("ms.error.wrong_format"));
                    return true;
                }
            }

            ItemStack itemStack = customBlockData.craftItemStack();
            Component itemName = itemStack.displayName();
            itemStack.setAmount(amount);

            player.getInventory().addItem(itemStack);
            ChatUtils.sendInfo(
                    sender,
                    translatable(
                            "ms.command.msblock.give.success",
                            text(amount),
                            itemName,
                            text(player.getName())
                    )
            );
            return true;
        }

        ChatUtils.sendWarning(sender, translatable("ms.error.name_length"));
        return true;
    }
}
