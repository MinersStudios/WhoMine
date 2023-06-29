package com.github.minersstudios.msessentials.anomalies;

import com.destroystokyo.paper.ParticleBuilder;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.anomalies.actions.AddPotionAction;
import com.github.minersstudios.msessentials.anomalies.actions.SpawnParticlesAction;
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

public class Anomaly {
    private final @NotNull NamespacedKey namespacedKey;
    private final @NotNull AnomalyBoundingBox anomalyBoundingBox;
    private final @Nullable AnomalyIgnorableItems anomalyIgnorableItems;
    private final @NotNull Map<Double, List<AnomalyAction>> anomalyActionMap;
    private final @NotNull List<OfflinePlayer> ignorablePlayers;

    public Anomaly(
            @NotNull NamespacedKey namespacedKey,
            @NotNull AnomalyBoundingBox anomalyBoundingBox,
            @Nullable AnomalyIgnorableItems anomalyIgnorableItems,
            @NotNull Map<Double, List<AnomalyAction>> anomalyActionMap,
            @NotNull List<OfflinePlayer> ignorablePlayers
    ) {
        this.namespacedKey = namespacedKey;
        this.anomalyBoundingBox = anomalyBoundingBox;
        this.anomalyIgnorableItems = anomalyIgnorableItems;
        this.anomalyActionMap = anomalyActionMap;
        this.ignorablePlayers = ignorablePlayers;
    }

    @Contract("_, _ -> new")
    public static @NotNull Anomaly fromConfig(
            @NotNull File file,
            @NotNull YamlConfiguration config
    ) {
        String fileName = file.getName();
        World world = Bukkit.getWorld(
                Objects.requireNonNull(config.getString("bounding-box.location.world-name"), "world in " + fileName + " is null")
        );
        AnomalyBoundingBox anomalyBoundingBox = new AnomalyBoundingBox(
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
        List<EquipmentSlot> equipmentSlots = new ArrayList<>();
        ConfigurationSection slotsSection = config.getConfigurationSection("ignorable-items.slots");

        if (slotsSection != null) {
            try {
                for (String string : slotsSection.getValues(false).keySet()) {
                    equipmentSlots.add(EquipmentSlot.valueOf(string.toUpperCase(Locale.ROOT)));
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Anomaly config specified an invalid equipment slot name", e);
            }
        }

        Map<EquipmentSlot, ItemStack> items = new HashMap<>();

        for (EquipmentSlot equipmentSlot : equipmentSlots) {
            String name = equipmentSlot.name().toLowerCase(Locale.ROOT);
            ItemStack itemStack = new ItemStack(Material.valueOf(slotsSection.getString(name + ".material")));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setCustomModelData(slotsSection.getInt(name + ".custom-model-data"));
            itemStack.setItemMeta(itemMeta);
            items.put(equipmentSlot, itemStack);
        }

        AnomalyIgnorableItems anomalyIgnorableItems = new AnomalyIgnorableItems(
                items,
                config.getInt("ignorable-items.breaking-per-action")
        );

        Map<Double, List<AnomalyAction>> anomalyActionMap = new HashMap<>();

        for (Double radius : anomalyBoundingBox.getRadii()) {
            ConfigurationSection radiusSection = config.getConfigurationSection("on-entering-to-area." + radius);
            Set<String> actionStrings =
                    Objects.requireNonNull(radiusSection, "Anomaly configuration radii not properly configured, anomaly : " + fileName)
                    .getValues(false).keySet();

            for (String anomalyAction : actionStrings) {
                AnomalyAction action;

                switch (anomalyAction) {
                    case "add-potion-effect" -> {
                        List<PotionEffect> potionEffects = new ArrayList<>();
                        ConfigurationSection effectsSection = radiusSection.getConfigurationSection("add-potion-effect.effects");

                        for (String potionStr : Objects.requireNonNull(effectsSection).getValues(false).keySet()) {
                            ConfigurationSection potionSection = effectsSection.getConfigurationSection(potionStr);
                            assert potionSection != null;
                            PotionEffectType potionEffectType = PotionEffectType.getByName(potionStr);

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
                        List<ParticleBuilder> particleBuilderList = new ArrayList<>();
                        ConfigurationSection particlesSection = radiusSection.getConfigurationSection("spawn-particles.particles");

                        for (String particleStr : Objects.requireNonNull(particlesSection).getValues(false).keySet()) {
                            ConfigurationSection particleSection = particlesSection.getConfigurationSection(particleStr);
                            assert particleSection != null;
                            Particle particle = Particle.valueOf(particleStr);
                            ParticleBuilder particleBuilder = new ParticleBuilder(particle)
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
                        List<AnomalyAction> actions = new ArrayList<>(anomalyActionMap.get(radius));
                        actions.add(action);
                        anomalyActionMap.put(radius, actions);
                    } else {
                        anomalyActionMap.put(radius, List.of(action));
                    }
                }
            }
        }

        List<OfflinePlayer> ignorablePlayers = new ArrayList<>();

        for (String uuid : config.getStringList("ignorable-players")) {
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

    public boolean isAnomalyActionRadius(@NotNull AnomalyAction anomalyAction, double radius) {
        for (Map.Entry<Double, List<AnomalyAction>> action : this.anomalyActionMap.entrySet()) {
            if (action.getValue().contains(anomalyAction)) {
                return action.getKey() == radius;
            }
        }
        return false;
    }

    public @NotNull NamespacedKey getNamespacedKey() {
        return this.namespacedKey;
    }

    public @NotNull AnomalyBoundingBox getBoundingBox() {
        return this.anomalyBoundingBox;
    }

    public @Nullable AnomalyIgnorableItems getIgnorableItems() {
        return this.anomalyIgnorableItems;
    }

    public @NotNull Map<Double, List<AnomalyAction>> getAnomalyActionMap() {
        return this.anomalyActionMap;
    }

    public @NotNull List<OfflinePlayer> getIgnorablePlayers() {
        return this.ignorablePlayers;
    }
}
