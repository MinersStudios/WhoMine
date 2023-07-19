package com.minersstudios.msessentials.menu;

import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.inventory.InventoryButton;
import com.minersstudios.mscore.inventory.SingleInventory;
import com.minersstudios.mscore.inventory.actions.ButtonClickAction;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.utils.ChatUtils;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.skin.Skin;
import com.minersstudios.msessentials.utils.MessageUtils;
import com.minersstudios.mscore.config.LanguageFile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class SkinsMenu {
    private static final Component TITLE = Component.translatable("ms.menu.skins.title", ChatUtils.DEFAULT_STYLE);
    private static final InventoryButton EMPTY_BUTTON = InventoryButton.create().item(new ItemStack(Material.AIR));
    private static final InventoryButton APPLY_BUTTON;
    private static final InventoryButton APPLY_BUTTON_NO_CMD;
    private static final InventoryButton DELETE_BUTTON;
    private static final InventoryButton DELETE_BUTTON_NO_CMD;
    private static final CustomInventory CUSTOM_INVENTORY;

    static {
        ItemStack applyItem = new ItemStack(Material.PAPER);
        ItemStack applyNoCMD = new ItemStack(Material.PAPER);
        ItemMeta applyMeta = applyItem.getItemMeta();
        ItemMeta applyMetaNoCMD = applyNoCMD.getItemMeta();
        Component applyName = LanguageFile.renderTranslationComponent("ms.menu.skins.button.apply").style(ChatUtils.DEFAULT_STYLE);

        applyMetaNoCMD.displayName(applyName);
        applyMeta.displayName(applyName);
        applyMeta.setCustomModelData(5004);
        applyMetaNoCMD.setCustomModelData(1);
        applyNoCMD.setItemMeta(applyMetaNoCMD);
        applyItem.setItemMeta(applyMeta);

        ButtonClickAction applyClickAction = (event, inv) -> {
            Player player = (Player) event.getWhoClicked();
            PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
            Skin skin = playerInfo.getPlayerFile().getSkin((int) inv.args().get(0));

            if (skin == null) return;

            playerInfo.setSkin(skin);
            player.closeInventory();
        };
        APPLY_BUTTON = InventoryButton.create().item(applyItem).clickAction(applyClickAction);
        APPLY_BUTTON_NO_CMD = InventoryButton.create().item(applyNoCMD).clickAction(applyClickAction);

        ItemStack deleteItem = new ItemStack(Material.PAPER);
        ItemStack deleteNoCMD = new ItemStack(Material.PAPER);
        ItemMeta deleteMeta = deleteItem.getItemMeta();
        ItemMeta deleteMetaNoCMD = deleteNoCMD.getItemMeta();
        Component deleteName = LanguageFile.renderTranslationComponent("ms.menu.skins.button.delete").style(ChatUtils.DEFAULT_STYLE);

        deleteMetaNoCMD.displayName(deleteName);
        deleteMeta.displayName(deleteName);
        deleteMeta.setCustomModelData(5005);
        deleteMetaNoCMD.setCustomModelData(1);
        deleteNoCMD.setItemMeta(deleteMetaNoCMD);
        deleteItem.setItemMeta(deleteMeta);

        ButtonClickAction deleteClickAction = (event, inv) -> {
            Player player = (Player) event.getWhoClicked();
            PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

            try {
                Skin selectedSkin = playerInfo.getPlayerFile().getSkin((int) inv.args().get(0));
                if (selectedSkin == null) return;
                Component skinName = text(selectedSkin.getName());

                playerInfo.getPlayerFile().removeSkin(selectedSkin);
                MSLogger.fine(
                        player,
                        translatable(
                                "ms.discord.skin.successfully_removed.minecraft",
                                skinName
                        )
                );
                playerInfo.sendPrivateDiscordMessage(MessageUtils.craftEmbed(
                        LanguageFile.renderTranslation(
                                translatable(
                                        "ms.discord.skin.successfully_removed",
                                        skinName,
                                        playerInfo.getDefaultName(),
                                        text(playerInfo.getNickname())
                                )
                        )
                ));
            } catch (Exception e) {
                MSLogger.severe(player, Component.translatable("ms.error.something_went_wrong"));
            }

            open(player);
        };
        DELETE_BUTTON = InventoryButton.create().item(deleteItem).clickAction(deleteClickAction);
        DELETE_BUTTON_NO_CMD = InventoryButton.create().item(deleteNoCMD).clickAction(deleteClickAction);

        CUSTOM_INVENTORY = SingleInventory.single(TITLE, 3)
                .buttonAt(
                        22,
                        InventoryButton.create()
                        .clickAction((event, inv) -> {
                            Player player = (Player) event.getWhoClicked();
                            player.closeInventory();
                            InventoryButton.playClickSound(player);
                        })
                );
    }

    public static void open(@NotNull Player player) {
        CustomInventory customInventory = CUSTOM_INVENTORY.clone();

        PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
        var skins = playerInfo.getPlayerFile().getSkins();
        var skinButtons = new HashMap<Integer, InventoryButton>();

        for (int i = 0; i < skins.size(); i++) {
            Skin skin = skins.get(i);
            ItemStack head = skin.getHead();
            InventoryButton inventoryButton = InventoryButton.create();
            int finalI = i;

            skinButtons.put(
                    i,
                    inventoryButton
                    .item(head)
                    .clickAction((event, inv) -> {
                        boolean havePrevious = !inv.args().isEmpty();
                        boolean isSame = havePrevious && ((int) inv.args().get(0)) == finalI;
                        ItemMeta headItemMeta = head.getItemMeta();

                        headItemMeta.setCustomModelData(isSame ? null : 1);
                        head.setItemMeta(headItemMeta);

                        if (havePrevious) {
                            int previous = (int) inv.args().get(0);
                            InventoryButton previousButton = skinButtons.get(previous);

                            if (previousButton != null) {
                                ItemStack previousHead = previousButton.item();
                                assert previousHead != null;
                                ItemMeta previousMeta = previousHead.getItemMeta();
                                previousMeta.setCustomModelData(null);
                                previousHead.setItemMeta(previousMeta);
                                inv.buttonAt(previous, previousButton);
                            }
                        }

                        if (!isSame) {
                            inv
                            .buttonAt(finalI, inventoryButton.item(head))
                            .buttonAt(18, APPLY_BUTTON)
                            .buttonAt(19, APPLY_BUTTON_NO_CMD)
                            .buttonAt(20, APPLY_BUTTON_NO_CMD)
                            .buttonAt(21, APPLY_BUTTON_NO_CMD)
                            .buttonAt(23, DELETE_BUTTON)
                            .buttonAt(24, DELETE_BUTTON_NO_CMD)
                            .buttonAt(25, DELETE_BUTTON_NO_CMD)
                            .buttonAt(26, DELETE_BUTTON_NO_CMD)
                            .args(List.of(finalI));
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
                            .args(new ArrayList<>());
                        }

                        player.openInventory(inv);
                        InventoryButton.playClickSound(player);
                    })
            );
        }

        customInventory.buttons(skinButtons).open(player);
    }
}
