package com.github.minersstudios.msitem.commands;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msitem.items.CustomItem;
import com.github.minersstudios.msitem.items.RenameableItem;
import com.github.minersstudios.msitem.items.Typed;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class GiveCommand {

    public static boolean runCommand(@NotNull CommandSender sender, String @NotNull ... args) {
        if (args.length < 3) return false;
        if (args[1].length() > 2) {
            int amount = 1;
            Player player = Bukkit.getPlayer(args[1]);

            if (player == null) {
                ChatUtils.sendError(sender, translatable("ms.error.player_not_found"));
                return true;
            }

            ItemStack itemStack;
            RenameableItem renameableItem = MSCore.getCache().renameableItemMap.getByPrimaryKey(args[2]);
            CustomItem customItem = MSCore.getCache().customItemMap.getByPrimaryKey(args[2]);

            if (customItem == null) {
                if (renameableItem == null) {
                    ChatUtils.sendError(sender, translatable("ms.command.msitem.give.wrong_item"));
                    return true;
                } else {
                    itemStack = renameableItem.getResultItemStack();
                }
            } else {
                if (
                        customItem instanceof Typed typed
                        && args.length == 4
                        && !args[3].matches("\\d+")
                ) {
                    for (var type : typed.getTypes()) {
                        if (type.getNamespacedKey().getKey().equals(args[3])) {
                            customItem = typed.createCustomItem(type);
                        }
                    }
                }
                itemStack = customItem.getItemStack();
            }

            switch (args.length) {
                case 4, 5 -> {
                    try {
                        amount = Integer.parseInt(args[args.length - 1]);
                    } catch (NumberFormatException ignore) {
                        ChatUtils.sendError(sender, translatable("ms.error.wrong_format"));
                        return true;
                    }
                }
            }

            itemStack.setAmount(amount);
            player.getInventory().addItem(itemStack);
            ChatUtils.sendInfo(
                    sender,
                    translatable(
                            "ms.command.msitem.give.success",
                            text(amount),
                            itemStack.displayName(),
                            text(player.getName())
                    )
            );
            return true;
        }

        ChatUtils.sendWarning(sender, translatable("ms.error.name_length"));
        return true;
    }
}
