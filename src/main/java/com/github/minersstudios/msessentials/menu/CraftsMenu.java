package com.github.minersstudios.msessentials.menu;

import com.github.minersstudios.mscore.inventory.*;
import com.github.minersstudios.mscore.inventory.actions.ButtonClickAction;
import com.github.minersstudios.mscore.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslationComponent;
import static com.github.minersstudios.mscore.inventory.InventoryButton.playClickSound;

public class CraftsMenu {
    public static final int RESULT_SLOT = 15;

    private static final Component CATEGORIES_TITLE = Component.translatable("ms.menu.crafts.categories.title", ChatUtils.DEFAULT_STYLE);
    private static final Component CRAFTS_TITLE = Component.translatable("ms.menu.crafts.category.title", ChatUtils.DEFAULT_STYLE);
    private static final Component CRAFT_TITLE = Component.translatable("ms.menu.crafts.craft.title", ChatUtils.DEFAULT_STYLE);

    private static final InventoryButton CRAFTS_QUIT_BUTTON;
    private static final InventoryButton CRAFTS_PREVIOUS_BUTTON;
    private static final InventoryButton CRAFTS_PREVIOUS_BUTTON_NO_CMD;
    private static final InventoryButton CRAFTS_NEXT_BUTTON;
    private static final InventoryButton CRAFTS_NEXT_BUTTON_NO_CMD;

    private static final CustomInventory CATEGORIES_INVENTORY;
    private static final ElementPagedInventory BLOCKS_INVENTORY;
    private static final ElementPagedInventory DECORS_INVENTORY;
    private static final ElementPagedInventory ITEMS_INVENTORY;

    static {
        InventoryButton blocksButton = InventoryButton.create()
                .clickAction((event, i) -> {
                    Player player = (Player) event.getWhoClicked();
                    open(Type.BLOCKS, player);
                    playClickSound(player);
                });

        InventoryButton decorsButton = InventoryButton.create()
                .clickAction((event, i) -> {
                    Player player = (Player) event.getWhoClicked();
                    open(Type.DECORS, player);
                    playClickSound(player);
                });

        InventoryButton itemsButton = InventoryButton.create()
                .clickAction((event, i) -> {
                    Player player = (Player) event.getWhoClicked();
                    open(Type.ITEMS, player);
                    playClickSound(player);
                });

        CATEGORIES_INVENTORY = SingleInventory.single(CATEGORIES_TITLE, 4)
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

        ItemStack previousPageItem = new ItemStack(Material.PAPER);
        ItemStack previousPageNoCMD = new ItemStack(Material.PAPER);
        ItemStack nextPageItem = new ItemStack(Material.PAPER);
        ItemStack nextPageNoCMDItem = new ItemStack(Material.PAPER);

        ItemMeta previousPageMeta = previousPageItem.getItemMeta();
        ItemMeta previousPageMetaNoCMD = previousPageNoCMD.getItemMeta();
        ItemMeta nextPageMeta = nextPageItem.getItemMeta();
        ItemMeta nextPageMetaNoCMD = nextPageNoCMDItem.getItemMeta();

        Component previousButton = renderTranslationComponent("ms.menu.crafts.button.previous_page").style(ChatUtils.DEFAULT_STYLE);
        Component nextButton = renderTranslationComponent("ms.menu.crafts.button.next_page").style(ChatUtils.DEFAULT_STYLE);

        previousPageMetaNoCMD.displayName(previousButton);
        previousPageMeta.displayName(previousButton);
        previousPageMeta.setCustomModelData(5001);
        previousPageMetaNoCMD.setCustomModelData(1);
        previousPageNoCMD.setItemMeta(previousPageMetaNoCMD);
        previousPageItem.setItemMeta(previousPageMeta);

        nextPageMetaNoCMD.displayName(nextButton);
        nextPageMeta.displayName(nextButton);
        nextPageMeta.setCustomModelData(5002);
        nextPageMetaNoCMD.setCustomModelData(1);
        nextPageNoCMDItem.setItemMeta(nextPageMetaNoCMD);
        nextPageItem.setItemMeta(nextPageMeta);

        ButtonClickAction previousClick = (event, customInventory) -> {
            if (!(customInventory instanceof PagedCustomInventory pagedInventory)) return;

            Player player = (Player) event.getWhoClicked();
            CustomInventory previousPage = pagedInventory.getPage(pagedInventory.getPreviousPageIndex());

            if (previousPage != null) {
                player.openInventory(previousPage);
                playClickSound(player);
            }
        };

        CRAFTS_PREVIOUS_BUTTON = InventoryButton.create().item(previousPageItem).clickAction(previousClick);
        CRAFTS_PREVIOUS_BUTTON_NO_CMD = InventoryButton.create()
                .item(previousPageNoCMD)
                .clickAction(previousClick);

        ButtonClickAction nextClick = (event, customInventory) -> {
            if (!(customInventory instanceof PagedCustomInventory pagedInventory)) return;

            Player player = (Player) event.getWhoClicked();
            CustomInventory nextPage = pagedInventory.getPage(pagedInventory.getNextPageIndex());

            if (nextPage != null) {
                player.openInventory(nextPage);
                playClickSound(player);
            }
        };

        CRAFTS_NEXT_BUTTON = InventoryButton.create().item(nextPageItem).clickAction(nextClick);
        CRAFTS_NEXT_BUTTON_NO_CMD = InventoryButton.create()
                .item(nextPageNoCMDItem)
                .clickAction(nextClick);

        CRAFTS_QUIT_BUTTON = InventoryButton.create()
                .clickAction((event, customInventory) -> {
                    Player player = (Player) event.getWhoClicked();
                    open(Type.MAIN, player);
                    playClickSound(player);
                });

        BLOCKS_INVENTORY = buildCraftsInventory();
        DECORS_INVENTORY = buildCraftsInventory();
        ITEMS_INVENTORY = buildCraftsInventory();
    }

    @SuppressWarnings("deprecation")
    public static void putCrafts(
            @NotNull Type type,
            @NotNull List<Recipe> recipes
    ) {
        var elements = new ArrayList<InventoryButton>();
        ElementPagedInventory customInventory = switch (type) {
            case BLOCKS -> BLOCKS_INVENTORY;
            case DECORS -> DECORS_INVENTORY;
            case ITEMS -> ITEMS_INVENTORY;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        for (var recipe : recipes) {
            ItemStack resultItem = recipe.getResult();
            SingleInventory craftInventory = SingleInventory.single(CRAFT_TITLE, 4);

            if (recipe instanceof ShapedRecipe shapedRecipe) {
                String[] shapes = shapedRecipe.getShape();
                int i = 0;

                for (var shape : shapes.length == 1 ? new String[]{"   ", shapes[0], "   "} : shapes) {
                    for (var character : (shape.length() == 1 ? " " + shape + " " : shape.length() == 2 ? shape + " " : shape).toCharArray()) {
                        ItemStack ingredient = shapedRecipe.getIngredientMap().get(character);

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
                        InventoryButton.create()
                        .item(resultItem)
                        .clickAction((clickEvent, inventory) -> {
                            Player player = (Player) clickEvent.getWhoClicked();
                            player.openInventory(
                                    craftInventory
                                    .buttonAt(
                                            31,
                                            InventoryButton.create()
                                            .clickAction((event, inv) -> {
                                                player.openInventory(inventory);
                                                playClickSound(player);
                                            })
                                    )
                            );
                            playClickSound(player);
                        })
                );
            }
        }

        customInventory.elements(elements);
    }

    public static void open(
            @NotNull Type type,
            @NotNull Player player
    ) {
        (switch (type) {
            case MAIN -> CATEGORIES_INVENTORY;
            case BLOCKS -> BLOCKS_INVENTORY;
            case DECORS -> DECORS_INVENTORY;
            case ITEMS -> ITEMS_INVENTORY;
        }).open(player);
    }

    private static @NotNull ElementPagedInventory buildCraftsInventory() {
        return ElementPagedInventory
                .elementPaged(CRAFTS_TITLE, 5, IntStream.range(0, 36).toArray())
                .staticButtonAt(
                        36,
                        inventory -> inventory.getPreviousPageIndex() == -1 ? CRAFTS_PREVIOUS_BUTTON_NO_CMD : CRAFTS_PREVIOUS_BUTTON
                )
                .staticButtonAt(37, i -> CRAFTS_PREVIOUS_BUTTON_NO_CMD)
                .staticButtonAt(38, i -> CRAFTS_PREVIOUS_BUTTON_NO_CMD)
                .staticButtonAt(39, i -> CRAFTS_PREVIOUS_BUTTON_NO_CMD)
                .staticButtonAt(40, i -> CRAFTS_QUIT_BUTTON)
                .staticButtonAt(
                        41,
                        inventory -> inventory.getNextPageIndex() == -1 ? CRAFTS_NEXT_BUTTON_NO_CMD : CRAFTS_NEXT_BUTTON
                )
                .staticButtonAt(42, i -> CRAFTS_NEXT_BUTTON_NO_CMD)
                .staticButtonAt(43, i -> CRAFTS_NEXT_BUTTON_NO_CMD)
                .staticButtonAt(44, i -> CRAFTS_NEXT_BUTTON_NO_CMD)
                .build();
    }

    public enum Type {
        MAIN, BLOCKS, DECORS, ITEMS
    }
}
