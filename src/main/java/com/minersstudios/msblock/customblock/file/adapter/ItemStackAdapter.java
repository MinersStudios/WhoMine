package com.minersstudios.msblock.customblock.file.adapter;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Locale;

/**
 * Gson adapter for serializing and deserializing ItemStack objects.
 * This adapter handles ItemStack serialization by converting it into
 * a JsonObject, and deserialization by reading the JsonObject and
 * constructing the corresponding ItemStack.
 * <p>
 * Serialized output you can see in the "MSBlock/blocks/example.json" file.
 */
public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    private static final String TYPE_KEY = "type";
    private static final String AMOUNT_KEY = "amount";
    private static final String NBT_KEY = "nbt";

    @Override
    public @NotNull ItemStack deserialize(
            final @NotNull JsonElement json,
            final @NotNull Type typeOfT,
            final @NotNull JsonDeserializationContext context
    ) throws JsonParseException, IllegalArgumentException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final String typeName = jsonObject.get(TYPE_KEY).getAsString().toUpperCase(Locale.ENGLISH);
        final Material type = Material.valueOf(typeName);

        final int amount = jsonObject.get(AMOUNT_KEY).getAsInt();
        final ItemStack itemStack = new ItemStack(type, amount);

        if (jsonObject.has(NBT_KEY)) {
            final net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            final String nbt = jsonObject.get(NBT_KEY).getAsString();

            try {
                nmsItemStack.setTag(TagParser.parseTag(nbt));
            } catch (CommandSyntaxException e) {
                throw new JsonParseException("Invalid NBT: " + nbt);
            }

            return nmsItemStack.asBukkitCopy();
        }

        return itemStack;
    }

    @Override
    public @NotNull JsonElement serialize(
            final @NotNull ItemStack src,
            final @NotNull Type typeOfSrc,
            final @NotNull JsonSerializationContext context
    ) {
        final JsonObject jsonObject = new JsonObject();
        final String nbt = src.getItemMeta().getAsString();

        jsonObject.addProperty(TYPE_KEY, src.getType().name());
        jsonObject.addProperty(AMOUNT_KEY, src.getAmount());

        if (!nbt.equals("{}")) {
            jsonObject.addProperty(NBT_KEY, nbt);
        }

        return jsonObject;
    }
}
