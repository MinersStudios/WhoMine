package com.github.minersstudios.msessentials.menu;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.InventoryButton;
import com.github.minersstudios.mscore.inventory.actions.ButtonClickAction;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.skin.Skin;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslationComponent;
import static com.github.minersstudios.mscore.inventory.InventoryButton.playClickSound;

public class SkinsMenu {
    private static final Component TITLE = Component.translatable("ms.menu.skins.title", ChatUtils.DEFAULT_STYLE);
    private static final InventoryButton EMPTY_BUTTON = InventoryButton.create().item(new ItemStack(Material.AIR));

    public static @NotNull CustomInventory create() {
        ItemStack quitItem = new ItemStack(Material.PAPER);
        ItemMeta quitMeta = quitItem.getItemMeta();
        Component quitName = renderTranslationComponent("ms.menu.skins.button.quit").style(ChatUtils.DEFAULT_STYLE);

        quitMeta.displayName(quitName);
        quitMeta.setCustomModelData(1);
        quitItem.setItemMeta(quitMeta);

        return CustomInventory.create(TITLE, 3)
                .buttonAt(
                        22,
                        InventoryButton.create()
                        .item(quitItem)
                        .clickAction((event, inv) -> {
                            Player player = (Player) event.getWhoClicked();
                            player.closeInventory();
                            playClickSound(player);
                        })
                );
    }

    public static boolean open(@NotNull Player player) {
        CustomInventory a = MSCore.getCache().customInventoryMap.get("skins");

        if (a == null) return false;

        CustomInventory customInventory = a.clone();

        ItemStack applyItem = new ItemStack(Material.PAPER);
        ItemStack applyNoCMD = new ItemStack(Material.PAPER);
        ItemMeta applyMeta = applyItem.getItemMeta();
        ItemMeta applyMetaNoCMD = applyNoCMD.getItemMeta();
        Component applyName = renderTranslationComponent("ms.menu.skins.button.apply").style(ChatUtils.DEFAULT_STYLE);

        applyMetaNoCMD.displayName(applyName);
        applyMeta.displayName(applyName);
        applyMeta.setCustomModelData(5004);
        applyMetaNoCMD.setCustomModelData(1);
        applyNoCMD.setItemMeta(applyMetaNoCMD);
        applyItem.setItemMeta(applyMeta);

        ButtonClickAction applyClickAction = (event, inv) -> {
            PlayerInfo playerInfo = PlayerInfo.fromMap(player);
            Skin skin = playerInfo.getPlayerFile().getSkin((int) inv.args().get(0));

            if (skin == null) return;

            playerInfo.setSkin(skin);
            player.closeInventory();
        };
        InventoryButton applySkinButton = InventoryButton.create().item(applyItem).clickAction(applyClickAction);
        InventoryButton applySkinButtonNoCMD = InventoryButton.create().item(applyNoCMD).clickAction(applyClickAction);

        ItemStack deleteItem = new ItemStack(Material.PAPER);
        ItemStack deleteNoCMD = new ItemStack(Material.PAPER);
        ItemMeta deleteMeta = deleteItem.getItemMeta();
        ItemMeta deleteMetaNoCMD = deleteNoCMD.getItemMeta();
        Component deleteName = renderTranslationComponent("ms.menu.skins.button.delete").style(ChatUtils.DEFAULT_STYLE);

        deleteMetaNoCMD.displayName(deleteName);
        deleteMeta.displayName(deleteName);
        deleteMeta.setCustomModelData(5005);
        deleteMetaNoCMD.setCustomModelData(1);
        deleteNoCMD.setItemMeta(deleteMetaNoCMD);
        deleteItem.setItemMeta(deleteMeta);

        ButtonClickAction deleteClickAction = (event, inv) -> {
            PlayerInfo playerInfo = PlayerInfo.fromMap(player);
            int selectedPartIndex = (int) inv.args().get(0);

            playerInfo.getPlayerFile().removeSkin(selectedPartIndex);
            open(player);
        };
        InventoryButton deleteSkinButton = InventoryButton.create().item(deleteItem).clickAction(deleteClickAction);
        InventoryButton deleteSkinButtonNoCMD = InventoryButton.create().item(deleteNoCMD).clickAction(deleteClickAction);

        PlayerInfo playerInfo = PlayerInfo.fromMap(player);
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
                        ItemMeta meta = head.getItemMeta();
                        meta.setCustomModelData(isSame ? null : 1);
                        head.setItemMeta(meta);

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
                            .buttonAt(18, applySkinButton)
                            .buttonAt(19, applySkinButtonNoCMD)
                            .buttonAt(20, applySkinButtonNoCMD)
                            .buttonAt(21, applySkinButtonNoCMD)
                            .buttonAt(23, deleteSkinButton)
                            .buttonAt(24, deleteSkinButtonNoCMD)
                            .buttonAt(25, deleteSkinButtonNoCMD)
                            .buttonAt(26, deleteSkinButtonNoCMD)
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
                        playClickSound(player);
                    })
            );
        }

        player.openInventory(customInventory.buttons(skinButtons));
        return true;
    }
}
