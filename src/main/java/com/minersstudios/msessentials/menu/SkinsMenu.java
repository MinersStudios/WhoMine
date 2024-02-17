package com.minersstudios.msessentials.menu;

import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.inventory.InventoryButton;
import com.minersstudios.mscore.inventory.plugin.AbstractInventoryHolder;
import com.minersstudios.mscore.inventory.plugin.InventoryHolder;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static com.minersstudios.mscore.locale.Translations.*;
import static net.kyori.adventure.text.Component.text;

@InventoryHolder
public final class SkinsMenu extends AbstractInventoryHolder<MSEssentials> {
    private static final Component TITLE = MENU_SKINS_TITLE.asTranslatable().style(ChatUtils.DEFAULT_STYLE);
    private static final InventoryButton EMPTY_BUTTON = new InventoryButton().item(new ItemStack(Material.AIR));
    private InventoryButton applyButton;
    private InventoryButton applyButtonEmpty;
    private InventoryButton deleteButton;
    private InventoryButton deleteButtonEmpty;

    @Override
    protected @NotNull CustomInventory createCustomInventory() {
        final MSEssentials plugin = this.getPlugin();
        final ItemStack applyItem = new ItemStack(Material.PAPER);
        final ItemMeta applyMeta = applyItem.getItemMeta();

        final ItemStack applyEmpty = applyItem.clone();
        final ItemMeta applyEmptyMeta = applyEmpty.getItemMeta();

        final Component applyName = MENU_SKINS_BUTTON_APPLY.asComponent().style(ChatUtils.DEFAULT_STYLE);

        applyMeta.displayName(applyName);
        applyMeta.setCustomModelData(5004);
        applyItem.setItemMeta(applyMeta);

        applyEmptyMeta.displayName(applyName);
        applyEmptyMeta.setCustomModelData(1);
        applyEmpty.setItemMeta(applyEmptyMeta);

        this.applyButton = new InventoryButton(applyItem, (event, inv) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(plugin, player);
            final Skin skin = playerInfo.getPlayerFile().getSkin((int) inv.args().get(0));

            if (skin == null) {
                return;
            }

            playerInfo.setSkin(skin);
            player.closeInventory();
        });
        this.applyButtonEmpty = this.applyButton.clone().item(applyEmpty);

        final ItemStack deleteItem = new ItemStack(Material.PAPER);
        final ItemMeta deleteMeta = deleteItem.getItemMeta();

        final ItemStack deleteEmpty = deleteItem.clone();
        final ItemMeta deleteEmptyMeta = deleteEmpty.getItemMeta();

        final Component deleteName = MENU_SKINS_BUTTON_DELETE.asComponent().style(ChatUtils.DEFAULT_STYLE);

        deleteMeta.displayName(deleteName);
        deleteMeta.setCustomModelData(5005);
        deleteItem.setItemMeta(deleteMeta);

        deleteEmptyMeta.displayName(deleteName);
        deleteEmptyMeta.setCustomModelData(1);
        deleteEmpty.setItemMeta(deleteEmptyMeta);

        this.deleteButton = new InventoryButton(deleteItem, (event, inv) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(plugin, player);
            final PlayerFile playerFile = playerInfo.getPlayerFile();

            try {
                final Skin selectedSkin = playerFile.getSkin((int) inv.args().get(0));

                if (selectedSkin == null) {
                    return;
                }

                final Component skinName = text(selectedSkin.getName());

                playerFile.removeSkin(selectedSkin);
                MSLogger.fine(
                        player,
                        DISCORD_SKIN_SUCCESSFULLY_REMOVED_MINECRAFT.asTranslatable()
                        .arguments(skinName)
                );
                playerInfo.sendPrivateDiscordMessage(BotHandler.craftEmbed(
                        ChatUtils.serializePlainComponent(
                                DISCORD_SKIN_SUCCESSFULLY_REMOVED
                                .asComponent(
                                        skinName,
                                        playerInfo.getDefaultName(),
                                        text(playerInfo.getNickname())
                                )
                        )
                ));
            } catch (final Throwable ignored) {
                MSLogger.severe(
                        player,
                        ERROR_SOMETHING_WENT_WRONG.asTranslatable()
                );
            }

            this.open(player);
        });
        this.deleteButtonEmpty = deleteButton.clone().item(deleteEmpty);

        return CustomInventory
                .single(TITLE, 3)
                .buttonAt(
                        22,
                        new InventoryButton()
                        .clickAction((event, inv) -> {
                            final Player player = (Player) event.getWhoClicked();

                            player.closeInventory();
                            InventoryButton.playClickSound(player);
                        })
                );
    }

    @Override
    public void open(final @NotNull Player player) {
        final CustomInventory customInventory = this.getCustomInventory().clone();
        final var skins = PlayerInfo.fromOnlinePlayer(this.getPlugin(), player).getPlayerFile().getSkins();
        final var skinButtons = new Int2ObjectOpenHashMap<InventoryButton>();

        for (int i = 0; i < skins.size(); ++i) {
            final Skin skin = skins.get(i);
            final ItemStack head = skin.getHead();
            final InventoryButton inventoryButton = new InventoryButton();
            final int finalI = i;

            skinButtons.put(
                    i,
                    inventoryButton
                    .item(head)
                    .clickAction((event, inv) -> {
                        final boolean havePrevious = !inv.args().isEmpty();
                        final boolean isSame = havePrevious && ((int) inv.args().get(0)) == finalI;
                        final ItemMeta headItemMeta = head.getItemMeta();

                        headItemMeta.setCustomModelData(isSame ? null : 1);
                        head.setItemMeta(headItemMeta);

                        if (havePrevious) {
                            final int previous = (int) inv.args().get(0);
                            final InventoryButton previousButton = skinButtons.get(previous);

                            if (previousButton != null) {
                                final ItemStack previousHead = previousButton.item();
                                final ItemMeta previousMeta = previousHead.getItemMeta();

                                previousMeta.setCustomModelData(null);
                                previousHead.setItemMeta(previousMeta);
                                inv.buttonAt(previous, previousButton);
                            }
                        }

                        if (!isSame) {
                            inv
                            .buttonAt(finalI, inventoryButton.item(head))
                            .buttonAt(18, applyButton)
                            .buttonAt(19, applyButtonEmpty)
                            .buttonAt(20, applyButtonEmpty)
                            .buttonAt(21, applyButtonEmpty)
                            .buttonAt(23, deleteButton)
                            .buttonAt(24, deleteButtonEmpty)
                            .buttonAt(25, deleteButtonEmpty)
                            .buttonAt(26, deleteButtonEmpty)
                            .args(Collections.singletonList(finalI));
                        } else {
                            inv
                            .buttonAt(18, EMPTY_BUTTON)
                            .buttonAt(19, EMPTY_BUTTON)
                            .buttonAt(20, EMPTY_BUTTON)
                            .buttonAt(21, EMPTY_BUTTON)
                            .buttonAt(23, EMPTY_BUTTON)
                            .buttonAt(24, EMPTY_BUTTON)
                            .buttonAt(25, EMPTY_BUTTON)
                            .buttonAt(26, EMPTY_BUTTON)
                            .args(Collections.emptyList());
                        }

                        player.openInventory(inv);
                        InventoryButton.playClickSound(player);
                    })
            );
        }

        customInventory.buttons(skinButtons).open(player);
    }
}
