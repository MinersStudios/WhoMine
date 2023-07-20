package com.minersstudios.msitem.utils;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.items.CustomItem;
import com.minersstudios.msitem.items.RenameableItem;
import com.minersstudios.msitem.items.register.cosmetics.LeatherHat;
import com.minersstudios.msitem.items.register.items.*;
import com.minersstudios.msitem.items.register.items.armor.hazmat.HazmatBoots;
import com.minersstudios.msitem.items.register.items.armor.hazmat.HazmatChestplate;
import com.minersstudios.msitem.items.register.items.armor.hazmat.HazmatHelmet;
import com.minersstudios.msitem.items.register.items.armor.hazmat.HazmatLeggings;
import com.minersstudios.msitem.items.register.items.cards.CardsBicycle;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class ConfigCache {
    public final @NotNull File configFile;
    public final @NotNull YamlConfiguration configYaml;

    public final long dosimeterCheckRate;

    public final List<CustomItem> recipeItems = new ArrayList<>();
    public final Map<Player, EquipmentSlot> dosimeterPlayers = new ConcurrentHashMap<>();
    public final List<BukkitTask> bukkitTasks = new ArrayList<>();

    public ConfigCache() {
        this.configFile = MSItem.getInstance().getConfigFile();
        this.configYaml = YamlConfiguration.loadConfiguration(this.configFile);

        this.dosimeterCheckRate = this.configYaml.getLong("dosimeter-check-rate");

        try (var path = Files.walk(Paths.get(MSItem.getInstance().getPluginFolder() + "/items"))) {
            path
            .filter(Files::isRegularFile)
            .map(Path::toFile)
            .forEach(file -> {
                String fileName = file.getName();
                if (fileName.equalsIgnoreCase("example.yml")) return;
                YamlConfiguration renameableItemConfig = YamlConfiguration.loadConfiguration(file);

                var materialsString = renameableItemConfig.getStringList("material");
                if (materialsString.isEmpty()) {
                    materialsString.add(renameableItemConfig.getString("material"));
                }

                var materials = new ArrayList<Material>();
                for (var material : materialsString) {
                    materials.add(Material.valueOf(material));
                }

                var renameableItemStacks = new ArrayList<ItemStack>();
                for (var material : materials) {
                    renameableItemStacks.add(new ItemStack(material));
                }

                ItemStack resultItemStack = new ItemStack(materials.get(0));
                ItemMeta itemMeta = resultItemStack.getItemMeta();
				var lore = renameableItemConfig.getStringList("lore");

                if (!lore.isEmpty()) {
					var loreComponentList = new ArrayList<Component>();

                    for (var text : lore) {
                        loreComponentList.add(Component.text(text));
                    }

                    itemMeta.lore(loreComponentList);
                }

                itemMeta.setCustomModelData(renameableItemConfig.getInt("custom-model-data"));
                resultItemStack.setItemMeta(itemMeta);

                var offlinePlayers = new HashSet<OfflinePlayer>();
                for (var uuid : renameableItemConfig.getStringList("whitelist")) {
                    offlinePlayers.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
                }

                RenameableItem renameableItem = new RenameableItem(
                        new NamespacedKey(MSItem.getInstance(), Objects.requireNonNull(renameableItemConfig.getString("namespaced-key"), fileName + " namespaced-key must be NotNull!")),
                        Objects.requireNonNull(renameableItemConfig.getString("rename-text"), fileName + " rename-text must be NotNull!"),
                        renameableItemStacks,
                        resultItemStack,
                        renameableItemConfig.getBoolean("show-in-rename-menu"),
                        offlinePlayers
                );
                MSPlugin.getGlobalCache().renameableItemMap.put(renameableItem.getNamespacedKey().getKey(), itemMeta.getCustomModelData(), renameableItem);
            });
        } catch (IOException e) {
            MSLogger.log(Level.SEVERE, "Error while loading items folder!", e);
        }
    }

    public void registerItems() {
        new LeatherHat().register();
        new RawPlumbum().register();
        new PlumbumIngot().register();
        new AntiRadiationTextile().register();
        new HazmatHelmet().register();
        new HazmatChestplate().register();
        new HazmatLeggings().register();
        new HazmatBoots().register();
        new Dosimeter().register();
        new BanSword().register();
        new Wrench().register();
        new CardsBicycle().register();
        new Cocaine().register();
    }
}
