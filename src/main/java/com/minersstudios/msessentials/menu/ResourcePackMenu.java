package com.minersstudios.msessentials.menu;

import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.inventory.InventoryButton;
import com.minersstudios.mscore.inventory.plugin.AbstractInventoryHolder;
import com.minersstudios.mscore.inventory.plugin.InventoryHolder;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.PlayerSettings;
import com.minersstudios.msessentials.player.ResourcePack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static com.minersstudios.mscore.utility.ChatUtils.COLORLESS_DEFAULT_STYLE;
import static com.minersstudios.mscore.utility.ChatUtils.DEFAULT_STYLE;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@InventoryHolder
public final class ResourcePackMenu extends AbstractInventoryHolder<MSEssentials> {

    @Override
    protected @NotNull CustomInventory createCustomInventory() {
        final MSEssentials plugin = this.getPlugin();
        final ItemStack infoItem = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta infoMeta = infoItem.getItemMeta();

        infoMeta.displayName(MENU_RESOURCE_PACK_BUTTON_INFO_TITLE.style(DEFAULT_STYLE));
        infoMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        infoMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        infoMeta.lore(Arrays.asList(
                MENU_RESOURCE_PACK_BUTTON_INFO_LORE_0.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_INFO_LORE_1.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_INFO_LORE_2.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_INFO_LORE_3.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_INFO_LORE_4.style(COLORLESS_DEFAULT_STYLE).color(GRAY)
        ));
        infoItem.setItemMeta(infoMeta);

        final ItemStack noneItem = new ItemStack(Material.COAL_BLOCK);
        final ItemMeta noneMeta = noneItem.getItemMeta();

        noneMeta.displayName(MENU_RESOURCE_PACK_BUTTON_NONE_TITLE.style(DEFAULT_STYLE));
        noneMeta.lore(Arrays.asList(
                MENU_RESOURCE_PACK_BUTTON_NONE_LORE_0.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_NONE_LORE_1.style(COLORLESS_DEFAULT_STYLE).color(GRAY)
        ));
        noneItem.setItemMeta(noneMeta);

        final ItemStack liteItem = new ItemStack(Material.IRON_BLOCK);
        final ItemMeta liteMeta = liteItem.getItemMeta();

        liteMeta.displayName(MENU_RESOURCE_PACK_BUTTON_LITE_TITLE.style(DEFAULT_STYLE));
        liteMeta.lore(Arrays.asList(
                MENU_RESOURCE_PACK_BUTTON_LITE_LORE_0.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_LITE_LORE_1.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_LITE_LORE_2.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_LITE_LORE_3.style(COLORLESS_DEFAULT_STYLE).color(GRAY)
        ));
        liteItem.setItemMeta(liteMeta);

        final ItemStack fullItem = new ItemStack(Material.NETHERITE_BLOCK);
        final ItemMeta fullMeta = fullItem.getItemMeta();

        fullMeta.displayName(MENU_RESOURCE_PACK_BUTTON_FULL_TITLE.style(DEFAULT_STYLE));
        fullMeta.lore(Arrays.asList(
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_0.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_1.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_2.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_3.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_4.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_5.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_6.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_7.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_8.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_9.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_10.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_11.style(COLORLESS_DEFAULT_STYLE).color(GRAY),
                MENU_RESOURCE_PACK_BUTTON_FULL_LORE_12.style(COLORLESS_DEFAULT_STYLE).color(GRAY)
        ));
        fullItem.setItemMeta(fullMeta);

        final InventoryButton noneButton = new InventoryButton(noneItem, (event, inventory) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(plugin, player);
            final PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();
            final ResourcePack.Type packType = playerSettings.getResourcePackType();

            if (
                    packType != ResourcePack.Type.NULL
                    && packType != ResourcePack.Type.NONE
            ) {
                playerInfo.kick(
                        MENU_RESOURCE_PACK_BUTTON_NONE_KICK_TITLE,
                        MENU_RESOURCE_PACK_BUTTON_NONE_KICK_SUBTITLE,
                        PlayerKickEvent.Cause.RESOURCE_PACK_REJECTION
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
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(plugin, player);
            final PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();

            playerSettings.setResourcePackType(ResourcePack.Type.FULL);
            playerSettings.save();
            InventoryButton.playClickSound(player);
            player.closeInventory();
            playerInfo.handleResourcePack()
            .thenAccept(isLoaded -> {
                if (
                        isLoaded
                        && playerInfo.isInWorldDark()
                ) {
                    playerInfo.handleJoin();
                }
            });
        });

        final InventoryButton liteButton = new InventoryButton(liteItem, (event, inventory) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(plugin, player);
            final PlayerSettings playerSettings = playerInfo.getPlayerFile().getPlayerSettings();

            playerSettings.setResourcePackType(ResourcePack.Type.LITE);
            playerSettings.save();
            InventoryButton.playClickSound(player);
            player.closeInventory();
            playerInfo.handleResourcePack()
            .thenAccept(isLoaded -> {
                if (
                        isLoaded
                        && playerInfo.isInWorldDark()
                ) {
                    playerInfo.handleJoin();
                }
            });
        });

        return CustomInventory
                .single(
                        MENU_RESOURCE_PACK_TITLE.style(DEFAULT_STYLE),
                        1
                )
                .buttonAt(0, noneButton)
                .buttonAt(1, noneButton)
                .buttonAt(2, fullButton)
                .buttonAt(3, fullButton)
                .buttonAt(4, new InventoryButton().item(infoItem))
                .buttonAt(5, fullButton)
                .buttonAt(6, fullButton)
                .buttonAt(7, liteButton)
                .buttonAt(8, liteButton)
                .closeAction((event, inventory) -> {
                    final Player player = (Player) event.getPlayer();
                    final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(plugin, player);
                    final ResourcePack.Type type = playerInfo.getPlayerFile().getPlayerSettings().getResourcePackType();

                    if (type == ResourcePack.Type.NULL) {
                        plugin.runTask(() -> player.openInventory(inventory));
                    }
                });
    }

    @Override
    public void open(final @NotNull Player player) {
        this.getCustomInventory().open(player);
    }
}
