package com.github.minersstudios.msessentials.menu;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.InventoryButton;
import com.github.minersstudios.mscore.inventory.SingleInventory;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.MSEssentials;
import com.github.minersstudios.msessentials.player.PlayerFile;
import com.github.minersstudios.msessentials.player.PlayerInfo;
import com.github.minersstudios.msessentials.player.Pronouns;
import com.github.minersstudios.msessentials.player.RegistrationProcess;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslationComponent;
import static com.github.minersstudios.mscore.inventory.InventoryButton.playClickSound;

public class PronounsMenu {
    private static final Component TITLE = Component.translatable("ms.menu.pronouns.title", ChatUtils.DEFAULT_STYLE);
    private static final CustomInventory INVENTORY;

    static {
        ItemStack he = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta heMeta = he.getItemMeta();
        heMeta.displayName(renderTranslationComponent("ms.menu.pronouns.button.he.title").style(ChatUtils.DEFAULT_STYLE));
        ArrayList<Component> loreHe = new ArrayList<>();
        loreHe.add(renderTranslationComponent("ms.menu.pronouns.button.he.lore").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY));
        heMeta.lore(loreHe);
        he.setItemMeta(heMeta);

        ItemStack she = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta sheMeta = she.getItemMeta();
        sheMeta.displayName(renderTranslationComponent("ms.menu.pronouns.button.she.title").style(ChatUtils.DEFAULT_STYLE));
        ArrayList<Component> loreShe = new ArrayList<>();
        loreShe.add(renderTranslationComponent("ms.menu.pronouns.button.she.lore").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY));
        sheMeta.lore(loreShe);
        she.setItemMeta(sheMeta);

        ItemStack they = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta theyMeta = they.getItemMeta();
        theyMeta.displayName(renderTranslationComponent("ms.menu.pronouns.button.they.title").style(ChatUtils.DEFAULT_STYLE));
        ArrayList<Component> loreThey = new ArrayList<>();
        loreThey.add(renderTranslationComponent("ms.menu.pronouns.button.they.lore").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY));
        theyMeta.lore(loreThey);
        they.setItemMeta(theyMeta);

        InventoryButton heButton = InventoryButton.create()
                .item(he)
                .clickAction((event, inventory) -> {
                    Player player = (Player) event.getWhoClicked();
                    PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
                    PlayerFile playerFile = playerInfo.getPlayerFile();

                    playerFile.setPronouns(Pronouns.HE);
                    playerFile.save();
                    playClickSound(player);
                    player.closeInventory();
                });

        InventoryButton sheButton = InventoryButton.create()
                .item(she)
                .clickAction((event, inventory) -> {
                    Player player = (Player) event.getWhoClicked();
                    PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
                    PlayerFile playerFile = playerInfo.getPlayerFile();

                    playerFile.setPronouns(Pronouns.SHE);
                    playerFile.save();
                    playClickSound(player);
                    player.closeInventory();
                });

        InventoryButton theyButton = InventoryButton.create()
                .item(they)
                .clickAction((event, inventory) -> {
                    Player player = (Player) event.getWhoClicked();
                    PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);
                    PlayerFile playerFile = playerInfo.getPlayerFile();

                    playerFile.setPronouns(Pronouns.THEY);
                    playerFile.save();
                    playClickSound(player);
                    player.closeInventory();
                });

        INVENTORY = SingleInventory.single(TITLE, 1)
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
                    Player player = (Player) event.getPlayer();
                    PlayerInfo playerInfo = PlayerInfo.fromOnlinePlayer(player);

                    if (playerInfo.getPlayerFile().getConfig().getString("pronouns") == null) {
                        MSEssentials.getInstance().runTask(() -> player.openInventory(inventory));
                    } else {
                        new RegistrationProcess().setPronouns(player, playerInfo);
                    }
                });
    }

    public static void open(@NotNull Player player) {
        INVENTORY.open(player);
    }
}
