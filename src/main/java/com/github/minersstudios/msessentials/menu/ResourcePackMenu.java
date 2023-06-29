package com.github.minersstudios.msessentials.menu;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.CustomInventoryMap;
import com.github.minersstudios.mscore.inventory.InventoryButton;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.PlayerInfoMap;
import com.github.minersstudios.msessentials.player.PlayerSettings;
import com.github.minersstudios.msessentials.player.ResourcePack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslationComponent;
import static com.github.minersstudios.mscore.inventory.InventoryButton.playClickSound;

public class ResourcePackMenu {

    public static @NotNull CustomInventory create() {
        ItemStack pick = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta pickMeta = pick.getItemMeta();
        pickMeta.displayName(renderTranslationComponent("ms.menu.resource_pack.button.info.title").style(ChatUtils.DEFAULT_STYLE));
        pickMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        pickMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        pickMeta.lore(List.of(
                renderTranslationComponent("ms.menu.resource_pack.button.info.lore.0").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.info.lore.1").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.info.lore.2").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.info.lore.3").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.info.lore.4").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY)
        ));
        pick.setItemMeta(pickMeta);

        ItemStack none = new ItemStack(Material.COAL_BLOCK);
        ItemMeta noneMeta = none.getItemMeta();
        noneMeta.displayName(renderTranslationComponent("ms.menu.resource_pack.button.none.title").style(ChatUtils.DEFAULT_STYLE));
        noneMeta.lore(List.of(
                renderTranslationComponent("ms.menu.resource_pack.button.none.lore.0").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.none.lore.1").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY)
        ));
        none.setItemMeta(noneMeta);

        ItemStack lite = new ItemStack(Material.IRON_BLOCK);
        ItemMeta liteMeta = lite.getItemMeta();
        liteMeta.displayName(renderTranslationComponent("ms.menu.resource_pack.button.lite.title").style(ChatUtils.DEFAULT_STYLE));
        liteMeta.lore(List.of(
                renderTranslationComponent("ms.menu.resource_pack.button.lite.lore.0").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.lite.lore.1").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.lite.lore.2").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.lite.lore.3").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY)
        ));
        lite.setItemMeta(liteMeta);

        ItemStack full = new ItemStack(Material.NETHERITE_BLOCK);
        ItemMeta fullMeta = full.getItemMeta();
        fullMeta.displayName(renderTranslationComponent("ms.menu.resource_pack.button.full.title").style(ChatUtils.DEFAULT_STYLE));
        fullMeta.lore(List.of(
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.0").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.1").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.2").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.3").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.4").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.5").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.6").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.7").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.8").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.9").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.10").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.11").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                renderTranslationComponent("ms.menu.resource_pack.button.full.lore.12").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY)
        ));
        full.setItemMeta(fullMeta);

        PlayerInfoMap playerInfoMap = MSEssentials.getConfigCache().playerInfoMap;

        InventoryButton noneButton = InventoryButton.create()
                .item(none)
                .clickAction((event, inventory, button) -> {
                    Player player = (Player) event.getWhoClicked();
                    PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);
                    PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();

                    if (playerSettings.getResourcePackType() != ResourcePack.Type.NULL && playerSettings.getResourcePackType() != ResourcePack.Type.NONE) {
                        playerInfo.kickPlayer(
                                Component.translatable("ms.menu.resource_pack.button.none.kick.title"),
                                Component.translatable("ms.menu.resource_pack.button.none.kick.subtitle")
                        );
                    }

                    playerSettings.setResourcePackType(ResourcePack.Type.NONE);
                    playerSettings.save();
                    playClickSound(player);
                    player.closeInventory();

                    if (playerInfo.isInWorldDark()) {
                        playerInfo.initJoin();
                    }
                });

        InventoryButton fullButton = InventoryButton.create()
                .item(full)
                .clickAction((event, inventory, button) -> {
                    Player player = (Player) event.getWhoClicked();
                    PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);
                    PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();

                    playerSettings.setResourcePackType(ResourcePack.Type.FULL);
                    playerSettings.save();
                    playClickSound(player);
                    player.closeInventory();
                    ResourcePack.setResourcePack(playerInfo);
                });

        InventoryButton liteButton = InventoryButton.create()
                .item(lite)
                .clickAction((event, inventory, button) -> {
                    Player player = (Player) event.getWhoClicked();
                    PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);
                    PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();

                    playerSettings.setResourcePackType(ResourcePack.Type.LITE);
                    playerSettings.save();
                    playClickSound(player);
                    player.closeInventory();
                    ResourcePack.setResourcePack(playerInfo);
                });

        return CustomInventory.create(Component.translatable("ms.menu.resource_pack.title"), 1)
                .buttonAt(0, noneButton)
                .buttonAt(1, noneButton)
                .buttonAt(2, fullButton)
                .buttonAt(3, fullButton)
                .buttonAt(4, InventoryButton.create().item(pick))
                .buttonAt(5, fullButton)
                .buttonAt(6, fullButton)
                .buttonAt(7, liteButton)
                .buttonAt(8, liteButton)
                .closeAction(((event, inventory) -> {
                    Player player = (Player) event.getPlayer();
                    PlayerInfo playerInfo = playerInfoMap.getPlayerInfo(player);
                    ResourcePack.Type type = playerInfo.getPlayerFile().getPlayerSettings().getResourcePackType();

                    if (type == ResourcePack.Type.NULL) {
                        Bukkit.getScheduler().runTask(MSEssentials.getInstance(), () -> player.openInventory(inventory));
                    }
                }));
    }

    public static boolean open(@NotNull Player player) {
        CustomInventoryMap customInventoryMap = MSCore.getCache().customInventoryMap;
        CustomInventory customInventory = customInventoryMap.get("resourcepack");
        if (customInventory == null) return false;
        player.openInventory(customInventory);
        return true;
    }
}
