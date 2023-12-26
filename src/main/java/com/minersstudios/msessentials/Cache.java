package com.minersstudios.msessentials;

import com.minersstudios.mscore.plugin.cache.PluginCache;
import com.minersstudios.msessentials.anomaly.Anomaly;
import com.minersstudios.msessentials.anomaly.AnomalyAction;
import com.minersstudios.msessentials.chat.ChatBuffer;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.discord.DiscordManager;
import com.minersstudios.msessentials.discord.DiscordMap;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.collection.IDMap;
import com.minersstudios.msessentials.player.collection.MuteMap;
import com.minersstudios.msessentials.player.collection.PlayerInfoMap;
import com.minersstudios.msessentials.world.WorldDark;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for all custom data
 */
public final class Cache extends PluginCache<MSEssentials> {
    private PlayerInfoMap playerInfoMap;
    private MuteMap muteMap;
    private DiscordMap discordMap;
    private IDMap idMap;
    private Map<Player, ArmorStand> seats;
    private Map<NamespacedKey, Anomaly> anomalies;
    private Map<Player, Map<AnomalyAction, Long>> playerAnomalyActionMap;
    private ChatBuffer chatBuffer;
    private List<BukkitTask> bukkitTasks;
    private Map<Long, BotHandler> botHandlers;
    private DiscordManager discordManager;
    PlayerInfo consolePlayerInfo;
    WorldDark worldDark;

    /**
     * Cache constructor
     *
     * @param plugin The plugin that owns this cache
     */
    public Cache(final @NotNull MSEssentials plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        final MSEssentials plugin = this.getPlugin();

        this.playerInfoMap = new PlayerInfoMap(plugin);
        this.muteMap = new MuteMap(plugin);
        this.discordMap = new DiscordMap(plugin);
        this.idMap = new IDMap(plugin);
        this.seats = new ConcurrentHashMap<>();
        this.anomalies = new ConcurrentHashMap<>();
        this.playerAnomalyActionMap = new ConcurrentHashMap<>();
        this.chatBuffer = new ChatBuffer(plugin);
        this.bukkitTasks = new ArrayList<>();
        this.botHandlers = new HashMap<>();
        this.discordManager = new DiscordManager(plugin);
    }

    @Override
    protected void onUnload() {
        this.discordManager.unload();

        for (final var task : this.bukkitTasks) {
            task.cancel();
        }

        this.playerInfoMap = null;
        this.muteMap = null;
        this.discordMap = null;
        this.idMap = null;
        this.seats = null;
        this.anomalies = null;
        this.playerAnomalyActionMap = null;
        this.chatBuffer = null;
        this.bukkitTasks = null;
        this.botHandlers = null;
        this.discordManager = null;
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

    public @UnknownNullability ChatBuffer getChatBuffer() {
        return this.chatBuffer;
    }

    public @UnknownNullability List<BukkitTask> getBukkitTasks() {
        return this.bukkitTasks;
    }

    public @UnknownNullability Map<Long, BotHandler> getBotHandlers() {
        return this.botHandlers;
    }

    public @UnknownNullability DiscordManager getDiscordManager() {
        return this.discordManager;
    }

    public @UnknownNullability PlayerInfo getConsolePlayerInfo() {
        return this.consolePlayerInfo;
    }

    public @UnknownNullability WorldDark getWorldDark() {
        return this.worldDark;
    }
}
