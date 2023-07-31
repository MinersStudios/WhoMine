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

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    private static final String TYPE_KEY = "type";
    private static final String AMOUNT_KEY = "amount";
    private static final String NBT_KEY = "nbt";

    @Override
    public @NotNull ItemStack deserialize(
            @NotNull JsonElement json,
            @NotNull Type typeOfT,
            @NotNull JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String typeName = jsonObject.get(TYPE_KEY).getAsString().toUpperCase(Locale.ENGLISH);
        Material type;

        try {
            type = Material.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unknown Material: " + typeName);
        }

        int amount = jsonObject.get(AMOUNT_KEY).getAsInt();
        ItemStack itemStack = new ItemStack(type, amount);

        if (jsonObject.has(NBT_KEY)) {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            String nbt = jsonObject.get(NBT_KEY).getAsString();

            try {
                nmsItemStack.setTag(TagParser.parseTag(nbt));
            } catch (CommandSyntaxException e) {
                throw new JsonParseException("Invalid NBT: " + nbt);
            }

            return nmsItemStack.getBukkitStack();
        }

        return itemStack;
    }

    @Override
    public @NotNull JsonElement serialize(
            @NotNull ItemStack src,
            @NotNull Type typeOfSrc,
            @NotNull JsonSerializationContext context
    ) {
        JsonObject jsonObject = new JsonObject();
        String nbt = src.getItemMeta().getAsString();

        jsonObject.addProperty(TYPE_KEY, src.getType().toString());
        jsonObject.addProperty(AMOUNT_KEY, src.getAmount());

        if (!nbt.equals("{}")) {
            jsonObject.addProperty(NBT_KEY, nbt);
        }

        return jsonObject;
    }
}
