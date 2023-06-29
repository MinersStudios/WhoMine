package com.github.minersstudios.msutils.tabcompleters;

import com.github.minersstudios.msutils.MSUtils;
import com.github.minersstudios.msutils.player.PlayerInfo;
import com.github.minersstudios.msutils.player.PlayerInfoMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
            PlayerInfoMap playerInfoMap = MSUtils.getConfigCache().playerInfoMap;

            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);

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
