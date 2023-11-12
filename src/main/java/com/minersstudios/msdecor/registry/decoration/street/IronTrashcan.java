package com.minersstudios.msdecor.registry.decoration.street;

import com.minersstudios.mscore.inventory.recipe.RecipeBuilder;
import com.minersstudios.mscore.inventory.recipe.ShapedRecipeBuilder;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.sound.SoundGroup;
import com.minersstudios.msdecor.api.CustomDecorDataImpl;
import com.minersstudios.msdecor.api.DecorHitBox;
import com.minersstudios.msdecor.api.Facing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.kyori.adventure.text.Component.text;

public final class IronTrashcan extends CustomDecorDataImpl<IronTrashcan> {
    public static final Component INV_NAME = text("Мусорка").color(NamedTextColor.DARK_GRAY);

    @Override
    protected @NotNull Builder builder() {
        final ItemStack itemStack = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setCustomModelData(1148);
        itemMeta.displayName(ChatUtils.createDefaultStyledText("Железная мусорка"));
        itemStack.setItemMeta(itemMeta);

        return new Builder()
                .key("iron_trashcan")
                .hitBox(
                        DecorHitBox.builder()
                        .type(DecorHitBox.Type.SOLID)
                        .size(1.0d, 1.0d, 1.0d)
                        .build()
                )
                .facings(Facing.FLOOR)
                .soundGroup(SoundGroup.ANVIL)
                .itemStack(itemStack)
                .recipes(
                        Map.entry(
                                RecipeBuilder.shapedBuilder()
                                .category(CraftingBookCategory.BUILDING)
                                .shape(
                                        "III",
                                        "I I",
                                        "III"
                                )
                                .ingredients(ShapedRecipeBuilder.material('I', Material.IRON_INGOT)),
                                true
                        )
                )
                .clickAction(
                        event -> {
                            if (event.getClickType().isLeftClick()) return;

                            final Player player = event.getPlayer();

                            player.openInventory(player.getServer().createInventory(null, 4 * 9, IronTrashcan.INV_NAME));
                            player.getWorld().playSound(
                                    event.getClickedInteraction().getLocation(),
                                    Sound.BLOCK_BARREL_OPEN,
                                    SoundCategory.PLAYERS,
                                    1.0f,
                                    1.0f
                            );
                            player.swingHand(event.getHand());
                        }
                );
    }
}
