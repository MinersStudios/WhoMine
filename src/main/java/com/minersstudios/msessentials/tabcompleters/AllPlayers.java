package com.minersstudios.msessentials.tabcompleters;

import com.minersstudios.msessentials.MSEssentials;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AllPlayers implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (var offlinePlayer : sender.getServer().getOfflinePlayers()) {
                String nickname = offlinePlayer.getName();

                if (nickname == null) continue;

                UUID uuid = offlinePlayer.getUniqueId();
                int id = MSEssentials.getCache().idMap.getID(uuid, false, false);

                if (id != -1) {
                    completions.add(String.valueOf(id));
                }

                if (offlinePlayer.hasPlayedBefore()) {
                    completions.add(nickname);
                }
            }
        }
        return completions;
    }
}
