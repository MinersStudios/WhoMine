package com.minersstudios.msessentials;

import com.minersstudios.msessentials.anomalies.Anomaly;
import com.minersstudios.msessentials.anomalies.AnomalyAction;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.discord.DiscordMap;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.map.IDMap;
import com.minersstudios.msessentials.player.map.MuteMap;
import com.minersstudios.msessentials.player.map.PlayerInfoMap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for all custom data
 */
public final class Cache {
    public final PlayerInfoMap playerInfoMap = new PlayerInfoMap();
    public final MuteMap muteMap = new MuteMap();
    public final DiscordMap discordMap = new DiscordMap();
    public final IDMap idMap = new IDMap();
    public final Map<Player, ArmorStand> seats = new HashMap<>();
    public final Map<NamespacedKey, Anomaly> anomalies = new HashMap<>();
    public final Map<Player, Map<AnomalyAction, Long>> playerAnomalyActionMap = new ConcurrentHashMap<>();
    public final Map<UUID, Queue<String>> chatQueue = new HashMap<>();
    public final List<BukkitTask> bukkitTasks = new ArrayList<>();
    public final Map<Long, BotHandler> botHandlers = new HashMap<>();
    public PlayerInfo consolePlayerInfo;
}
