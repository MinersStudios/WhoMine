package com.github.minersstudios.msessentials.tabcompleters;

import com.github.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AllLocalPlayers implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (var player : sender.getServer().getOnlinePlayers()) {
                PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

                if (playerInfo.isOnline()) {
                    int id = playerInfo.getID(false, false);

                    if (id != -1) {
                        completions.add(String.valueOf(id));
                    }

                    completions.add(player.getName());
                }
            }
        }
        return completions;
    }
}
