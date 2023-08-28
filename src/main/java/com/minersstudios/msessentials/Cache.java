package com.minersstudios.msessentials;

import com.minersstudios.msessentials.anomalies.Anomaly;
import com.minersstudios.msessentials.anomalies.AnomalyAction;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.discord.DiscordMap;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.IDMap;
import com.minersstudios.msessentials.player.collection.MuteMap;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
    public final Map<Player, ArmorStand> seats = new ConcurrentHashMap<>();
    public final Map<NamespacedKey, Anomaly> anomalies = new ConcurrentHashMap<>();
    public final Map<Player, Map<AnomalyAction, Long>> playerAnomalyActionMap = new ConcurrentHashMap<>();
    public final Map<UUID, Queue<String>> chatQueue = new HashMap<>();
    public final List<BukkitTask> bukkitTasks = new ArrayList<>();
    public final Map<Long, BotHandler> botHandlers = new HashMap<>();
    public PlayerInfo consolePlayerInfo;
    public JDA jda;
    public Guild mainGuild;
    public TextChannel discordGlobalChannel;
    public TextChannel discordLocalChannel;
    public Role memberRole;
}
