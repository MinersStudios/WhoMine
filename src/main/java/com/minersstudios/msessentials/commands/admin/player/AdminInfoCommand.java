package com.minersstudios.msessentials.commands.admin.player;

import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.PlayerName;
import com.minersstudios.msessentials.player.PlayerSettings;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AdminInfoCommand {

    public static boolean runCommand(
            final @NotNull CommandSender sender,
            final @NotNull PlayerInfo playerInfo
    ) {
        final PlayerFile playerFile = playerInfo.getPlayerFile();
        final PlayerName playerName = playerFile.getPlayerName();
        final PlayerSettings playerSettings = playerFile.getPlayerSettings();
        Location lastLeaveLocation = playerFile.getLastLeaveLocation();
        Location lastDeathLocation = playerFile.getLastDeathLocation();

        if (lastLeaveLocation == null) {
            lastLeaveLocation = MSEssentials.getConfiguration().spawnLocation;
        }

        if (lastDeathLocation == null) {
            lastDeathLocation = MSEssentials.getConfiguration().spawnLocation;
        }

        MSLogger.info(sender,
                "UUID : " + playerInfo.getOfflinePlayer().getUniqueId()
                + "\n ID : " + playerInfo.getID(false, false)
                + "\n Nickname : " + playerName.getNickname()
                + "\n Firstname : " + playerName.getFirstName()
                + "\n Lastname : " + playerName.getLastName()
                + "\n Patronymic : " + playerName.getPatronymic()
                + "\n RP-type : " + playerSettings.getResourcePackType()
                + "\n Muted : " + playerInfo.isMuted()
                + "\n Banned : " + playerInfo.isBanned()
                + "\n First join : " + playerFile.getFirstJoin()
                + "\n Mute reason : " + playerInfo.getMuteReason()
                + "\n Muted to : " + playerInfo.getMutedTo(sender)
                + "\n Ban reason : " + playerInfo.getBanReason()
                + "\n Banned to : " + playerInfo.getBannedTo(sender)
                + "\n Last death world : " + lastDeathLocation.getWorld().getName()
                + "\n Last death X : " + lastDeathLocation.getX()
                + "\n Last death Y : " + lastDeathLocation.getY()
                + "\n Last death Z : " + lastDeathLocation.getZ()
                + "\n Last death Yaw : " + lastDeathLocation.getYaw()
                + "\n Last death Pitch : " + lastDeathLocation.getPitch()
                + "\n Last leave world : " + lastLeaveLocation.getWorld().getName()
                + "\n Last leave X : " + lastLeaveLocation.getX()
                + "\n Last leave Y : " + lastLeaveLocation.getY()
                + "\n Last leave Z : " + lastLeaveLocation.getZ()
                + "\n Last leave Yaw : " + lastLeaveLocation.getYaw()
                + "\n Last leave Pitch : " + lastLeaveLocation.getPitch()
        );
        return true;
    }
}
