package com.minersstudios.msdecor.registry.furniture.lamp;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.msdecor.api.*;
import com.minersstudios.msdecor.event.CustomDecorClickEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class BigLamp extends CustomDecorDataImpl<BigLamp> {

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1151);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Большая лампа"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("big_lamp")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.LIGHT)
                        .size(0.875d, 2.3125d, 0.875d)
                        .modelOffsetY(0.375d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.WOOD)
                .itemStack(itemStack)
                .recipes(
                        unused -> Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "L",
                                        "S",
                                        "S"
                                )
                                .ingredients(
                                        ShapedRecipeBuilder.material('S', Material.STICK),
                                        ShapedRecipeBuilder.material('L', Material.LEATHER)
                                ),
                                Boolean.TRUE
                        )
                )
                .parameters(
                        DecorParameter.LIGHTABLE,
                        DecorParameter.PAINTABLE
                )
                .lightLevels(0, 15)
                .clickAction(BigLamp::playClick);
    }

    static void playClick(final @NotNull CustomDecorClickEvent event) {
        if (event.getClickType().isLeftClick()) {
            return;
        }

        final CustomDecor customDecor = event.getCustomDecor();
        final Interaction interaction = event.getClickedInteraction();
        final World world = interaction.getWorld();

        DecorParameter.doLight(
                event,
                customDecor.getData().getNextLightLevel(
                        world.getBlockAt(interaction.getLocation()).getBlockData() instanceof final Light light
                                ? light.getLevel()
                                : 0
                )
        );
        world.playSound(
                customDecor.getBoundingBox().getCenter().toLocation(),
                Sound.BLOCK_LEVER_CLICK,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );
        event.getPlayer().swingHand(event.getHand());
    }
}
