package com.minersstudios.msitem.menu;

import com.minersstudios.mscore.inventory.*;
import com.minersstudios.mscore.inventory.actions.ButtonClickAction;
import com.minersstudios.mscore.plugin.config.LanguageFile;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.item.renameable.RenameCollection;
import com.minersstudios.msitem.item.renameable.RenameableItem;
import com.minersstudios.msitem.item.renameable.RenameableItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static com.minersstudios.mscore.inventory.InventoryButton.playClickSound;
import static net.kyori.adventure.text.Component.translatable;

public class RenamesMenu {
    private static final int RENAMEABLE_ITEM_SLOT = 2;
    private static final int RENAMED_ITEM_SLOT = 6;
    private static final int QUIT_RENAME_BUTTON_SLOT = 40;
    private static final int QUIT_RENAMES_BUTTON_SLOT = 40;
    private static final int CURRENT_RENAMEABLE_ITEM_SLOT = 20;
    private static final int CURRENT_RENAMED_ITEM_SLOT = 24;
    private static final int RED_CROSS_SLOT = 22;

    private static final Component RENAMES_TITLE = translatable("ms.menu.renames.title", ChatUtils.DEFAULT_STYLE);
    private static final Component RENAME_TITLE = translatable("ms.menu.rename.title", ChatUtils.DEFAULT_STYLE);

    private static final ItemStack RED_CROSS_ITEM;

    private static final ElementPagedInventory INVENTORY;

    static {
        Component previousButtonComponent = LanguageFile.renderTranslationComponent("ms.menu.renames.button.previous_page").style(ChatUtils.DEFAULT_STYLE);
        Component nextButtonComponent = LanguageFile.renderTranslationComponent("ms.menu.renames.button.next_page").style(ChatUtils.DEFAULT_STYLE);
        Component redCrossComponent = LanguageFile.renderTranslationComponent("ms.menu.rename.no_exp").style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY);

        ItemStack previousPageItem = new ItemStack(Material.PAPER);
        ItemStack previousPageNoCMDItem = new ItemStack(Material.PAPER);
        ItemStack nextPageItem = new ItemStack(Material.PAPER);
        ItemStack nextPageNoCMDItem = new ItemStack(Material.PAPER);
        RED_CROSS_ITEM = new ItemStack(Material.PAPER);

        ItemMeta previousPageMeta = previousPageItem.getItemMeta();
        ItemMeta previousPageMetaNoCMD = previousPageNoCMDItem.getItemMeta();
        ItemMeta nextPageMeta = nextPageItem.getItemMeta();
        ItemMeta nextPageMetaNoCMD = nextPageNoCMDItem.getItemMeta();
        ItemMeta redCrossMeta = RED_CROSS_ITEM.getItemMeta();

        previousPageMetaNoCMD.displayName(previousButtonComponent);
        previousPageMeta.displayName(previousButtonComponent);
        previousPageMeta.setCustomModelData(5001);
        previousPageMetaNoCMD.setCustomModelData(1);
        previousPageNoCMDItem.setItemMeta(previousPageMetaNoCMD);
        previousPageItem.setItemMeta(previousPageMeta);

        nextPageMetaNoCMD.displayName(nextButtonComponent);
        nextPageMeta.displayName(nextButtonComponent);
        nextPageMeta.setCustomModelData(5002);
        nextPageMetaNoCMD.setCustomModelData(1);
        nextPageNoCMDItem.setItemMeta(nextPageMetaNoCMD);
        nextPageItem.setItemMeta(nextPageMeta);

        redCrossMeta.displayName(redCrossComponent);
        redCrossMeta.setCustomModelData(5003);
        RED_CROSS_ITEM.setItemMeta(redCrossMeta);

        ButtonClickAction previousClick = (event, customInventory) -> {
            if (!(customInventory instanceof PagedCustomInventory paged)) return;

            Player player = (Player) event.getWhoClicked();
            CustomInventory previousPage = paged.getPage(paged.getPreviousPageIndex());

            if (previousPage != null) {
                player.openInventory(previousPage);
                playClickSound(player);
            }
        };
        ButtonClickAction nextClick = (event, customInventory) -> {
            if (!(customInventory instanceof PagedCustomInventory paged)) return;

            Player player = (Player) event.getWhoClicked();
            CustomInventory nextPage = paged.getPage(paged.getNextPageIndex());

            if (nextPage != null) {
                player.openInventory(nextPage);
                playClickSound(player);
            }
        };

        InventoryButton previousPageButton = InventoryButton.create()
                .item(previousPageItem)
                .clickAction(previousClick);
        InventoryButton previousPageButtonNoCMD = InventoryButton.create()
                .item(previousPageNoCMDItem)
                .clickAction(previousClick);
        InventoryButton nextButton = InventoryButton.create()
                .item(nextPageItem)
                .clickAction(nextClick);
        InventoryButton nextButtonNoCMD = InventoryButton.create()
                .item(nextPageNoCMDItem)
                .clickAction(nextClick);
        InventoryButton backButton = InventoryButton.create()
                .clickAction((event, customInventory) -> {
                    Player player = (Player) event.getWhoClicked();
                    player.closeInventory();
                    playClickSound(player);
                });

        INVENTORY = ElementPagedInventory
                .elementPaged(RENAMES_TITLE, 5, IntStream.range(0, 36).toArray())
                .staticButtonAt(
                        36,
                        inventory -> inventory.getPreviousPageIndex() == -1 ? previousPageButtonNoCMD : previousPageButton
                )
                .staticButtonAt(37, i -> previousPageButtonNoCMD)
                .staticButtonAt(38, i -> previousPageButtonNoCMD)
                .staticButtonAt(39, i -> previousPageButtonNoCMD)
                .staticButtonAt(QUIT_RENAMES_BUTTON_SLOT, i -> backButton)
                .staticButtonAt(
                        41,
                        inventory -> inventory.getNextPageIndex() == -1 ? nextButtonNoCMD : nextButton
                )
                .staticButtonAt(42, i -> nextButtonNoCMD)
                .staticButtonAt(43, i -> nextButtonNoCMD)
                .staticButtonAt(44, i -> nextButtonNoCMD)
                .build();
    }

    public static void update() {
        var elements = new ArrayList<InventoryButton>();

        for (var renameableItem : MSItem.getCache().renameableItemsMenu) {
            RenameCollection renameCollection = renameableItem.getRenames();
            ItemStack resultItem = renameCollection.getMainItem();
            var renameableItemStacks = new ArrayList<>(renameCollection.items());
            var renames = new ArrayList<String>();

            for (var rename : renameCollection.renames()) {
                renames.add(ChatUtils.normalize(rename));
            }

            elements.add(InventoryButton.create()
            .item(resultItem)
            .clickAction((buttonEvent, inventory) -> {
                if (buttonEvent.getClick().isCreativeAction()) return;

                MSItem plugin = MSItem.getInstance();
                Player player = (Player) buttonEvent.getWhoClicked();
                SingleInventory renameInventory = SingleInventory.single(RENAME_TITLE, 5);

                renameInventory.setItem(RENAMEABLE_ITEM_SLOT, renameableItemStacks.get(0));
                renameInventory.setItem(RENAMED_ITEM_SLOT, resultItem);
                renameInventory.buttonAt(
                        QUIT_RENAME_BUTTON_SLOT,
                        InventoryButton.create()
                        .clickAction((e, i) -> {
                            player.openInventory(inventory);
                            playClickSound(player);
                        })
                );

                if (renameableItemStacks.size() > 1) {
                    plugin.runTaskTimer(new BukkitRunnable() {
                        int index = 0;

                        @Override
                        public void run() {
                            if (!player.getOpenInventory().getTopInventory().equals(renameInventory)) {
                                this.cancel();
                                return;
                            }

                            ItemStack newRenameable = renameableItemStacks.get(this.index);

                            resultItem.setType(newRenameable.getType());
                            renameInventory.setItem(RENAMEABLE_ITEM_SLOT, newRenameable);
                            renameInventory.setItem(RENAMED_ITEM_SLOT, resultItem);

                            this.index++;

                            if (this.index + 1 > renameableItemStacks.size()) {
                                this.index = 0;
                            }
                        }
                    }, 0L, 25L);
                }

                if (renames.size() > 1) {
                    ItemMeta meta = resultItem.getItemMeta();

                    plugin.runTaskTimer(new BukkitRunnable() {
                        int index = 0;

                        @Override
                        public void run() {
                            if (!player.getOpenInventory().getTopInventory().equals(renameInventory)) {
                                this.cancel();
                                return;
                            }

                            meta.displayName(ChatUtils.createDefaultStyledText(renames.get(this.index)));
                            resultItem.setItemMeta(meta);
                            renameInventory.setItem(RENAMED_ITEM_SLOT, resultItem);
                            this.index++;

                            if (this.index + 1 > renames.size()) {
                                this.index = 0;
                            }
                        }
                    }, 0L, 25L);
                }

                renameInventory.closeAction((e, customInventory) -> {
                    ItemStack itemStack = customInventory.getItem(CURRENT_RENAMEABLE_ITEM_SLOT);

                    if (itemStack != null) {
                        var didntFitItems = player.getInventory().addItem(itemStack);

                        if (!didntFitItems.isEmpty()) {
                            player.getWorld().dropItemNaturally(player.getLocation().add(0.0d, 0.5d, 0.0d), itemStack);
                        }
                    }
                });

                renameInventory.clickAction((event, customInventory) -> {
                    int slot = event.getSlot();
                    ItemStack currentItem = event.getCurrentItem();
                    ItemStack cursorItem = event.getCursor();
                    boolean hasExp = player.getLevel() >= 1 || player.getGameMode() == GameMode.CREATIVE;

                    if (slot == CURRENT_RENAMEABLE_ITEM_SLOT) {
                        ItemStack secondItem = renameInventory.getItem(RENAMED_ITEM_SLOT);
                        assert secondItem != null;
                        Component displayName = secondItem.getItemMeta().displayName();
                        assert displayName != null;
                        String renameText = ChatUtils.serializePlainComponent(displayName);

                        plugin.runTask(() -> createRenamedItem(event.getCurrentItem(), renameInventory, renameText, hasExp));
                        return;
                    } else if (
                            slot == CURRENT_RENAMED_ITEM_SLOT
                            && currentItem != null
                            && renameInventory.getItem(CURRENT_RENAMEABLE_ITEM_SLOT) != null
                            && cursorItem != null
                            && cursorItem.getType().isAir()
                            && hasExp
                    ) {
                        player.setItemOnCursor(currentItem);
                        renameInventory.setItem(CURRENT_RENAMEABLE_ITEM_SLOT, null);
                        renameInventory.setItem(CURRENT_RENAMED_ITEM_SLOT, null);
                        player.giveExpLevels(-1);
                    }

                    event.setCancelled(!event.getClick().isCreativeAction());
                });

                renameInventory.bottomClickAction((event, customInventory) -> {
                    Inventory clickedInventory = event.getClickedInventory();

                    if (
                            !event.getClick().isShiftClick()
                            || clickedInventory == null
                            || clickedInventory.equals(customInventory)
                            || renameInventory.getItem(CURRENT_RENAMEABLE_ITEM_SLOT) != null
                    ) return;

                    renameInventory.setItem(CURRENT_RENAMEABLE_ITEM_SLOT, event.getCurrentItem());
                    clickedInventory.setItem(event.getSlot(), null);

                    player.getServer().getPluginManager().callEvent(
                            new InventoryClickEvent(
                                    event.getView(),
                                    InventoryType.SlotType.CONTAINER,
                                    CURRENT_RENAMEABLE_ITEM_SLOT,
                                    ClickType.LEFT,
                                    InventoryAction.PLACE_ALL
                            )
                    );
                });

                player.openInventory(renameInventory);
                playClickSound(player);
            }));
        }

        INVENTORY.elements(elements);
    }

    public static void open(Player player) {
        INVENTORY.open(player);
    }

    private static void createRenamedItem(
            @Nullable ItemStack itemStack,
            @NotNull Inventory inventory,
            @NotNull String renameText,
            boolean hasExp
    ) {
        RenameableItem renameableItem = RenameableItemRegistry.fromRename(renameText, itemStack).orElse(null);

        if (
                renameableItem != null
                && renameableItem.isWhiteListed((OfflinePlayer) inventory.getViewers().get(0))
        ) {
            inventory.setItem(CURRENT_RENAMED_ITEM_SLOT, renameableItem.craftRenamed(itemStack, renameText));

            if (!hasExp) {
                inventory.setItem(RED_CROSS_SLOT, RED_CROSS_ITEM);
            }

            return;
        }

        inventory.setItem(CURRENT_RENAMED_ITEM_SLOT, null);

        ItemStack redCross = inventory.getItem(RED_CROSS_SLOT);

        if (redCross != null) {
            inventory.setItem(RED_CROSS_SLOT, null);
        }
    }
}
