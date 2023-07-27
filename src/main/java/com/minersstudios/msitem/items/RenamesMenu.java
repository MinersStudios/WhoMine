package com.minersstudios.msitem.items;

import com.minersstudios.mscore.inventory.ElementPagedInventory;
import com.minersstudios.mscore.inventory.InventoryButton;
import com.minersstudios.mscore.inventory.PagedInventory;
import com.minersstudios.mscore.inventory.SingleInventory;
import com.minersstudios.mscore.inventory.actions.ButtonClickAction;
import com.minersstudios.mscore.plugin.MSPlugin;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.MSItemUtils;
import com.minersstudios.msitem.MSItem;
import com.minersstudios.msitem.utils.CustomItemUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.minersstudios.mscore.inventory.InventoryButton.playClickSound;
import static com.minersstudios.mscore.util.ChatUtils.createDefaultStyledText;
import static net.kyori.adventure.text.Component.text;

public class RenamesMenu {
    public static final int
            renameableItemSlot = 2,
            renamedItemSlot = 6,
            quitRenameButtonSlot = 40,
            quitRenamesButtonSlot = 40,
            currentRenameableItemSlot = 20,
            currentRenamedItemSlot = 24,
            redCrossSlot = 22;

    private static final ElementPagedInventory INVENTORY;

    static {
        ButtonClickAction previousClick = (event, customInventory) -> {
            if (!(customInventory instanceof PagedInventory pagedInventory)) return;

            Player player = (Player) event.getWhoClicked();
            PagedInventory previousPage = pagedInventory.getPage(pagedInventory.getPreviousPageIndex());

            if (previousPage != null) {
                player.openInventory(previousPage);
                playClickSound(player);
            }
        };

        InventoryButton previousPageButton = InventoryButton.create().item(createPreviousPageButton()[1]).clickAction(previousClick);

        ButtonClickAction nextClick = (event, customInventory) -> {
            if (!(customInventory instanceof PagedInventory pagedInventory)) return;

            Player player = (Player) event.getWhoClicked();
            PagedInventory nextPage = pagedInventory.getPage(pagedInventory.getNextPageIndex());

            if (nextPage != null) {
                player.openInventory(nextPage);
                playClickSound(player);
            }
        };

        InventoryButton nextButton = InventoryButton.create().item(createNextPageButton()[1]).clickAction(nextClick);

        INVENTORY = ElementPagedInventory.elementPaged(ChatUtils.createDefaultStyledText("뀂ꀰ"), 5, IntStream.range(0, 36).toArray())
                .staticButtonAt(
                        36,
                        inventory -> InventoryButton.create()
                                .item(createPreviousPageButton()[inventory.getPreviousPageIndex() == -1 ? 1 : 0])
                                .clickAction(previousClick)
                )
                .staticButtonAt(37, i -> previousPageButton)
                .staticButtonAt(38, i -> previousPageButton)
                .staticButtonAt(39, i -> previousPageButton)
                .staticButtonAt(quitRenamesButtonSlot, i -> InventoryButton.create()
                        .item(createQuitButton())
                        .clickAction((event, customInventory) -> {
                            Player player = (Player) event.getWhoClicked();
                            player.closeInventory();
                            playClickSound(player);
                        }))
                .staticButtonAt(
                        41,
                        inventory -> InventoryButton.create()
                                .item(createNextPageButton()[inventory.getNextPageIndex() == -1 ? 1 : 0])
                                .clickAction(nextClick)
                )
                .staticButtonAt(42, i -> nextButton)
                .staticButtonAt(43, i -> nextButton)
                .staticButtonAt(44, i -> nextButton)
                .build();
    }

    public static void update() {
        var elements = new ArrayList<InventoryButton>();

        for (var renameableItem : MSPlugin.getGlobalCache().renameableItemsMenu) {
            ItemStack resultItem = renameableItem.getResultItemStack();

            elements.add(InventoryButton.create()
                    .item(resultItem)
                    .clickAction((buttonEvent, inventory) -> {
                        Player player = (Player) buttonEvent.getWhoClicked();
                        var renameInventory = SingleInventory.single(ChatUtils.createDefaultStyledText("뀃ꀱ"), 5);
                        var renameableItemStacks = renameableItem.getRenameableItemStacks();

                        if (renameableItemStacks.size() == 1) {
                            renameInventory.setItem(renameableItemSlot, renameableItemStacks.get(0));
                        } else {
                            new BukkitRunnable() {
                                int index = 0;

                                @Override
                                public void run() {
                                    if (!buttonEvent.getView().getTopInventory().equals(renameInventory))
                                        this.cancel();
                                    renameInventory.setItem(renameableItemSlot, renameableItemStacks.get(this.index));
                                    this.index++;
                                    if (this.index + 1 > renameableItemStacks.size()) {
                                        this.index = 0;
                                    }
                                }
                            }.runTaskTimer(MSItem.getInstance(), 0L, 10L);
                        }

                        renameInventory.setItem(renamedItemSlot, resultItem);
                        renameInventory.buttonAt(quitRenameButtonSlot, InventoryButton.create()
                                .item(createQuitButton())
                                .clickAction((e, i) -> {
                                    player.openInventory(inventory);
                                    playClickSound(player);
                                }));

                        renameInventory.closeAction((e, customInventory) -> {
                            ItemStack itemStack = customInventory.getItem(currentRenameableItemSlot);
                            if (itemStack != null) {
                                Map<Integer, ItemStack> map = player.getInventory().addItem(itemStack);
                                if (!map.isEmpty()) {
                                    player.getWorld().dropItemNaturally(player.getLocation().add(0.0d, 0.5d, 0.0d), itemStack);
                                }
                            }
                        });

                        renameInventory.clickAction((event, customInventory) -> {
                            int slot = event.getSlot();
                            ItemStack currentItem = event.getCurrentItem();
                            ItemStack cursorItem = event.getCursor();
                            boolean hasExp = player.getLevel() >= 1 || player.getGameMode() == GameMode.CREATIVE;

                            if (slot == currentRenameableItemSlot) {
                                ItemStack secondItem = renameInventory.getItem(renamedItemSlot);
                                assert secondItem != null;
                                String renameText = ChatUtils.serializePlainComponent(Objects.requireNonNull(secondItem.getItemMeta().displayName()));
                                MSItem.getInstance().runTask(() ->
                                        createRenamedItem(event.getCurrentItem(), renameInventory, renameText, hasExp)
                                );
                                return;
                            } else if (
                                    slot == currentRenamedItemSlot
                                            && currentItem != null
                                            && renameInventory.getItem(currentRenameableItemSlot) != null
                                            && cursorItem != null
                                            && cursorItem.getType().isAir()
                                            && hasExp
                            ) {
                                player.setItemOnCursor(currentItem);
                                renameInventory.setItem(currentRenameableItemSlot, null);
                                renameInventory.setItem(currentRenamedItemSlot, null);
                                player.giveExpLevels(-1);
                            }

                            event.setCancelled(!event.getClick().isCreativeAction());
                        });

                        player.openInventory(renameInventory);
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
        if (MSItemUtils.getCustomItem(itemStack).orElse(null) instanceof Renameable renameable) {
            inventory.setItem(currentRenamedItemSlot, renameable.createRenamedItem(itemStack, renameText));
            if (!hasExp) {
                inventory.setItem(redCrossSlot, createRedCross());
            }
            return;
        } else {
            RenameableItem renameableItem = CustomItemUtils.getRenameableItem(itemStack, renameText);
            if (
                    renameableItem != null
                            && renameableItem.isWhiteListed((OfflinePlayer) inventory.getViewers().get(0))
            ) {
                inventory.setItem(currentRenamedItemSlot, renameableItem.createRenamedItem(itemStack, renameText));
                if (!hasExp) {
                    inventory.setItem(redCrossSlot, createRedCross());
                }
                return;
            }
        }
        inventory.setItem(currentRenamedItemSlot, null);
        ItemStack redCross = inventory.getItem(redCrossSlot);
        if (redCross != null) {
            inventory.setItem(redCrossSlot, null);
        }
    }

    @Contract(" -> new")
    private static ItemStack @NotNull [] createPreviousPageButton() {
        ItemStack previousPage = new ItemStack(Material.PAPER),
                previousPageNoCMD = new ItemStack(Material.PAPER);
        ItemMeta previousPageMeta = previousPage.getItemMeta(),
                previousPageMetaNoCMD = previousPageNoCMD.getItemMeta();
        previousPageMetaNoCMD.displayName(createDefaultStyledText("Предыдущая страница"));
        previousPageMeta.displayName(createDefaultStyledText("Предыдущая страница"));
        previousPageMeta.setCustomModelData(5001);
        previousPageMetaNoCMD.setCustomModelData(1);
        previousPageNoCMD.setItemMeta(previousPageMetaNoCMD);
        previousPage.setItemMeta(previousPageMeta);
        return new ItemStack[]{previousPage, previousPageNoCMD};
    }

    @Contract(" -> new")
    private static ItemStack @NotNull [] createNextPageButton() {
        ItemStack nextPage = new ItemStack(Material.PAPER),
                nextPageNoCMD = new ItemStack(Material.PAPER);
        ItemMeta nextPageMeta = nextPage.getItemMeta(),
                nextPageMetaNoCMD = nextPageNoCMD.getItemMeta();
        nextPageMetaNoCMD.displayName(createDefaultStyledText("Следующая страница"));
        nextPageMeta.displayName(createDefaultStyledText("Следующая страница"));
        nextPageMeta.setCustomModelData(5002);
        nextPageMetaNoCMD.setCustomModelData(1);
        nextPageNoCMD.setItemMeta(nextPageMetaNoCMD);
        nextPage.setItemMeta(nextPageMeta);
        return new ItemStack[]{nextPage, nextPageNoCMD};
    }

    @Contract(" -> new")
    private static @NotNull ItemStack createQuitButton() {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(createDefaultStyledText("Вернуться"));
        itemMeta.setCustomModelData(1);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Contract(" -> new")
    private static @NotNull ItemStack createRedCross() {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(text("Вам не хватает 1 уровня опыта", ChatUtils.COLORLESS_DEFAULT_STYLE).color(NamedTextColor.GRAY));
        itemMeta.setCustomModelData(5003);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
