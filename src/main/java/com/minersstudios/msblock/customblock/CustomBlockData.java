package com.minersstudios.msblock.customblock;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.mscore.util.MSCustomUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class CustomBlockData implements Cloneable {
    private @NotNull NamespacedKey namespacedKey;
    private float digSpeed;
    private int expToDrop;
    private boolean dropsDefaultItem;
    private @NotNull ToolType toolType;
    private boolean forceTool;
    private @Nullable String itemName;
    private int itemCustomModelData;
    private @Nullable NoteBlockData noteBlockData;
    private @Nullable Set<Material> placeableMaterials;
    private @NotNull SoundGroup soundGroup;
    private @Nullable PlacingType placingType;
    private @Nullable Map<BlockFace, NoteBlockData> blockFaceMap;
    private @Nullable Map<Axis, NoteBlockData> blockAxisMap;
    private boolean showInCraftsMenu;
    private @Nullable ShapedRecipe shapedRecipe;

    protected File file;
    protected YamlConfiguration config;

    public CustomBlockData(
            @NotNull NamespacedKey namespacedKey,
            float digSpeed,
            int expToDrop,
            boolean dropsDefaultItem,
            @NotNull ToolType toolType,
            boolean forceTool,
            @Nullable String itemName,
            int itemCustomModelData,
            @Nullable NoteBlockData noteBlockData,
            @Nullable Set<Material> placeableMaterials,
            @NotNull SoundGroup soundGroup,
            @Nullable PlacingType placingType,
            @Nullable Map<BlockFace, NoteBlockData> blockFaceMap,
            @Nullable Map<Axis, NoteBlockData> blockAxisMap,
            boolean showInCraftsMenu,
            @Nullable ShapedRecipe shapedRecipe
    ) {
        this.namespacedKey = namespacedKey;
        this.digSpeed = digSpeed;
        this.expToDrop = expToDrop;
        this.dropsDefaultItem = dropsDefaultItem;
        this.toolType = toolType;
        this.forceTool = forceTool;
        this.itemName = itemName;
        this.itemCustomModelData = itemCustomModelData;
        this.noteBlockData = noteBlockData;
        this.placeableMaterials = placeableMaterials;
        this.soundGroup = soundGroup;
        this.placingType = placingType;
        this.blockFaceMap = blockFaceMap;
        this.blockAxisMap = blockAxisMap;
        this.showInCraftsMenu = showInCraftsMenu;
        this.shapedRecipe = shapedRecipe;
    }

    @Contract("_, _ -> new")
    public static @NotNull CustomBlockData fromConfig(@NotNull File file, @NotNull YamlConfiguration config) {
        String fileName = file.getName();

        NamespacedKey namespacedKey = new NamespacedKey(
                MSBlock.getInstance(),
                Objects.requireNonNull(config.getString("namespaced-key"), "namespaced-key in " + fileName + " is null")
        );

        ConfigurationSection blockSettings = Objects.requireNonNull(
                config.getConfigurationSection("block-settings"),
                "block-settings section in " + fileName + " is null"
        );
        ConfigurationSection item = Objects.requireNonNull(
                config.getConfigurationSection("item"),
                "item section in " + fileName + " is null"
        );
        ConfigurationSection sounds = Objects.requireNonNull(
                config.getConfigurationSection("sounds"),
                "sounds section in " + fileName + " is null"
        );

        CustomBlockData customBlockData = new CustomBlockData(
                namespacedKey,
                (float) blockSettings.getDouble("dig-speed"),
                blockSettings.getInt("drop.experience"),
                blockSettings.getBoolean("drop.drops-default-item", true),
                ToolType.valueOf(blockSettings.getString("tool.type", "HAND")),
                blockSettings.getBoolean("tool.force", false),
                item.getString("name"),
                item.getInt("custom-model-data"),
                craftNoteBlockData(config),
                craftPlaceableMaterials(config),
                SoundGroup.fromConfigSection(sounds),
                PlacingType.fromValue(config.getString("placing.placing-type")),
                craftBlockFaceMap(config),
                craftBlockAxisMap(config),
                config.getBoolean("craft.show-in-crafts-menu", false),
                null
        );

        customBlockData.file = file;
        customBlockData.config = config;

        if (config.getConfigurationSection("craft") != null) {
            MSBlock.getCache().recipeBlocks.add(customBlockData);
        }

        return customBlockData;
    }

    public void setNamespacedKey(@NotNull NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public @NotNull NamespacedKey getNamespacedKey() {
        return this.namespacedKey;
    }

    public void setDigSpeed(float digSpeed) {
        this.digSpeed = digSpeed;
    }

    public float getDigSpeed() {
        return this.digSpeed;
    }

    public void setExpToDrop(int expToDrop) {
        this.expToDrop = expToDrop;
    }

    public int getExpToDrop() {
        return this.expToDrop;
    }

    public void setDropsDefaultItem(boolean dropsDefaultItem) {
        this.dropsDefaultItem = dropsDefaultItem;
    }

    public boolean isDropsDefaultItem() {
        return this.dropsDefaultItem;
    }

    public void setToolType(@NotNull ToolType toolType) {
        this.toolType = toolType;
    }

    public @NotNull ToolType getToolType() {
        return this.toolType;
    }

    public void setForceTool(boolean forceTool) {
        this.forceTool = forceTool;
    }

    public boolean isForceTool() {
        return this.forceTool;
    }

    public void setItemName(@Nullable String itemName) {
        this.itemName = itemName;
    }

    public @Nullable String getItemName() {
        return this.itemName;
    }

    public void setItemCustomModelData(int itemCustomModelData) {
        this.itemCustomModelData = itemCustomModelData;
    }

    public int getItemCustomModelData() {
        return this.itemCustomModelData;
    }

    public void setNoteBlockData(@Nullable NoteBlockData noteBlockData) {
        this.noteBlockData = noteBlockData;
    }

    public @Nullable NoteBlockData getNoteBlockData() {
        return this.noteBlockData;
    }

    public void setPlaceableMaterials(@Nullable Set<Material> placeableMaterials) {
        this.placeableMaterials = placeableMaterials;
    }

    public @Nullable Set<Material> getPlaceableMaterials() {
        return this.placeableMaterials;
    }

    public void setSoundGroup(@NotNull SoundGroup soundGroup) {
        this.soundGroup = soundGroup;
    }

    public @NotNull SoundGroup getSoundGroup() {
        return this.soundGroup;
    }

    public void setPlacingType(@Nullable PlacingType placingType) {
        this.placingType = placingType;
    }

    public @Nullable PlacingType getPlacingType() {
        return this.placingType;
    }

    public void setBlockFaceMap(@Nullable Map<BlockFace, NoteBlockData> blockFaceMap) {
        this.blockFaceMap = blockFaceMap;
    }

    public @Nullable Map<BlockFace, NoteBlockData> getBlockFaceMap() {
        return this.blockFaceMap;
    }

    public void setBlockAxisMap(@Nullable Map<Axis, NoteBlockData> blockAxisMap) {
        this.blockAxisMap = blockAxisMap;
    }

    public @Nullable Map<Axis, NoteBlockData> getBlockAxisMap() {
        return this.blockAxisMap;
    }

    public void setShowInCraftsMenu(boolean showInCraftsMenu) {
        this.showInCraftsMenu = showInCraftsMenu;
    }

    public boolean isShowInCraftsMenu() {
        return this.showInCraftsMenu;
    }

    public void setShapedRecipe(@Nullable ShapedRecipe shapedRecipe) {
        this.shapedRecipe = shapedRecipe;
    }

    public @Nullable ShapedRecipe getShapedRecipe() {
        return this.shapedRecipe;
    }

    public float calculateDigSpeed(@NotNull Player player) {
        float base = 1.0f;
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        Material material = itemInMainHand.getType();
        PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.FAST_DIGGING);

        if (this.toolType == ToolType.fromMaterial(material)) {
            base = ToolTier.fromMaterial(material).getDigSpeed();

            if (itemInMainHand.containsEnchantment(Enchantment.DIG_SPEED)) {
                base += itemInMainHand.getEnchantmentLevel(Enchantment.DIG_SPEED) * 0.3f;
            }
        } else if (this.toolType == ToolType.PICKAXE) {
            base /= 30.0f;
        } else {
            base /= 5.0f;
        }

        if (potionEffect != null) {
            base *= (potionEffect.getAmplifier() + 1) * 0.32f;
        }

        return base / this.digSpeed;
    }

    public @NotNull ItemStack craftItemStack() {
        if (this.itemCustomModelData == 0) {
            return new ItemStack(Material.NOTE_BLOCK);
        }

        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(
                CustomBlockRegistry.TYPE_NAMESPACED_KEY,
                PersistentDataType.STRING,
                this.namespacedKey.getKey()
        );
        itemMeta.setCustomModelData(this.itemCustomModelData);

        if (this.itemName != null) {
            itemMeta.displayName(ChatUtils.createDefaultStyledText(this.itemName));
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void registerRecipes() {
        var ingredientMap = new HashMap<Character, ItemStack>();
        ConfigurationSection craftSection = this.config.getConfigurationSection("craft.material-list");

        if (craftSection != null) {
            for (String key : craftSection.getKeys(false)) {
                ItemStack itemStack;
                String itemStr = craftSection.getString(key);

                try {
                    itemStack = new ItemStack(Material.valueOf(itemStr));
                } catch (IllegalArgumentException ignored) {
                    itemStack = MSCustomUtils.getItemStack(itemStr).orElse(null);
                }

                ingredientMap.put(key.toCharArray()[0], itemStack);
            }

            ItemStack craftedItem = this.craftItemStack().clone();
            craftedItem.setAmount(this.config.getInt("craft.item-amount", 1));

            ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey, craftedItem);
            shapedRecipe.setGroup(MSBlock.getInstance().getName().toLowerCase(Locale.ROOT) + this.config.getString("craft.group"));
            shapedRecipe.shape(this.config.getStringList("craft.shaped-recipe").toArray(String[]::new));

            ingredientMap.keySet().forEach(character -> shapedRecipe.setIngredient(character, ingredientMap.get(character)));

            if (this.isShowInCraftsMenu()) {
                CustomBlockRegistry.registerRecipe(shapedRecipe);
            }

            Bukkit.addRecipe(shapedRecipe);
            this.setShapedRecipe(shapedRecipe);
        }
    }

    public @Nullable Set<Axis> getAxes() {
        return this.blockAxisMap == null ? null : this.blockAxisMap.keySet();
    }

    public @Nullable Set<BlockFace> getFaces() {
        return this.blockFaceMap == null ? null : this.blockFaceMap.keySet();
    }

    @Override
    public @NotNull CustomBlockData clone() {
        try {
            return (CustomBlockData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private static @Nullable Set<Material> craftPlaceableMaterials(@NotNull YamlConfiguration config) {
        var placeableMaterials = new HashSet<Material>();

        for (String material : config.getStringList("placing.placeable-materials")) {
            placeableMaterials.add(Material.valueOf(material));
        }

        return placeableMaterials.isEmpty() ? null : placeableMaterials;
    }

    @Contract("_ -> new")
    private static @Nullable Map<BlockFace, NoteBlockData> craftBlockFaceMap(@NotNull YamlConfiguration config) {
        var blockFaceMap = new HashMap<BlockFace, NoteBlockData>();
        ConfigurationSection configurationSection = config.getConfigurationSection("placing.directional.block-faces");

        if (configurationSection == null) return null;

        for (String blockFace : configurationSection.getKeys(false)) {
            blockFaceMap.put(
                    BlockFace.valueOf(blockFace.toUpperCase(Locale.ROOT)),
                    new NoteBlockData(
                            Instrument.valueOf(config.getString("placing.directional.block-faces." + blockFace + ".instrument")),
                            new Note(config.getInt("placing.directional.block-faces." + blockFace + ".note")),
                            config.getBoolean("placing.directional.block-faces." + blockFace + ".is-powered", false)
                    )
            );
        }

        return blockFaceMap.isEmpty() ? null : blockFaceMap;
    }

    private static @Nullable Map<Axis, NoteBlockData> craftBlockAxisMap(@NotNull YamlConfiguration config) {
        var blockAxisMap = new HashMap<Axis, NoteBlockData>();
        ConfigurationSection configurationSection = config.getConfigurationSection("placing.orientable.axes");

        if (configurationSection == null) return null;

        for (String axis : configurationSection.getKeys(false)) {
            blockAxisMap.put(
                    Axis.valueOf(axis.toUpperCase(Locale.ROOT)),
                    new NoteBlockData(
                            Instrument.valueOf(config.getString("placing.orientable.axes." + axis + ".instrument")),
                            new Note(config.getInt("placing.orientable.axes." + axis + ".note")),
                            config.getBoolean("placing.orientable.axes." + axis + ".is-powered", false)
                    )
            );
        }

        return blockAxisMap.isEmpty() ? null : blockAxisMap;
    }

    @Contract("_ -> new")
    private static @Nullable NoteBlockData craftNoteBlockData(@NotNull YamlConfiguration config) {
        String instrument = config.getString("placing.normal.instrument");
        return instrument == null
                ? null
                : new NoteBlockData(
                Instrument.valueOf(instrument),
                new Note(config.getInt("placing.normal.note")),
                config.getBoolean("placing.normal.is-powered", false)
        );
    }

    public enum PlacingType {
        BY_BLOCK_FACE, BY_EYE_POSITION;

        public static @Nullable PlacingType fromValue(@Nullable String value) {
            if (value == null) return null;
            try {
                return PlacingType.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
