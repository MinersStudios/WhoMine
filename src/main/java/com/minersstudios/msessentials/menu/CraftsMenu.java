package com.minersstudios.msessentials.menu;

import com.minersstudios.mscore.inventory.*;
import com.minersstudios.mscore.inventory.action.ButtonClickAction;
import com.minersstudios.mscore.language.LanguageFile;
import com.minersstudios.mscore.utility.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.kyori.adventure.text.Component.translatable;

public final class CraftsMenu {
    public static final int RESULT_SLOT = 15;

    private static final Component CATEGORIES_TITLE = translatable("ms.menu.crafts.categories.title", ChatUtils.DEFAULT_STYLE);
    private static final Component CRAFTS_TITLE = translatable("ms.menu.crafts.category.title", ChatUtils.DEFAULT_STYLE);
    private static final Component CRAFT_TITLE = translatable("ms.menu.crafts.craft.title", ChatUtils.DEFAULT_STYLE);

    private static final InventoryButton CRAFTS_QUIT_BUTTON;
    private static final InventoryButton CRAFTS_PREVIOUS_BUTTON;
    private static final InventoryButton CRAFTS_PREVIOUS_BUTTON_EMPTY;
    private static final InventoryButton CRAFTS_NEXT_BUTTON;
    private static final InventoryButton CRAFTS_NEXT_BUTTON_EMPTY;

    private static final CustomInventory CATEGORIES_INVENTORY;
    private static final ElementPagedInventory BLOCKS_INVENTORY;
    private static final ElementPagedInventory DECORS_INVENTORY;
    private static final ElementPagedInventory ITEMS_INVENTORY;

    static {
        final InventoryButton blocksButton = new InventoryButton()
                .clickAction((event, i) -> {
                    final Player player = (Player) event.getWhoClicked();

                    open(Type.BLOCKS, player);
                    InventoryButton.playClickSound(player);
                });

        final InventoryButton decorsButton = new InventoryButton()
                .clickAction((event, i) -> {
                    final Player player = (Player) event.getWhoClicked();

                    open(Type.DECORS, player);
                    InventoryButton.playClickSound(player);
                });

        final InventoryButton itemsButton = new InventoryButton()
                .clickAction((event, i) -> {
                    final Player player = (Player) event.getWhoClicked();

                    open(Type.ITEMS, player);
                    InventoryButton.playClickSound(player);
                });

        CATEGORIES_INVENTORY = CustomInventory.single(CATEGORIES_TITLE, 4)
                .buttons(
                        IntStream.of(0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29)
                        .boxed()
                        .collect(Collectors.toMap(Function.identity(), slot -> blocksButton))
                )
                .buttons(
                        IntStream.of(3, 4, 5, 12, 13, 14, 21, 22, 23, 30, 31, 32)
                        .boxed()
                        .collect(Collectors.toMap(Function.identity(), slot -> decorsButton))
                )
                .buttons(
                        IntStream.of(6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35)
                        .boxed()
                        .collect(Collectors.toMap(Function.identity(), slot -> itemsButton))
                );

        final Component previousButton = LanguageFile.renderTranslationComponent("ms.menu.crafts.button.previous_page").style(ChatUtils.DEFAULT_STYLE);
        final Component nextButton = LanguageFile.renderTranslationComponent("ms.menu.crafts.button.next_page").style(ChatUtils.DEFAULT_STYLE);

        final ItemStack previousPageItem = new ItemStack(Material.PAPER);
        final ItemMeta previousPageMeta = previousPageItem.getItemMeta();

        previousPageMeta.displayName(previousButton);
        previousPageMeta.setCustomModelData(5001);
        previousPageItem.setItemMeta(previousPageMeta);

        final ItemStack previousPageEmptyItem = previousPageItem.clone();
        final ItemMeta previousPageEmptyMeta = previousPageEmptyItem.getItemMeta();

        previousPageEmptyMeta.displayName(previousButton);
        previousPageEmptyMeta.setCustomModelData(1);
        previousPageEmptyItem.setItemMeta(previousPageEmptyMeta);

        final ItemStack nextPageItem = previousPageItem.clone();
        final ItemMeta nextPageMeta = nextPageItem.getItemMeta();

        nextPageMeta.displayName(nextButton);
        nextPageMeta.setCustomModelData(5002);
        nextPageItem.setItemMeta(nextPageMeta);

        final ItemStack nextPageEmptyItem = previousPageItem.clone();
        final ItemMeta nextPageEmptyMeta = nextPageEmptyItem.getItemMeta();

        nextPageEmptyMeta.displayName(nextButton);
        nextPageEmptyMeta.setCustomModelData(1);
        nextPageEmptyItem.setItemMeta(nextPageEmptyMeta);

        final ButtonClickAction previousClick = (event, customInventory) -> {
            final PagedCustomInventory paged = (PagedCustomInventory) customInventory;
            final Player player = (Player) event.getWhoClicked();
            final CustomInventory previousPage = paged.getPage(paged.getPreviousPageIndex());

            if (previousPage != null) {
                player.openInventory(previousPage);
                InventoryButton.playClickSound(player);
            }
        };
        final ButtonClickAction nextClick = (event, customInventory) -> {
            final PagedCustomInventory paged = (PagedCustomInventory) customInventory;
            final Player player = (Player) event.getWhoClicked();
            final CustomInventory nextPage = paged.getPage(paged.getNextPageIndex());

            if (nextPage != null) {
                player.openInventory(nextPage);
                InventoryButton.playClickSound(player);
            }
        };

        CRAFTS_PREVIOUS_BUTTON = new InventoryButton(previousPageItem, previousClick);
        CRAFTS_PREVIOUS_BUTTON_EMPTY = CRAFTS_PREVIOUS_BUTTON.clone().item(previousPageEmptyItem);
        CRAFTS_NEXT_BUTTON = new InventoryButton(nextPageItem, nextClick);
        CRAFTS_NEXT_BUTTON_EMPTY = CRAFTS_NEXT_BUTTON.clone().item(nextPageEmptyItem);
        CRAFTS_QUIT_BUTTON = new InventoryButton()
                .clickAction((event, customInventory) -> {
                    final Player player = (Player) event.getWhoClicked();

                    open(Type.MAIN, player);
                    InventoryButton.playClickSound(player);
                });

        BLOCKS_INVENTORY = buildCraftsInventory();
        DECORS_INVENTORY = buildCraftsInventory();
        ITEMS_INVENTORY = buildCraftsInventory();
    }

    @SuppressWarnings("deprecation")
    public static void putCrafts(
            final @NotNull Type type,
            final @NotNull Collection<Recipe> recipes
    ) {
        final var elements = new ArrayList<InventoryButton>();
        final ElementPagedInventory customInventory = switch (type) {
            case BLOCKS -> BLOCKS_INVENTORY;
            case DECORS -> DECORS_INVENTORY;
            case ITEMS -> ITEMS_INVENTORY;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        for (final var recipe : recipes) {
            final ItemStack resultItem = recipe.getResult();
            final SingleInventory craftInventory = CustomInventory.single(CRAFT_TITLE, 4);

            if (recipe instanceof final ShapedRecipe shapedRecipe) {
                final String[] shapes = shapedRecipe.getShape();
                int i = 0;

                for (final var shape : shapes.length == 1 ? new String[]{"   ", shapes[0], "   "} : shapes) {
                    for (final var character : (shape.length() == 1 ? " " + shape + " " : shape.length() == 2 ? shape + " " : shape).toCharArray()) {
                        final ItemStack ingredient = shapedRecipe.getIngredientMap().get(character);

                        if (ingredient == null) {
                            i++;
                            continue;
                        }

                        switch (i) {
                            case 0 -> craftInventory.setItem(2, ingredient);
                            case 1 -> craftInventory.setItem(3, ingredient);
                            case 2 -> craftInventory.setItem(4, ingredient);
                            case 3 -> craftInventory.setItem(11, ingredient);
                            case 4 -> craftInventory.setItem(12, ingredient);
                            case 5 -> craftInventory.setItem(13, ingredient);
                            case 6 -> craftInventory.setItem(20, ingredient);
                            case 7 -> craftInventory.setItem(21, ingredient);
                            case 8 -> craftInventory.setItem(22, ingredient);
                        }

                        i++;
                    }
                }

                craftInventory.setItem(RESULT_SLOT, resultItem);

                elements.add(
                        new InventoryButton()
                        .item(resultItem)
                        .clickAction((buttonEvent, inventory) -> {
                            if (buttonEvent.getClick().isCreativeAction()) {
                                return;
                            }

                            final Player player = (Player) buttonEvent.getWhoClicked();

                            player.openInventory(
                                    craftInventory
                                    .buttonAt(
                                            31,
                                            new InventoryButton()
                                            .clickAction((event, inv) -> {
                                                player.openInventory(inventory);
                                                InventoryButton.playClickSound(player);
                                            })
                                    )
                            );
                            InventoryButton.playClickSound(player);
                        })
                );
            }
        }

        customInventory.elements(elements);
    }

    public static void open(
            final @NotNull Type type,
            final @NotNull Player player
    ) {
        (switch (type) {
            case MAIN -> CATEGORIES_INVENTORY;
            case BLOCKS -> BLOCKS_INVENTORY;
            case DECORS -> DECORS_INVENTORY;
            case ITEMS -> ITEMS_INVENTORY;
        }).open(player);
    }

    private static @NotNull ElementPagedInventory buildCraftsInventory() {
        return CustomInventory
                .elementPaged(CRAFTS_TITLE, 5, IntStream.range(0, 36).toArray())
                .staticButtonAt(
                        36,
                        inventory -> inventory.getPreviousPageIndex() == -1 ? CRAFTS_PREVIOUS_BUTTON_EMPTY : CRAFTS_PREVIOUS_BUTTON
                )
                .staticButtonAt(37, i -> CRAFTS_PREVIOUS_BUTTON_EMPTY)
                .staticButtonAt(38, i -> CRAFTS_PREVIOUS_BUTTON_EMPTY)
                .staticButtonAt(39, i -> CRAFTS_PREVIOUS_BUTTON_EMPTY)
                .staticButtonAt(40, i -> CRAFTS_QUIT_BUTTON)
                .staticButtonAt(
                        41,
                        inventory -> inventory.getNextPageIndex() == -1 ? CRAFTS_NEXT_BUTTON_EMPTY : CRAFTS_NEXT_BUTTON
                )
                .staticButtonAt(42, i -> CRAFTS_NEXT_BUTTON_EMPTY)
                .staticButtonAt(43, i -> CRAFTS_NEXT_BUTTON_EMPTY)
                .staticButtonAt(44, i -> CRAFTS_NEXT_BUTTON_EMPTY)
                .build();
    }

    public enum Type {
        MAIN, BLOCKS, DECORS, ITEMS
    }
}
