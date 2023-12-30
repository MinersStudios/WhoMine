package com.minersstudios.msessentials.anomaly;

import com.destroystokyo.paper.ParticleBuilder;
import com.minersstudios.mscore.location.MSBoundingBox;
import com.minersstudios.msessentials.Cache;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.anomaly.action.AddPotionAction;
import com.minersstudios.msessentials.anomaly.action.SpawnParticlesAction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.*;

/**
 * Anomaly class with all anomaly data and associated namespaced key.
 * All anomalies are cached in {@link Cache#getAnomalies()} .
 * <br>
 * Can have :
 * <ul>
 *     <li>{@link AnomalyBoundingBox} - radii and location of anomaly</li>
 *     <li>{@link AnomalyIgnorableItems} - ignorable items</li>
 *     <li>{@link AnomalyAction} - anomaly actions, which will be executed when player is in anomaly</li>
 *     <li>{@link OfflinePlayer} - ignorable players, which will be ignored by anomaly actions</li>
 * </ul>
 *
 * @see #fromConfig(MSEssentials, File)
 */
public class Anomaly {
    private final NamespacedKey namespacedKey;
    private final AnomalyBoundingBox anomalyBoundingBox;
    private final AnomalyIgnorableItems anomalyIgnorableItems;
    private final Double2ObjectMap<List<AnomalyAction>> anomalyActionMap;
    private final Set<OfflinePlayer> ignorablePlayers;

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
            final @NotNull Double2ObjectMap<List<AnomalyAction>> anomalyActionMap,
            final @NotNull Set<OfflinePlayer> ignorablePlayers
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
    @Contract("_, _ -> new")
    public static @NotNull Anomaly fromConfig(
            final @NotNull MSEssentials plugin,
            final @NotNull File file
    ) throws IllegalArgumentException {
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        final String namespacedKeyStr = config.getString("namespaced-key");

        if (namespacedKeyStr == null) {
            throw new IllegalArgumentException("Namespaced key is not specified");
        }

        final String worldName = config.getString("bounding-box.location.world-name");

        if (worldName == null) {
            throw new IllegalArgumentException("World name is not specified");
        }

        final World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new IllegalArgumentException("Specified world does not exist");
        }

        final AnomalyBoundingBox anomalyBoundingBox = new AnomalyBoundingBox(
                world,
                MSBoundingBox.of(
                        config.getDouble("bounding-box.location.first-corner.x"),
                        config.getDouble("bounding-box.location.first-corner.y"),
                        config.getDouble("bounding-box.location.first-corner.z"),
                        config.getDouble("bounding-box.location.second-corner.x"),
                        config.getDouble("bounding-box.location.second-corner.y"),
                        config.getDouble("bounding-box.location.second-corner.z")
                ),
                config.getDoubleList("bounding-box.radius")
        );
        final var equipmentSlots = new ObjectArrayList<EquipmentSlot>();
        final ConfigurationSection slotsSection = config.getConfigurationSection("ignorable-items.slots");
        final var items = new EnumMap<EquipmentSlot, ItemStack>(EquipmentSlot.class);

        if (slotsSection != null) {
            try {
                for (final var string : slotsSection.getValues(false).keySet()) {
                    equipmentSlots.add(EquipmentSlot.valueOf(string.toUpperCase(Locale.ROOT)));
                }
            } catch (final IllegalArgumentException e) {
                throw new IllegalArgumentException("Anomaly config specified an invalid equipment slot name", e);
            }

            for (final var equipmentSlot : equipmentSlots) {
                final String name = equipmentSlot.name().toLowerCase(Locale.ROOT);
                final ItemStack itemStack = new ItemStack(Material.valueOf(slotsSection.getString(name + ".material")));
                final ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setCustomModelData(slotsSection.getInt(name + ".custom-model-data"));
                itemStack.setItemMeta(itemMeta);
                items.put(equipmentSlot, itemStack);
            }
        }

        final AnomalyIgnorableItems anomalyIgnorableItems = new AnomalyIgnorableItems(
                plugin,
                items,
                config.getInt("ignorable-items.breaking-per-action")
        );

        final var anomalyActionMap = new Double2ObjectOpenHashMap<List<AnomalyAction>>();

        for (final double radius : anomalyBoundingBox.getRadii()) {
            final ConfigurationSection radiusSection = config.getConfigurationSection("on-entering-to-area." + radius);

            if (radiusSection == null) {
                throw new IllegalArgumentException("Anomaly action radii not properly configured");
            }

            final var actionStrings = radiusSection.getValues(false).keySet();

            for (final var anomalyAction : actionStrings) {
                final AnomalyAction action;

                switch (anomalyAction) {
                    case "add-potion-effect" -> {
                        final var potionEffects = new ObjectArrayList<PotionEffect>();
                        final ConfigurationSection effectsSection = radiusSection.getConfigurationSection("add-potion-effect.effects");

                        if (effectsSection == null) {
                            throw new IllegalArgumentException("Effects section not specified in add-potion-effect");
                        }

                        for (final var potionStr : effectsSection.getValues(false).keySet()) {
                            final ConfigurationSection potionSection = effectsSection.getConfigurationSection(potionStr);
                            final PotionEffectType potionEffectType =
                                    Registry.POTION_EFFECT_TYPE.get(
                                            new NamespacedKey(
                                                    NamespacedKey.MINECRAFT,
                                                    potionStr
                                            )
                                    );

                            assert potionSection != null;

                            if (potionEffectType == null) {
                                throw new IllegalArgumentException("Potion effect type is invalid");
                            }

                            potionEffects.add(new PotionEffect(
                                    potionEffectType,
                                    potionSection.getInt("time"),
                                    potionSection.getInt("amplifier"),
                                    potionSection.getBoolean("ambient"),
                                    potionSection.getBoolean("particles"),
                                    potionSection.getBoolean("icon")
                            ));
                        }

                        action = new AddPotionAction(
                                plugin,
                                radiusSection.getLong("add-potion-effect.time"),
                                radiusSection.getInt("add-potion-effect.percentage"),
                                potionEffects.toArray(new PotionEffect[0])
                        );
                    }
                    case "spawn-particles" -> {
                        final var particleBuilderList = new ObjectArrayList<ParticleBuilder>();
                        final ConfigurationSection particlesSection = radiusSection.getConfigurationSection("spawn-particles.particles");

                        if (particlesSection == null) {
                            throw new IllegalArgumentException("Particles section not specified in spawn-particles");
                        }

                        for (final var particleStr : particlesSection.getValues(false).keySet()) {
                            final ConfigurationSection particleSection = particlesSection.getConfigurationSection(particleStr);
                            assert particleSection != null;
                            final Particle particle = Particle.valueOf(particleStr);
                            final ParticleBuilder particleBuilder =
                                    new ParticleBuilder(particle)
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
                                                    (float) particleSection.getDouble("particle-size")
                                            )
                                            : particleBuilder
                            );
                        }

                        action = new SpawnParticlesAction(
                                plugin,
                                radiusSection.getLong("spawn-particles.time"),
                                radiusSection.getInt("spawn-particles.percentage"),
                                particleBuilderList.toArray(new ParticleBuilder[0])
                        );
                    }
                    default -> throw new IllegalArgumentException("Specified invalid anomaly action : " + anomalyAction);
                }

                if (anomalyActionMap.containsKey(radius)) {
                    final var actions = new ObjectArrayList<>(anomalyActionMap.get(radius));

                    actions.add(action);
                    anomalyActionMap.put(radius, actions);
                } else {
                    anomalyActionMap.put(radius, Collections.singletonList(action));
                }
            }
        }

        final var ignorablePlayers = new ObjectOpenHashSet<OfflinePlayer>();

        for (final var uuid : config.getStringList("ignorable-players")) {
            ignorablePlayers.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
        }

        return new Anomaly(
                new NamespacedKey(
                        MSEssentials.NAMESPACE,
                        namespacedKeyStr
                ),
                anomalyBoundingBox,
                equipmentSlots.isEmpty()
                        ? null
                        : anomalyIgnorableItems,
                anomalyActionMap,
                ignorablePlayers
        );
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
     * @return Ignorable items of the anomaly, null if the anomaly does not have
     *         ignorable items.
     * @see AnomalyIgnorableItems
     */
    public @Nullable AnomalyIgnorableItems getIgnorableItems() {
        return this.anomalyIgnorableItems;
    }

    /**
     * @return Map of anomaly actions and their radius to be executed when a
     *         player enters the anomaly
     */
    public @NotNull @Unmodifiable Map<Double, List<AnomalyAction>> getAnomalyActionMap() {
        return Collections.unmodifiableMap(this.anomalyActionMap);
    }

    /**
     * @return Set of ignorable players of the anomaly, empty list if the
     *         anomaly does not have ignorable players. The anomaly will not
     *         affect these players.
     */
    public @NotNull @Unmodifiable Set<OfflinePlayer> getIgnorablePlayers() {
        return Collections.unmodifiableSet(this.ignorablePlayers);
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
        for (final var entry : this.anomalyActionMap.double2ObjectEntrySet()) {
            if (entry.getValue().contains(anomalyAction)) {
                return entry.getDoubleKey() == radius;
            }
        }

        return false;
    }
}
