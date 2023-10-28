package com.minersstudios.msdecor.registry.decoration.street;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.location.MSBoundingBox;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.ItemUtils;
import com.minersstudios.mscore.util.SoundGroup;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.DecorParameter;
import com.minersstudios.msdecor.api.Facing;
import org.bukkit.*;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class Brazier extends CustomDecorDataImpl<Brazier> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1183);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Мангал"));
        itemStack.setItemMeta(itemMeta);

        final ItemStack itemStack2 = itemStack.clone();
        final ItemMeta itemMeta2 = itemStack2.getItemMeta();

        itemMeta2.setCustomModelData(1184);
        itemStack2.setItemMeta(itemMeta2);

        return new Builder()
                .key("brazier")
                .hitBox(new DecorHitBox(
                        0.875d,
                        1.0d,
                        0.875d,
                        DecorHitBox.Type.LIGHT
                ))
                .facing(Facing.FLOOR)
                .soundGroup(SoundGroup.CHAIN)
                .itemStack(itemStack)
                .recipes(
                        Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "B B",
                                        "BBB",
                                        " I "
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('B', Material.IRON_BARS),
                                        ShapedRecipeBuilder.material('I', Material.IRON_INGOT)
                                ),
                                true
                        )
                )
                .parameters(DecorParameter.LIGHT_TYPED)
                .lightLevels(0, 15)
                .lightLevelTypes(
                        builder -> Map.entry(
                                0,
                                new Type(
                                        builder,
                                        "default",
                                        itemStack
                                )
                        ),
                        builder -> Map.entry(
                                15,
                                new Type(
                                        builder,
                                        "fired",
                                        itemStack2
                                )
                        )
                )
                .clickAction(
                        event -> {
                            if (event.getClickType().isLeftClick()) return;

                            final Player player = event.getPlayer();
                            final ItemStack itemInUse = player.getInventory().getItem(event.getHand());
                            final Material material = itemInUse.getType();
                            final boolean isShovel = Tag.ITEMS_SHOVELS.isTagged(material);
                            final boolean isFlintAndSteel = material == Material.FLINT_AND_STEEL;

                            if (
                                    !isShovel
                                    && !isFlintAndSteel
                            ) return;

                            final var customDecor = event.getCustomDecor();
                            final var data = customDecor.getData();
                            final var type = data.getLightTypeOf(event.getClickedInteraction());

                            if (type == null) return;

                            final World world = player.getWorld();
                            final int nextLevel;

                            switch (type.getKey().getKey()) {
                                case "brazier.type.fired" -> {
                                    nextLevel = 0;

                                    if (isShovel) {
                                        world.playSound(
                                                customDecor.getBoundingBox().getCenter().toLocation(),
                                                Sound.BLOCK_FIRE_EXTINGUISH,
                                                SoundCategory.PLAYERS,
                                                1.0f,
                                                1.0f
                                        );
                                    } else {
                                        return;
                                    }
                                }
                                case "brazier.type.default" -> {
                                    nextLevel = 15;

                                    if (isFlintAndSteel) {
                                        world.playSound(
                                                customDecor.getBoundingBox().getCenter().toLocation(),
                                                Sound.ITEM_FLINTANDSTEEL_USE,
                                                SoundCategory.PLAYERS,
                                                1.0f,
                                                1.0f
                                        );
                                    } else {
                                        return;
                                    }
                                }
                                default -> nextLevel = -1;
                            }

                            final var nextType = data.getLightTypeOf(nextLevel);

                            if (nextType == null) return;

                            final ItemDisplay itemDisplay = customDecor.getDisplay();
                            final ItemStack displayItem = itemDisplay.getItemStack();
                            assert displayItem != null;
                            final ItemStack typeItem = nextType.getItem();
                            final ItemMeta typeMeta = typeItem.getItemMeta();

                            typeMeta.displayName(displayItem.getItemMeta().displayName());
                            typeItem.setItemMeta(typeMeta);
                            itemDisplay.setItemStack(typeItem);

                            final MSBoundingBox msbb = event.getCustomDecor().getBoundingBox();

                            for (final var block : msbb.getBlockStates(world)) {
                                if (block.getBlockData() instanceof final Light light) {
                                    light.setLevel(nextLevel);
                                    block.setBlockData(light);
                                }
                            }

                            if (player.getGameMode() == GameMode.SURVIVAL) {
                                ItemUtils.damageItem(player, itemInUse);
                            }

                            player.swingHand(event.getHand());
                        }
                );
    }
}