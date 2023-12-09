package com.minersstudios.msessentials.menu;

import com.minersstudios.mscore.inventory.CustomInventory;
import com.minersstudios.mscore.inventory.InventoryButton;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.minersstudios.msessentials.player.PlayerInfo;
import com.minersstudios.msessentials.player.Pronouns;
import com.minersstudios.msessentials.player.RegistrationProcess;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.translatable;

public final class PronounsMenu {
    private static final CustomInventory INVENTORY;

    static {
        final ItemStack heItem = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        final ItemMeta heMeta = heItem.getItemMeta();
        final var loreHe = new ArrayList<Component>();

        heMeta.displayName(LanguageFile.renderTranslationComponent("ms.menu.pronouns.button.he.title").style(ChatUtils.DEFAULT_STYLE));
        loreHe.add(LanguageFile.renderTranslationComponent("ms.menu.pronouns.button.he.lore").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY));
        heMeta.lore(loreHe);
        heItem.setItemMeta(heMeta);

        final ItemStack sheItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        final ItemMeta sheMeta = sheItem.getItemMeta();
        final var loreShe = new ArrayList<Component>();

        sheMeta.displayName(LanguageFile.renderTranslationComponent("ms.menu.pronouns.button.she.title").style(ChatUtils.DEFAULT_STYLE));
        loreShe.add(LanguageFile.renderTranslationComponent("ms.menu.pronouns.button.she.lore").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY));
        sheMeta.lore(loreShe);
        sheItem.setItemMeta(sheMeta);

        final ItemStack theyItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        final ItemMeta theyMeta = theyItem.getItemMeta();
        final var loreThey = new ArrayList<Component>();

        theyMeta.displayName(LanguageFile.renderTranslationComponent("ms.menu.pronouns.button.they.title").style(ChatUtils.DEFAULT_STYLE));
        loreThey.add(LanguageFile.renderTranslationComponent("ms.menu.pronouns.button.they.lore").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY));
        theyMeta.lore(loreThey);
        theyItem.setItemMeta(theyMeta);

        final InventoryButton heButton = new InventoryButton(heItem, (event, inventory) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
            final PlayerFile playerFile = playerInfo.getPlayerFile();

            playerFile.setPronouns(Pronouns.HE);
            playerFile.save();
            InventoryButton.playClickSound(player);
            player.closeInventory();
        });

        final InventoryButton sheButton = new InventoryButton(sheItem, (event, inventory) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
            final PlayerFile playerFile = playerInfo.getPlayerFile();

            playerFile.setPronouns(Pronouns.SHE);
            playerFile.save();
            InventoryButton.playClickSound(player);
            player.closeInventory();
        });

        final InventoryButton theyButton = new InventoryButton(theyItem, (event, inventory) -> {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
            final PlayerFile playerFile = playerInfo.getPlayerFile();

            playerFile.setPronouns(Pronouns.THEY);
            playerFile.save();
            InventoryButton.playClickSound(player);
            player.closeInventory();
        });

        INVENTORY = CustomInventory.single(translatable("ms.menu.pronouns.title", ChatUtils.DEFAULT_STYLE), 1)
                .buttonAt(0, heButton)
                .buttonAt(1, heButton)
                .buttonAt(2, heButton)
                .buttonAt(3, sheButton)
                .buttonAt(4, sheButton)
                .buttonAt(5, sheButton)
                .buttonAt(6, theyButton)
                .buttonAt(7, theyButton)
                .buttonAt(8, theyButton)
                .closeAction((event, inventory) -> {
                    final Player player = (Player) event.getPlayer();
                    final PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

                    if (playerInfo.getPlayerFile().getConfig().getString("pronouns") == null) {
                        MSEssentials.singleton().runTask(() -> player.openInventory(inventory));
                    } else {
                        new RegistrationProcess().setPronouns(player, playerInfo);
                    }
                });
    }

    public static void open(final @NotNull Player player) {
        INVENTORY.open(player);
    }
}
