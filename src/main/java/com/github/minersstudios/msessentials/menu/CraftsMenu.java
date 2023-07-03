package com.github.minersstudios.msessentials.menu;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.inventory.*;
import com.github.minersstudios.mscore.inventory.actions.ButtonClickAction;
import com.github.minersstudios.mscore.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.minersstudios.mscore.config.LanguageFile.renderTranslationComponent;
import static com.github.minersstudios.mscore.inventory.InventoryButton.playClickSound;

public class CraftsMenu {
    public static final int
            RESULT_SLOT = 15,
            CRAFT_QUIT_BUTTON = 31,
            CRAFTS_QUIT_BUTTON = 40;

    @Contract(" -> new")
    public static @NotNull CustomInventory create() {
        InventoryButton blocksButton = InventoryButton.create()
                .item(new ItemStack(Material.AIR))
                .clickAction((event, i) -> {
                    Player player = (Player) event.getWhoClicked();
                    open(Type.BLOCKS, player);
                    playClickSound(player);
                });

        InventoryButton decorsButton = InventoryButton.create()
                .item(new ItemStack(Material.AIR))
                .clickAction((event, i) -> {
                    Player player = (Player) event.getWhoClicked();
                    open(Type.DECORS, player);
                    playClickSound(player);
                });

        InventoryButton itemsButton = InventoryButton.create()
                .item(new ItemStack(Material.AIR))
                .clickAction((event, i) -> {
                    Player player = (Player) event.getWhoClicked();
                    open(Type.ITEMS, player);
                    playClickSound(player);
                });

        return CustomInventory.create(Component.translatable("ms.menu.crafts.categories.title", NamedTextColor.WHITE), 4)
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
    }

    public static boolean open(@NotNull Type type, @NotNull Player player) {
        CustomInventoryMap customInventoryMap = MSCore.getCache().customInventoryMap;
        CustomInventory customInventory = switch (type) {
            case MAIN -> customInventoryMap.get("crafts");
            case BLOCKS -> customInventoryMap.get("crafts_blocks");
            case DECORS -> customInventoryMap.get("crafts_decors");
            case ITEMS -> customInventoryMap.get("crafts_items");
        };
        if (customInventory == null) return false;
        player.openInventory(customInventory);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Contract("_ -> new")
    public static @NotNull CustomInventory createCraftsInventory(@NotNull List<Recipe> recipes) {
        ItemStack
                previousPageItem = new ItemStack(Material.PAPER),
                previousPageNoCMD = new ItemStack(Material.PAPER);
        ItemMeta
                previousPageMeta = previousPageItem.getItemMeta(),
                previousPageMetaNoCMD = previousPageNoCMD.getItemMeta();
        Component previousButton = renderTranslationComponent("ms.menu.crafts.button.previous_page").style(ChatUtils.DEFAULT_STYLE);
        previousPageMetaNoCMD.displayName(previousButton);
        previousPageMeta.displayName(previousButton);
        previousPageMeta.setCustomModelData(5001);
        previousPageMetaNoCMD.setCustomModelData(1);
        previousPageNoCMD.setItemMeta(previousPageMetaNoCMD);
        previousPageItem.setItemMeta(previousPageMeta);

        ItemStack
                nextPageItem = new ItemStack(Material.PAPER),
                nextPageNoCMDItem = new ItemStack(Material.PAPER);
        ItemMeta
                nextPageMeta = nextPageItem.getItemMeta(),
                nextPageMetaNoCMD = nextPageNoCMDItem.getItemMeta();
        Component nextButton = renderTranslationComponent("ms.menu.crafts.button.next_page").style(ChatUtils.DEFAULT_STYLE);
        nextPageMetaNoCMD.displayName(nextButton);
        nextPageMeta.displayName(nextButton);
        nextPageMeta.setCustomModelData(5002);
        nextPageMetaNoCMD.setCustomModelData(1);
        nextPageNoCMDItem.setItemMeta(nextPageMetaNoCMD);
        nextPageItem.setItemMeta(nextPageMeta);

        ItemStack quitItem = new ItemStack(Material.PAPER);
        ItemMeta quitMeta = quitItem.getItemMeta();
        quitMeta.displayName(renderTranslationComponent("ms.menu.crafts.button.back").style(ChatUtils.DEFAULT_STYLE));
        quitMeta.setCustomModelData(1);
        quitItem.setItemMeta(quitMeta);

        var elements = new ArrayList<InventoryButton>();
        for (var recipe : recipes) {
            ItemStack resultItem = recipe.getResult();

            CustomInventory craftInventory = CustomInventory.create(Component.translatable("ms.menu.crafts.craft.title", ChatUtils.DEFAULT_STYLE), 4);

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
                                            CRAFT_QUIT_BUTTON,
                                            InventoryButton.create()
                                            .item(quitItem)
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

        ElementListedInventory craftsInventory = ElementListedInventory
                .create(
                        Component.translatable("ms.menu.crafts.category.title", ChatUtils.DEFAULT_STYLE),
                        5,
                        IntStream.range(0, 36).toArray()
                )
                .elements(elements);

        ButtonClickAction previousClick = (event, customInventory) -> {
            if (!(customInventory instanceof ListedInventory listedInventory)) return;

            Player player = (Player) event.getWhoClicked();
            ListedInventory previousPage = craftsInventory.getPage(listedInventory.getPreviousPageIndex());

            if (previousPage != null) {
                player.openInventory(previousPage);
                playClickSound(player);
            }
        };

        InventoryButton previousPageButton = InventoryButton.create().item(previousPageNoCMD).clickAction(previousClick);

        ButtonClickAction nextClick = (event, customInventory) -> {
            if (!(customInventory instanceof ListedInventory listedInventory)) return;

            Player player = (Player) event.getWhoClicked();
            ListedInventory nextPage = craftsInventory.getPage(listedInventory.getNextPageIndex());

            if (nextPage != null) {
                player.openInventory(nextPage);
                playClickSound(player);
            }
        };

        InventoryButton nextPageButton = InventoryButton.create().item(nextPageNoCMDItem).clickAction(nextClick);

        return craftsInventory
                .staticButtonAt(
                        36,
                        inventory -> InventoryButton.create()
                                .item(inventory.getPreviousPageIndex() == -1 ? previousPageNoCMD : previousPageItem)
                                .clickAction(previousClick)
                )
                .staticButtonAt(37, i -> previousPageButton)
                .staticButtonAt(38, i -> previousPageButton)
                .staticButtonAt(39, i -> previousPageButton)
                .staticButtonAt(
                        CRAFTS_QUIT_BUTTON,
                        i -> InventoryButton.create()
                                .item(quitItem)
                                .clickAction((event, customInventory) -> {
                                    Player player = (Player) event.getWhoClicked();
                                    open(Type.MAIN, player);
                                    playClickSound(player);
                                })
                )
                .staticButtonAt(
                        41,
                        inventory -> InventoryButton.create()
                                .item(inventory.getNextPageIndex() == -1 ? nextPageNoCMDItem : nextPageItem)
                                .clickAction(nextClick)
                )
                .staticButtonAt(42, i -> nextPageButton)
                .staticButtonAt(43, i -> nextPageButton)
                .staticButtonAt(44, i -> nextPageButton)
                .build();
    }

    public enum Type {
        MAIN, BLOCKS, DECORS, ITEMS
    }
}
