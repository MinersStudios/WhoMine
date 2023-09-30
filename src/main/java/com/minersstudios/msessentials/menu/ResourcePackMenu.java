package com.minersstudios.msessentials.menu;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.inventory.InventoryButton;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.PlayerSettings;
import com.minersstudios.msessentials.player.ResourcePack;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

public class ResourcePackMenu {
    private static final CustomInventory INVENTORY;

    static {
        final ItemStack infoItem = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta infoMeta = infoItem.getItemMeta();

        infoMeta.displayName(LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.info.title").style(ChatUtils.DEFAULT_STYLE));
        infoMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        infoMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        infoMeta.lore(ImmutableList.of(
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.info.lore.0").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.info.lore.1").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.info.lore.2").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.info.lore.3").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.info.lore.4").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY)
        ));
        infoItem.setItemMeta(infoMeta);

        final ItemStack noneItem = new ItemStack(Material.COAL_BLOCK);
        final ItemMeta noneMeta = noneItem.getItemMeta();

        noneMeta.displayName(LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.none.title").style(ChatUtils.DEFAULT_STYLE));
        noneMeta.lore(ImmutableList.of(
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.none.lore.0").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.none.lore.1").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY)
        ));
        noneItem.setItemMeta(noneMeta);

        final ItemStack liteItem = new ItemStack(Material.IRON_BLOCK);
        final ItemMeta liteMeta = liteItem.getItemMeta();

        liteMeta.displayName(LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.lite.title").style(ChatUtils.DEFAULT_STYLE));
        liteMeta.lore(ImmutableList.of(
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.lite.lore.0").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.lite.lore.1").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.lite.lore.2").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.lite.lore.3").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY)
        ));
        liteItem.setItemMeta(liteMeta);

        final ItemStack fullItem = new ItemStack(Material.NETHERITE_BLOCK);
        final ItemMeta fullMeta = fullItem.getItemMeta();

        fullMeta.displayName(LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.title").style(ChatUtils.DEFAULT_STYLE));
        fullMeta.lore(ImmutableList.of(
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.0").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.1").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.2").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.3").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.4").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.5").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.6").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.7").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.8").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.9").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.10").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.11").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY),
                LanguageFile.renderTranslationComponent("ms.menu.resource_pack.button.full.lore.12").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY)
        ));
        fullItem.setItemMeta(fullMeta);

        final InventoryButton noneButton = new InventoryButton(noneItem, (event, inventory) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
            final PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();

            if (playerSettings.getResourcePackType() != ResourcePack.Type.NULL && playerSettings.getResourcePackType() != ResourcePack.Type.NONE) {
                playerInfo.kickPlayer(
                        translatable("ms.menu.resource_pack.button.none.kick.title"),
                        translatable("ms.menu.resource_pack.button.none.kick.subtitle")
                );
            }

            playerSettings.setResourcePackType(ResourcePack.Type.NONE);
            playerSettings.save();
            InventoryButton.playClickSound(player);
            player.closeInventory();

            if (playerInfo.isInWorldDark()) {
                playerInfo.handleJoin();
            }
        });

        final InventoryButton fullButton = new InventoryButton(fullItem, (event, inventory) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
            final PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();

            playerSettings.setResourcePackType(ResourcePack.Type.FULL);
            playerSettings.save();
            InventoryButton.playClickSound(player);
            player.closeInventory();
            playerInfo.handleResourcePack().thenAccept(bool -> {
                if (bool && playerInfo.isInWorldDark()) {
                    playerInfo.handleJoin();
                }
            });
        });

        final InventoryButton liteButton = new InventoryButton(liteItem, (event, inventory) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
            final PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();

            playerSettings.setResourcePackType(ResourcePack.Type.LITE);
            playerSettings.save();
            InventoryButton.playClickSound(player);
            player.closeInventory();
            playerInfo.handleResourcePack().thenAccept(bool -> {
                if (bool && playerInfo.isInWorldDark()) {
                    playerInfo.handleJoin();
                }
            });
        });

        INVENTORY = CustomInventory.single(translatable("ms.menu.resource_pack.title", ChatUtils.DEFAULT_STYLE), 1)
                .buttonAt(0, noneButton)
                .buttonAt(1, noneButton)
                .buttonAt(2, fullButton)
                .buttonAt(3, fullButton)
                .buttonAt(4, new InventoryButton().item(infoItem))
                .buttonAt(5, fullButton)
                .buttonAt(6, fullButton)
                .buttonAt(7, liteButton)
                .buttonAt(8, liteButton)
                .closeAction(((event, inventory) -> {
                    final Player player = (Player) event.getPlayer();
                    final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
                    final ResourcePack.Type type = playerInfo.getPlayerFile().getPlayerSettings().getResourcePackType();

                    if (type == ResourcePack.Type.NULL) {
                        MSEssentials.getInstance().runTask(() -> player.openInventory(inventory));
                    }
                }));
    }

    public static void open(final @NotNull Player player) {
        INVENTORY.open(player);
    }
}
