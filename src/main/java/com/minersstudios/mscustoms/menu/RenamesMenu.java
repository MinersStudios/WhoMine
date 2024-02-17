package com.minersstudios.mscustoms.menu;

import com.minersstudios.mscore.inventory.*;
import com.minersstudios.mscore.locale.Translations;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscustoms.MSCustoms;
import com.minersstudios.mscustoms.custom.item.renameable.RenameCollection;
import com.minersstudios.mscustoms.custom.item.renameable.RenameableItem;
import com.minersstudios.mscustoms.custom.item.renameable.RenameableItemRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

import java.util.stream.IntStream;

import static com.minersstudios.mscore.inventory.InventoryButton.playClickSound;

public final class RenamesMenu {
    private static final int RENAMEABLE_ITEM_SLOT = 2;
    private static final int RENAMED_ITEM_SLOT = 6;
    private static final int QUIT_RENAME_BUTTON_SLOT = 40;
    private static final int QUIT_RENAMES_BUTTON_SLOT = 40;
    private static final int CURRENT_RENAMEABLE_ITEM_SLOT = 20;
    private static final int CURRENT_RENAMED_ITEM_SLOT = 24;
    private static final int RED_CROSS_SLOT = 22;

    private static final ItemStack RED_CROSS_ITEM;

    private static final ElementPagedInventory INVENTORY;

    static {
        final Component previousButtonComponent = Translations.MENU_RENAMES_BUTTON_PREVIOUS_PAGE.asComponent().style(ChatUtils.DEFAULT_STYLE);
        final Component nextButtonComponent = Translations.MENU_RENAMES_BUTTON_NEXT_PAGE.asComponent().style(ChatUtils.DEFAULT_STYLE);
        final Component redCrossComponent = Translations.MENU_RENAME_NO_EXP.asComponent().style(ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY);

        final ItemStack previousPageItem = new ItemStack(Material.PAPER);
        final ItemMeta previousPageMeta = previousPageItem.getItemMeta();

        previousPageMeta.displayName(previousButtonComponent);
        previousPageMeta.setCustomModelData(5001);
        previousPageItem.setItemMeta(previousPageMeta);

        final ItemStack previousPageEmptyItem = previousPageItem.clone();
        final ItemMeta previousPageEmptyMeta = previousPageEmptyItem.getItemMeta();

        previousPageEmptyMeta.displayName(previousButtonComponent);
        previousPageEmptyMeta.setCustomModelData(1);
        previousPageEmptyItem.setItemMeta(previousPageEmptyMeta);

        final ItemStack nextPageItem = previousPageItem.clone();
        final ItemMeta nextPageMeta = nextPageItem.getItemMeta();

        nextPageMeta.displayName(nextButtonComponent);
        nextPageMeta.setCustomModelData(5002);
        nextPageItem.setItemMeta(nextPageMeta);

        final ItemStack nextPageEmptyItem = previousPageItem.clone();
        final ItemMeta nextPageEmptyMeta = nextPageEmptyItem.getItemMeta();

        nextPageEmptyMeta.displayName(nextButtonComponent);
        nextPageEmptyMeta.setCustomModelData(1);
        nextPageEmptyItem.setItemMeta(nextPageEmptyMeta);

        RED_CROSS_ITEM = previousPageItem.clone();
        final ItemMeta redCrossMeta = RED_CROSS_ITEM.getItemMeta();

        redCrossMeta.displayName(redCrossComponent);
        redCrossMeta.setCustomModelData(5003);
        RED_CROSS_ITEM.setItemMeta(redCrossMeta);

        final InventoryButton previousPageButton = new InventoryButton(previousPageItem, (event, customInventory) -> {
            final PagedCustomInventory paged = (PagedCustomInventory) customInventory;
            final Player player = (Player) event.getWhoClicked();
            final CustomInventory previousPage = paged.getPage(paged.getPreviousPageIndex());

            if (previousPage != null) {
                player.openInventory(previousPage);
                playClickSound(player);
            }
        });
        final InventoryButton previousPageButtonEmpty = previousPageButton.clone().item(previousPageEmptyItem);

        final InventoryButton nextButton = new InventoryButton(nextPageItem, (event, customInventory) -> {
            final PagedCustomInventory paged = (PagedCustomInventory) customInventory;
            final Player player = (Player) event.getWhoClicked();
            final CustomInventory nextPage = paged.getPage(paged.getNextPageIndex());

            if (nextPage != null) {
                player.openInventory(nextPage);
                playClickSound(player);
            }
        });
        final InventoryButton nextButtonEmpty = nextButton.clone().item(nextPageEmptyItem);

        final InventoryButton backButton = new InventoryButton()
                .clickAction((event, customInventory) -> {
                    final Player player = (Player) event.getWhoClicked();

                    player.closeInventory();
                    playClickSound(player);
                });

        INVENTORY = CustomInventory
                .elementPaged(
                        Translations.MENU_RENAMES_TITLE.asTranslatable().style(ChatUtils.DEFAULT_STYLE),
                        5,
                        IntStream.range(0, 36).toArray()
                )
                .staticButtonAt(
                        36,
                        inventory -> inventory.getPreviousPageIndex() == -1 ? previousPageButtonEmpty : previousPageButton
                )
                .staticButtonAt(37, i -> previousPageButtonEmpty)
                .staticButtonAt(38, i -> previousPageButtonEmpty)
                .staticButtonAt(39, i -> previousPageButtonEmpty)
                .staticButtonAt(QUIT_RENAMES_BUTTON_SLOT, i -> backButton)
                .staticButtonAt(
                        41,
                        inventory -> inventory.getNextPageIndex() == -1 ? nextButtonEmpty : nextButton
                )
                .staticButtonAt(42, i -> nextButtonEmpty)
                .staticButtonAt(43, i -> nextButtonEmpty)
                .staticButtonAt(44, i -> nextButtonEmpty)
                .build();
    }

    public static void update(final @NotNull MSCustoms plugin) {
        final var elements = new ObjectArrayList<InventoryButton>();

        for (final var renameableItem : plugin.getCache().getRenameableMenuItems()) {
            final RenameCollection renameCollection = renameableItem.getRenames();
            final ItemStack resultItem = renameCollection.getMainItem();
            final var renameableItemStacks = new ObjectArrayList<>(renameCollection.items());
            final var renames = new ObjectArrayList<String>();

            assert resultItem != null;

            for (final var rename : renameCollection.renames()) {
                renames.add(ChatUtils.normalize(rename));
            }

            elements.add(new InventoryButton()
            .item(resultItem)
            .clickAction((buttonEvent, inventory) -> {
                if (buttonEvent.getClick().isCreativeAction()) {
                    return;
                }

                final Player player = (Player) buttonEvent.getWhoClicked();
                final SingleInventory renameInventory =
                        CustomInventory.single(
                                Translations.MENU_RENAME_TITLE.asTranslatable().style(ChatUtils.DEFAULT_STYLE),
                                5
                        );

                renameInventory.setItem(RENAMEABLE_ITEM_SLOT, renameableItemStacks.get(0));
                renameInventory.setItem(RENAMED_ITEM_SLOT, resultItem);
                renameInventory.buttonAt(
                        QUIT_RENAME_BUTTON_SLOT,
                        new InventoryButton()
                        .clickAction((e, i) -> {
                            player.openInventory(inventory);
                            playClickSound(player);
                        })
                );

                if (renameableItemStacks.size() > 1) {
                    new BukkitRunnable() {
                        int index = 0;

                        @Override
                        public void run() {
                            if (!player.getOpenInventory().getTopInventory().equals(renameInventory)) {
                                this.cancel();
                                return;
                            }

                            final ItemStack newRenameable = renameableItemStacks.get(this.index);

                            resultItem.setType(newRenameable.getType());
                            renameInventory.setItem(RENAMEABLE_ITEM_SLOT, newRenameable);
                            renameInventory.setItem(RENAMED_ITEM_SLOT, resultItem);

                            this.index++;

                            if (this.index + 1 > renameableItemStacks.size()) {
                                this.index = 0;
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 25L);
                }

                if (renames.size() > 1) {
                    final ItemMeta meta = resultItem.getItemMeta();

                    new BukkitRunnable() {
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
                    }.runTaskTimer(plugin, 0L, 25L);
                }

                renameInventory.closeAction((e, customInventory) -> {
                    final ItemStack itemStack = customInventory.getItem(CURRENT_RENAMEABLE_ITEM_SLOT);

                    if (itemStack != null) {
                        final var didntFitItems = player.getInventory().addItem(itemStack);

                        if (!didntFitItems.isEmpty()) {
                            player.getWorld().dropItemNaturally(player.getLocation().add(0.0d, 0.5d, 0.0d), itemStack);
                        }
                    }
                });

                renameInventory.clickAction((event, customInventory) -> {
                    final int slot = event.getSlot();
                    final ItemStack currentItem = event.getCurrentItem();
                    final ItemStack cursorItem = event.getCursor();
                    final boolean hasExp = player.getLevel() >= 1 || player.getGameMode() == GameMode.CREATIVE;

                    if (slot == CURRENT_RENAMEABLE_ITEM_SLOT) {
                        final ItemStack secondItem = renameInventory.getItem(RENAMED_ITEM_SLOT);
                        assert secondItem != null;
                        final Component displayName = secondItem.getItemMeta().displayName();
                        assert displayName != null;
                        final String renameText = ChatUtils.serializePlainComponent(displayName);

                        plugin.runTask(() -> createRenamedItem(event.getCurrentItem(), renameInventory, renameText, hasExp));
                        return;
                    } else if (
                            slot == CURRENT_RENAMED_ITEM_SLOT
                            && currentItem != null
                            && renameInventory.getItem(CURRENT_RENAMEABLE_ITEM_SLOT) != null
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
                    final Inventory clickedInventory = event.getClickedInventory();

                    if (
                            !event.getClick().isShiftClick()
                            || clickedInventory == null
                            || clickedInventory.equals(customInventory)
                            || renameInventory.getItem(CURRENT_RENAMEABLE_ITEM_SLOT) != null
                    ) {
                        return;
                    }

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

    public static void open(final @NotNull Player player) {
        INVENTORY.open(player);
    }

    private static void createRenamedItem(
            final @Nullable ItemStack itemStack,
            final @NotNull Inventory inventory,
            final @NotNull String renameText,
            final boolean hasExp
    ) {
        final RenameableItem renameableItem = RenameableItemRegistry.fromRename(renameText, itemStack).orElse(null);

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

        final ItemStack redCross = inventory.getItem(RED_CROSS_SLOT);

        if (redCross != null) {
            inventory.setItem(RED_CROSS_SLOT, null);
        }
    }
}
