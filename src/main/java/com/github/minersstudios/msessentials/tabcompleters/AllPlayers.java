package com.github.minersstudios.msessentials.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.minersstudios.msessentials.MSEssentials.getConfigCache;

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
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                String nickname = offlinePlayer.getName();

                if (nickname == null) continue;

                UUID uuid = offlinePlayer.getUniqueId();
                int id = getConfigCache().idMap.get(uuid, false, false);

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
