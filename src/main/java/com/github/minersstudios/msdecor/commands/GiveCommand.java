package com.github.minersstudios.msdecor.commands;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.logger.MSLogger;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.customdecor.Typed;
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
            CustomDecorData customDecorData = MSCore.getCache().customDecorMap.getByPrimaryKey(args[2]);

            if (player == null) {
                MSLogger.severe(sender, translatable("ms.error.player_not_found"));
                return true;
            }

            if (customDecorData == null) {
                MSLogger.severe(sender, translatable("ms.command.msdecor.give.wrong_decor"));
                return true;
            }

            switch (args.length) {
                case 4, 5 -> {
                    try {
                        amount = Integer.parseInt(args[args.length - 1]);
                    } catch (NumberFormatException ignore) {
                        MSLogger.severe(sender, translatable("ms.error.wrong_format"));
                        return true;
                    }
                }
            }

            if (
                    customDecorData instanceof Typed typed
                    && args.length == 4
                    && !args[3].matches("\\d+")
            ) {
                for (var type : typed.getTypes()) {
                    if (args[3].equals(type.getNamespacedKey().getKey())) {
                        customDecorData = typed.createCustomDecorData(type);
                    }
                }
            }

            ItemStack itemStack = customDecorData.getItemStack();
            Component itemName = itemStack.displayName();
            itemStack.setAmount(amount);

            player.getInventory().addItem(itemStack);
            MSLogger.info(
                    sender,
                    translatable(
                            "ms.command.msdecor.give.success",
                            text(amount),
                            itemName,
                            text(player.getName())
                    )
            );
            return true;
        }

        MSLogger.warning(sender, translatable("ms.error.name_length"));
        return true;
    }
}
