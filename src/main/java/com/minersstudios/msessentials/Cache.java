package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.PluginCache;
import com.minersstudios.msessentials.anomalies.Anomaly;
import com.minersstudios.msessentials.anomalies.AnomalyAction;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.discord.DiscordMap;
import com.minersstudios.msessentials.discord.command.SlashCommandExecutor;
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
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for all custom data
 */
public final class Cache extends PluginCache {
    private PlayerInfoMap playerInfoMap;
    private MuteMap muteMap;
    private DiscordMap discordMap;
    private IDMap idMap;
    private Map<Player, ArmorStand> seats;
    private Map<NamespacedKey, Anomaly> anomalies;
    private Map<Player, Map<AnomalyAction, Long>> playerAnomalyActionMap;
    private Map<UUID, Queue<String>> chatQueue;
    private List<BukkitTask> bukkitTasks;
    private Map<Long, BotHandler> botHandlers;
    PlayerInfo consolePlayerInfo;
    JDA jda;
    Guild mainGuild;
    TextChannel discordGlobalChannel;
    TextChannel discordLocalChannel;
    Role memberRole;
    List<SlashCommandExecutor> slashCommands;

    @Override
    protected void onLoad() {
        this.playerInfoMap = new PlayerInfoMap();
        this.muteMap = new MuteMap();
        this.discordMap = new DiscordMap();
        this.idMap = new IDMap();
        this.seats = new ConcurrentHashMap<>();
        this.anomalies = new ConcurrentHashMap<>();
        this.playerAnomalyActionMap = new ConcurrentHashMap<>();
        this.chatQueue = new HashMap<>();
        this.bukkitTasks = new ArrayList<>();
        this.botHandlers = new HashMap<>();
    }

    @Override
    protected void onUnload() {
        this.playerInfoMap = null;
        this.muteMap = null;
        this.discordMap = null;
        this.idMap = null;
        this.seats = null;
        this.anomalies = null;
        this.playerAnomalyActionMap = null;
        this.chatQueue = null;
        this.bukkitTasks = null;
        this.botHandlers = null;
    }

    public @UnknownNullability PlayerInfoMap getPlayerInfoMap() {
        return this.playerInfoMap;
    }

    public @UnknownNullability MuteMap getMuteMap() {
        return this.muteMap;
    }

    public @UnknownNullability DiscordMap getDiscordMap() {
        return this.discordMap;
    }

    public @UnknownNullability IDMap getIdMap() {
        return this.idMap;
    }

    public @UnknownNullability Map<Player, ArmorStand> getSeats() {
        return this.seats;
    }

    public @UnknownNullability Map<NamespacedKey, Anomaly> getAnomalies() {
        return this.anomalies;
    }

    public @UnknownNullability Map<Player, Map<AnomalyAction, Long>> getPlayerAnomalyActionMap() {
        return this.playerAnomalyActionMap;
    }

    public @UnknownNullability Map<UUID, Queue<String>> getChatQueue() {
        return this.chatQueue;
    }

    public @UnknownNullability List<BukkitTask> getBukkitTasks() {
        return this.bukkitTasks;
    }

    public @UnknownNullability Map<Long, BotHandler> getBotHandlers() {
        return this.botHandlers;
    }

    public @UnknownNullability PlayerInfo getConsolePlayerInfo() {
        return this.consolePlayerInfo;
    }

    public @UnknownNullability JDA getJda() {
        return this.jda;
    }

    public @UnknownNullability Guild getMainGuild() {
        return this.mainGuild;
    }

    public @UnknownNullability TextChannel getDiscordGlobalChannel() {
        return this.discordGlobalChannel;
    }

    public @UnknownNullability TextChannel getDiscordLocalChannel() {
        return this.discordLocalChannel;
    }

    public @UnknownNullability Role getMemberRole() {
        return this.memberRole;
    }

    public @UnknownNullability @Unmodifiable List<SlashCommandExecutor> getSlashCommands() {
        return Collections.unmodifiableList(this.slashCommands);
    }
}
