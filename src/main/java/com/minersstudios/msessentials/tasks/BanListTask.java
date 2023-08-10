package com.minersstudios.msessentials.tasks;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.minersstudios.msessentials.player.PlayerInfo;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ban.ProfileBanList;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BanListTask implements Runnable {
    private final Set<BanEntry<PlayerProfile>> ignoreBanSet = new HashSet<>();

    @Override
    public void run() {
        ProfileBanList banList = Bukkit.getServer().getBanList(BanList.Type.PROFILE);
        Set<BanEntry<PlayerProfile>> entries = banList.getEntries();
        Instant currentInstant = Instant.now();

        for (var entry : entries) {
            Date expiration = entry.getExpiration();

            if (
                    !this.ignoreBanSet.contains(entry)
                    && expiration != null
                    && expiration.toInstant().isBefore(currentInstant)
            ) {
                PlayerProfile profile = entry.getBanTarget();
                UUID uuid = profile.getId();
                String name = profile.getName();

                if (uuid == null || name == null) {
                    this.ignoreBanSet.add(entry);
                } else {
                    PlayerInfo.fromProfile(uuid, name).pardon(null);
                }
            }
        }
    }
}
