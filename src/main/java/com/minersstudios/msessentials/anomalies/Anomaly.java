package com.minersstudios.msessentials.anomalies;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.common.collect.ImmutableList;
import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomalies.actions.AddPotionAction;
import com.minersstudios.msessentials.anomalies.actions.SpawnParticlesAction;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * Anomaly class with all anomaly data and associated namespaced key.
 * All anomalies are cached in {@link Cache#anomalies}.
 * <br>
 * Can have :
 * <ul>
 *     <li>{@link AnomalyBoundingBox} - radii and location of anomaly</li>
 *     <li>{@link AnomalyIgnorableItems} - ignorable items</li>
 *     <li>{@link AnomalyAction} - anomaly actions, which will be executed when player is in anomaly</li>
 *     <li>{@link OfflinePlayer} - ignorable players, which will be ignored by anomaly actions</li>
 * </ul>
 *
 * @see #fromConfig(File)
 */
public class Anomaly {
    private final @NotNull NamespacedKey namespacedKey;
    private final @NotNull AnomalyBoundingBox anomalyBoundingBox;
    private final @Nullable AnomalyIgnorableItems anomalyIgnorableItems;
    private final @NotNull Map<Double, List<AnomalyAction>> anomalyActionMap;
    private final @NotNull List<OfflinePlayer> ignorablePlayers;

    /**
     * @param namespacedKey         Namespaced key associated with anomaly
     * @param anomalyBoundingBox    Bounding box of anomaly
     * @param anomalyIgnorableItems Ignorable items of anomaly
     * @param anomalyActionMap      Action map of anomaly
     * @param ignorablePlayers      Ignorable players of anomaly
     */
    public Anomaly(
            final @NotNull NamespacedKey namespacedKey,
            final @NotNull AnomalyBoundingBox anomalyBoundingBox,
            final @Nullable AnomalyIgnorableItems anomalyIgnorableItems,
            final @NotNull Map<Double, List<AnomalyAction>> anomalyActionMap,
            final @NotNull List<OfflinePlayer> ignorablePlayers
    ) {
        this.namespacedKey = namespacedKey;
        this.anomalyBoundingBox = anomalyBoundingBox;
        this.anomalyIgnorableItems = anomalyIgnorableItems;
        this.anomalyActionMap = anomalyActionMap;
        this.ignorablePlayers = ignorablePlayers;
    }

    /**
     * Loads anomaly from config with specified settings.
     * Example can be found in "plugin_folder/anomalies/example.yml"
     *
     * @param file File of anomaly yaml config
     * @return Loaded anomaly from config
     * @throws IllegalArgumentException If anomaly config is invalid
     */
    @Contract("_ -> new")
    public static @NotNull Anomaly fromConfig(final @NotNull File file) throws IllegalArgumentException {
        final String fileName = file.getName();
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        final World world = Bukkit.getWorld(
                Objects.requireNonNull(config.getString("bounding-box.location.world-name"), "world in " + fileName + " is null")
        );
        final AnomalyBoundingBox anomalyBoundingBox = new AnomalyBoundingBox(
                Objects.requireNonNull(world, "Can't find world, anomaly : " + fileName),
                new BoundingBox(
                        config.getDouble("bounding-box.location.first-corner.x"),
                        config.getDouble("bounding-box.location.first-corner.y"),
                        config.getDouble("bounding-box.location.first-corner.z"),
                        config.getDouble("bounding-box.location.second-corner.x"),
                        config.getDouble("bounding-box.location.second-corner.y"),
                        config.getDouble("bounding-box.location.second-corner.z")
                ),
                config.getDoubleList("bounding-box.radius")
        );
        final var equipmentSlots = new ArrayList<EquipmentSlot>();
        final ConfigurationSection slotsSection = config.getConfigurationSection("ignorable-items.slots");

        if (slotsSection != null) {
            try {
                for (final var string : slotsSection.getValues(false).keySet()) {
                    equipmentSlots.add(EquipmentSlot.valueOf(string.toUpperCase(Locale.ROOT)));
                }
            } catch (final IllegalArgumentException e) {
                throw new IllegalArgumentException("Anomaly config specified an invalid equipment slot name", e);
            }
        }

        final var items = new HashMap<EquipmentSlot, ItemStack>();

        for (final var equipmentSlot : equipmentSlots) {
            final String name = equipmentSlot.name().toLowerCase(Locale.ROOT);
            final ItemStack itemStack = new ItemStack(Material.valueOf(slotsSection.getString(name + ".material")));
            final ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setCustomModelData(slotsSection.getInt(name + ".custom-model-data"));
            itemStack.setItemMeta(itemMeta);
            items.put(equipmentSlot, itemStack);
        }

        final AnomalyIgnorableItems anomalyIgnorableItems = new AnomalyIgnorableItems(
                items,
                config.getInt("ignorable-items.breaking-per-action")
        );

        final var anomalyActionMap = new HashMap<Double, List<AnomalyAction>>();

        for (final var radius : anomalyBoundingBox.getRadii()) {
            final ConfigurationSection radiusSection = config.getConfigurationSection("on-entering-to-area." + radius);
            final var actionStrings =
                    Objects.requireNonNull(radiusSection, "Anomaly configuration radii not properly configured, anomaly : " + fileName)
                    .getValues(false).keySet();

            for (final var anomalyAction : actionStrings) {
                final AnomalyAction action;

                switch (anomalyAction) {
                    case "add-potion-effect" -> {
                        final var potionEffects = new ArrayList<PotionEffect>();
                        final ConfigurationSection effectsSection = radiusSection.getConfigurationSection("add-potion-effect.effects");

                        for (final var potionStr : Objects.requireNonNull(effectsSection).getValues(false).keySet()) {
                            final ConfigurationSection potionSection = effectsSection.getConfigurationSection(potionStr);
                            assert potionSection != null;
                            final PotionEffectType potionEffectType = PotionEffectType.getByName(potionStr);

                            potionEffects.add(new PotionEffect(
                                    Objects.requireNonNull(potionEffectType, "Invalid effect type name in : " + fileName),
                                    potionSection.getInt("time"),
                                    potionSection.getInt("amplifier"),
                                    potionSection.getBoolean("ambient"),
                                    potionSection.getBoolean("particles"),
                                    potionSection.getBoolean("icon")
                            ));
                        }

                        action = new AddPotionAction(
                                radiusSection.getLong("add-potion-effect.time"),
                                radiusSection.getInt("add-potion-effect.percentage"),
                                potionEffects
                        );
                    }
                    case "spawn-particles" -> {
                        final var particleBuilderList = new ArrayList<ParticleBuilder>();
                        final ConfigurationSection particlesSection = radiusSection.getConfigurationSection("spawn-particles.particles");

                        for (final var particleStr : Objects.requireNonNull(particlesSection).getValues(false).keySet()) {
                            final ConfigurationSection particleSection = particlesSection.getConfigurationSection(particleStr);
                            assert particleSection != null;
                            final Particle particle = Particle.valueOf(particleStr);
                            final ParticleBuilder particleBuilder = new ParticleBuilder(particle)
                                    .count(particleSection.getInt("count"))
                                    .offset(
                                            particleSection.getDouble("offset.x"),
                                            particleSection.getDouble("offset.y"),
                                            particleSection.getDouble("offset.z")
                                    );

                            particleBuilderList.add(
                                    particleBuilder.particle() == Particle.REDSTONE
                                            ? particleBuilder.color(
                                            Color.fromRGB(particleSection.getInt("color")),
                                            (float) particleSection.getDouble("particle-size"))
                                            : particleBuilder
                            );
                        }

                        action = new SpawnParticlesAction(
                                radiusSection.getLong("spawn-particles.time"),
                                radiusSection.getInt("spawn-particles.percentage"),
                                particleBuilderList
                        );
                    }
                    default -> action = null;
                }

                if (action != null) {
                    if (anomalyActionMap.containsKey(radius)) {
                        final var actions = new ArrayList<>(anomalyActionMap.get(radius));

                        actions.add(action);
                        anomalyActionMap.put(radius, actions);
                    } else {
                        anomalyActionMap.put(radius, ImmutableList.of(action));
                    }
                }
            }
        }

        final var ignorablePlayers = new ArrayList<OfflinePlayer>();

        for (final var uuid : config.getStringList("ignorable-players")) {
            ignorablePlayers.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
        }

        return new Anomaly(
                new NamespacedKey(MSEssentials.getInstance(),
                        Objects.requireNonNull(config.getString("namespaced-key"), "namespaced-key in " + fileName + " is null")
                ),
                anomalyBoundingBox,
                equipmentSlots.isEmpty() ? null : anomalyIgnorableItems,
                anomalyActionMap,
                ignorablePlayers
        );
    }

    /**
     * @param anomalyAction Anomaly action to get the radius of
     * @param radius        Radius to check
     * @return True if the radios is the same as the anomaly action radius
     */
    public boolean isAnomalyActionRadius(
            final @NotNull AnomalyAction anomalyAction,
            final double radius
    ) {
        for (final var action : this.anomalyActionMap.entrySet()) {
            if (action.getValue().contains(anomalyAction)) {
                return action.getKey() == radius;
            }
        }
        return false;
    }

    /**
     * @return Namespaced key associated with the anomaly
     */
    public @NotNull NamespacedKey getNamespacedKey() {
        return this.namespacedKey;
    }

    /**
     * @return Bounding box of the anomaly
     */
    public @NotNull AnomalyBoundingBox getBoundingBox() {
        return this.anomalyBoundingBox;
    }

    /**
     * @return Ignorable items of the anomaly,
     *         null if the anomaly does not have ignorable items.
     * @see AnomalyIgnorableItems
     */
    public @Nullable AnomalyIgnorableItems getIgnorableItems() {
        return this.anomalyIgnorableItems;
    }

    /**
     * @return Map of anomaly actions and their radius to be executed when a player enters the anomaly
     */
    public @NotNull Map<Double, List<AnomalyAction>> getAnomalyActionMap() {
        return this.anomalyActionMap;
    }

    /**
     * @return List of ignorable players of the anomaly,
     *         empty list if the anomaly does not have ignorable players.
     *         These players will not be affected by the anomaly.
     */
    public @NotNull List<OfflinePlayer> getIgnorablePlayers() {
        return this.ignorablePlayers;
    }
}
