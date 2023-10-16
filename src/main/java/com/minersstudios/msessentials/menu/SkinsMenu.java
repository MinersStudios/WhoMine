package com.minersstudios.msessentials.menu;

import com.google.common.collect.ImmutableList;
import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.inventory.InventoryButton;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.discord.BotHandler;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class SkinsMenu {
    private static final Component TITLE = translatable("ms.menu.skins.title", ChatUtils.DEFAULT_STYLE);
    private static final InventoryButton EMPTY_BUTTON = new InventoryButton().item(new ItemStack(Material.AIR));
    private static final InventoryButton APPLY_BUTTON;
    private static final InventoryButton APPLY_BUTTON_EMPTY;
    private static final InventoryButton DELETE_BUTTON;
    private static final InventoryButton DELETE_BUTTON_EMPTY;
    private static final CustomInventory CUSTOM_INVENTORY;

    static {
        final ItemStack applyItem = new ItemStack(Material.PAPER);
        final ItemMeta applyMeta = applyItem.getItemMeta();

        final ItemStack applyEmpty = applyItem.clone();
        final ItemMeta applyEmptyMeta = applyEmpty.getItemMeta();

        final Component applyName = LanguageFile.renderTranslationComponent("ms.menu.skins.button.apply").style(ChatUtils.DEFAULT_STYLE);

        applyMeta.displayName(applyName);
        applyMeta.setCustomModelData(5004);
        applyItem.setItemMeta(applyMeta);

        applyEmptyMeta.displayName(applyName);
        applyEmptyMeta.setCustomModelData(1);
        applyEmpty.setItemMeta(applyEmptyMeta);

        APPLY_BUTTON = new InventoryButton(applyItem, (event, inv) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
            final Skin skin = playerInfo.getPlayerFile().getSkin((int) inv.args().get(0));

            if (skin == null) return;

            playerInfo.setSkin(skin);
            player.closeInventory();
        });
        APPLY_BUTTON_EMPTY = APPLY_BUTTON.clone().item(applyEmpty);

        final ItemStack deleteItem = new ItemStack(Material.PAPER);
        final ItemMeta deleteMeta = deleteItem.getItemMeta();

        final ItemStack deleteEmpty = deleteItem.clone();
        final ItemMeta deleteEmptyMeta = deleteEmpty.getItemMeta();

        final Component deleteName = LanguageFile.renderTranslationComponent("ms.menu.skins.button.delete").style(ChatUtils.DEFAULT_STYLE);

        deleteMeta.displayName(deleteName);
        deleteMeta.setCustomModelData(5005);
        deleteItem.setItemMeta(deleteMeta);

        deleteEmptyMeta.displayName(deleteName);
        deleteEmptyMeta.setCustomModelData(1);
        deleteEmpty.setItemMeta(deleteEmptyMeta);

        DELETE_BUTTON = new InventoryButton(deleteItem, (event, inv) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
            final PlayerFile playerFile = playerInfo.getPlayerFile();

            try {
                final Skin selectedSkin = playerFile.getSkin((int) inv.args().get(0));
                if (selectedSkin == null) return;
                final Component skinName = text(selectedSkin.getName());

                playerFile.removeSkin(selectedSkin);
                MSLogger.fine(
                        player,
                        translatable(
                                "ms.discord.skin.successfully_removed.minecraft",
                                skinName
                        )
                );
                playerInfo.sendPrivateDiscordMessage(BotHandler.craftEmbed(
                        LanguageFile.renderTranslation(
                                translatable(
                                        "ms.discord.skin.successfully_removed",
                                        skinName,
                                        playerInfo.getDefaultName(),
                                        text(playerInfo.getNickname())
                                )
                        )
                ));
            } catch (final Exception ignored) {
                MSLogger.severe(player, translatable("ms.error.something_went_wrong"));
            }

            open(player);
        });
        DELETE_BUTTON_EMPTY = DELETE_BUTTON.clone().item(deleteEmpty);

        CUSTOM_INVENTORY = CustomInventory.single(TITLE, 3).buttonAt(
                22,
                new InventoryButton()
                .clickAction((event, inv) -> {
                    final Player player = (Player) event.getWhoClicked();

                    player.closeInventory();
                    InventoryButton.playClickSound(player);
                })
        );
    }

    public static void open(final @NotNull Player player) {
        final CustomInventory customInventory = CUSTOM_INVENTORY.clone();

        final var skins = PlayerInfo.fromOnlinePlayer(player).getPlayerFile().getSkins();
        final var skinButtons = new HashMap<Integer, InventoryButton>();

        for (int i = 0; i < skins.size(); i++) {
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
                            .buttonAt(18, APPLY_BUTTON)
                            .buttonAt(19, APPLY_BUTTON_EMPTY)
                            .buttonAt(20, APPLY_BUTTON_EMPTY)
                            .buttonAt(21, APPLY_BUTTON_EMPTY)
                            .buttonAt(23, DELETE_BUTTON)
                            .buttonAt(24, DELETE_BUTTON_EMPTY)
                            .buttonAt(25, DELETE_BUTTON_EMPTY)
                            .buttonAt(26, DELETE_BUTTON_EMPTY)
                            .args(ImmutableList.of(finalI));
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
