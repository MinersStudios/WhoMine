package com.minersstudios.mscore.util;

import com.minersstudios.msblock.Config;
import com.minersstudios.msblock.MSBlock;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a group of sounds associated with a custom block
 */
public final class SoundGroup implements Cloneable {
    private Sound placeSound;
    private Sound breakSound;
    private Sound hitSound;
    private Sound stepSound;

    public static final SoundGroup EMPTY = new SoundGroup(Sound.create("intentionally_empty", SoundCategory.BLOCKS), Sound.create("intentionally_empty", SoundCategory.BLOCKS), Sound.create("intentionally_empty", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("intentionally_empty", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WOOD = new SoundGroup(Sound.create("block.wood.place", SoundCategory.BLOCKS), Sound.create("block.wood.break", SoundCategory.BLOCKS), Sound.create("block.wood.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.wood.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GRAVEL = new SoundGroup(Sound.create("block.gravel.place", SoundCategory.BLOCKS), Sound.create("block.gravel.break", SoundCategory.BLOCKS), Sound.create("block.gravel.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.gravel.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GRASS = new SoundGroup(Sound.create("block.grass.place", SoundCategory.BLOCKS), Sound.create("block.grass.break", SoundCategory.BLOCKS), Sound.create("block.grass.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.grass.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup STONE = new SoundGroup(Sound.create("block.stone.place", SoundCategory.BLOCKS), Sound.create("block.stone.break", SoundCategory.BLOCKS), Sound.create("block.stone.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.stone.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup METAL = new SoundGroup(Sound.create("block.metal.place", SoundCategory.BLOCKS), Sound.create("block.metal.break", SoundCategory.BLOCKS), Sound.create("block.metal.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.metal.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GLASS = new SoundGroup(Sound.create("block.glass.place", SoundCategory.BLOCKS), Sound.create("block.glass.break", SoundCategory.BLOCKS), Sound.create("block.glass.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.glass.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LILY_PAD = new SoundGroup(Sound.create("block.lily_pad.place", SoundCategory.BLOCKS), Sound.create("block.lily_pad.break", SoundCategory.BLOCKS), Sound.create("block.lily_pad.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.lily_pad.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WOOL = new SoundGroup(Sound.create("block.wool.place", SoundCategory.BLOCKS), Sound.create("block.wool.break", SoundCategory.BLOCKS), Sound.create("block.wool.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.wool.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SAND = new SoundGroup(Sound.create("block.sand.place", SoundCategory.BLOCKS), Sound.create("block.sand.break", SoundCategory.BLOCKS), Sound.create("block.sand.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.sand.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SNOW = new SoundGroup(Sound.create("block.snow.place", SoundCategory.BLOCKS), Sound.create("block.snow.break", SoundCategory.BLOCKS), Sound.create("block.snow.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.snow.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup POWDER_SNOW = new SoundGroup(Sound.create("block.powder_snow.place", SoundCategory.BLOCKS), Sound.create("block.powder_snow.break", SoundCategory.BLOCKS), Sound.create("block.powder_snow.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.powder_snow.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LADDER = new SoundGroup(Sound.create("block.ladder.place", SoundCategory.BLOCKS), Sound.create("block.ladder.break", SoundCategory.BLOCKS), Sound.create("block.ladder.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.ladder.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup ANVIL = new SoundGroup(Sound.create("block.anvil.place", SoundCategory.BLOCKS, 0.3f, 1.0f), Sound.create("block.anvil.break", SoundCategory.BLOCKS, 0.3f, 1.0f), Sound.create("block.anvil.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.anvil.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SLIME_BLOCK = new SoundGroup(Sound.create("block.slime_block.place", SoundCategory.BLOCKS), Sound.create("block.slime_block.break", SoundCategory.BLOCKS), Sound.create("block.slime_block.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.slime_block.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup HONEY_BLOCK = new SoundGroup(Sound.create("block.honey_block.place", SoundCategory.BLOCKS), Sound.create("block.honey_block.break", SoundCategory.BLOCKS), Sound.create("block.honey_block.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.honey_block.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WET_GRASS = new SoundGroup(Sound.create("block.wet_grass.place", SoundCategory.BLOCKS), Sound.create("block.wet_grass.break", SoundCategory.BLOCKS), Sound.create("block.wet_grass.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.wet_grass.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CORAL_BLOCK = new SoundGroup(Sound.create("block.coral_block.place", SoundCategory.BLOCKS), Sound.create("block.coral_block.break", SoundCategory.BLOCKS), Sound.create("block.coral_block.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.coral_block.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BAMBOO = new SoundGroup(Sound.create("block.bamboo.place", SoundCategory.BLOCKS), Sound.create("block.bamboo.break", SoundCategory.BLOCKS), Sound.create("block.bamboo.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.bamboo.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BAMBOO_SAPLING = new SoundGroup(Sound.create("block.bamboo_sapling.place", SoundCategory.BLOCKS), Sound.create("block.bamboo_sapling.break", SoundCategory.BLOCKS), Sound.create("block.bamboo_sapling.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.bamboo_sapling.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCAFFOLDING = new SoundGroup(Sound.create("block.scaffolding.place", SoundCategory.BLOCKS), Sound.create("block.scaffolding.break", SoundCategory.BLOCKS), Sound.create("block.scaffolding.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.scaffolding.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SWEET_BERRY_BUSH = new SoundGroup(Sound.create("block.sweet_berry_bush.place", SoundCategory.BLOCKS), Sound.create("block.sweet_berry_bush.break", SoundCategory.BLOCKS), Sound.create("block.sweet_berry_bush.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.sweet_berry_bush.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CROP = new SoundGroup(Sound.create("block.crop.place", SoundCategory.BLOCKS), Sound.create("block.crop.break", SoundCategory.BLOCKS), Sound.create("block.crop.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.crop.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup HARD_CROP = new SoundGroup(Sound.create("block.hard_crop.place", SoundCategory.BLOCKS), Sound.create("block.hard_crop.break", SoundCategory.BLOCKS), Sound.create("block.hard_crop.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.hard_crop.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup VINE = new SoundGroup(Sound.create("block.vine.place", SoundCategory.BLOCKS), Sound.create("block.vine.break", SoundCategory.BLOCKS), Sound.create("block.vine.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.vine.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_WART = new SoundGroup(Sound.create("block.nether_wart.place", SoundCategory.BLOCKS), Sound.create("block.nether_wart.break", SoundCategory.BLOCKS), Sound.create("block.nether_wart.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_wart.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LANTERN = new SoundGroup(Sound.create("block.lantern.place", SoundCategory.BLOCKS), Sound.create("block.lantern.break", SoundCategory.BLOCKS), Sound.create("block.lantern.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.lantern.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup STEM = new SoundGroup(Sound.create("block.stem.place", SoundCategory.BLOCKS), Sound.create("block.stem.break", SoundCategory.BLOCKS), Sound.create("block.stem.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.stem.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NYLIUM = new SoundGroup(Sound.create("block.nylium.place", SoundCategory.BLOCKS), Sound.create("block.nylium.break", SoundCategory.BLOCKS), Sound.create("block.nylium.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.nylium.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup FUNGUS = new SoundGroup(Sound.create("block.fungus.place", SoundCategory.BLOCKS), Sound.create("block.fungus.break", SoundCategory.BLOCKS), Sound.create("block.fungus.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.fungus.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup ROOTS = new SoundGroup(Sound.create("block.roots.place", SoundCategory.BLOCKS), Sound.create("block.roots.break", SoundCategory.BLOCKS), Sound.create("block.roots.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.roots.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SHROOMLIGHT = new SoundGroup(Sound.create("block.shroomlight.place", SoundCategory.BLOCKS), Sound.create("block.shroomlight.break", SoundCategory.BLOCKS), Sound.create("block.shroomlight.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.shroomlight.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WEEPING_VINES = new SoundGroup(Sound.create("block.weeping_vines.place", SoundCategory.BLOCKS), Sound.create("block.weeping_vines.break", SoundCategory.BLOCKS), Sound.create("block.weeping_vines.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.weeping_vines.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup TWISTING_VINES = new SoundGroup(Sound.create("block.twisting_vines.place", SoundCategory.BLOCKS), Sound.create("block.twisting_vines.break", SoundCategory.BLOCKS), Sound.create("block.twisting_vines.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.twisting_vines.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SOUL_SAND = new SoundGroup(Sound.create("block.soul_sand.place", SoundCategory.BLOCKS), Sound.create("block.soul_sand.break", SoundCategory.BLOCKS), Sound.create("block.soul_sand.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.soul_sand.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SOUL_SOIL = new SoundGroup(Sound.create("block.soul_soil.place", SoundCategory.BLOCKS), Sound.create("block.soul_soil.break", SoundCategory.BLOCKS), Sound.create("block.soul_soil.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.soul_soil.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BASALT = new SoundGroup(Sound.create("block.basalt.place", SoundCategory.BLOCKS), Sound.create("block.basalt.break", SoundCategory.BLOCKS), Sound.create("block.basalt.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.basalt.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WART_BLOCK = new SoundGroup(Sound.create("block.wart_block.place", SoundCategory.BLOCKS), Sound.create("block.wart_block.break", SoundCategory.BLOCKS), Sound.create("block.wart_block.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.wart_block.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHERRACK = new SoundGroup(Sound.create("block.netherrack.place", SoundCategory.BLOCKS), Sound.create("block.netherrack.break", SoundCategory.BLOCKS), Sound.create("block.netherrack.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.netherrack.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_BRICKS = new SoundGroup(Sound.create("block.nether_bricks.place", SoundCategory.BLOCKS), Sound.create("block.nether_bricks.break", SoundCategory.BLOCKS), Sound.create("block.nether_bricks.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_bricks.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_SPROUTS = new SoundGroup(Sound.create("block.nether_sprouts.place", SoundCategory.BLOCKS), Sound.create("block.nether_sprouts.break", SoundCategory.BLOCKS), Sound.create("block.nether_sprouts.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_sprouts.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_ORE = new SoundGroup(Sound.create("block.nether_ore.place", SoundCategory.BLOCKS), Sound.create("block.nether_ore.break", SoundCategory.BLOCKS), Sound.create("block.nether_ore.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_ore.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BONE_BLOCK = new SoundGroup(Sound.create("block.bone_block.place", SoundCategory.BLOCKS), Sound.create("block.bone_block.break", SoundCategory.BLOCKS), Sound.create("block.bone_block.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.bone_block.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHERITE_BLOCK = new SoundGroup(Sound.create("block.netherite_block.place", SoundCategory.BLOCKS), Sound.create("block.netherite_block.break", SoundCategory.BLOCKS), Sound.create("block.netherite_block.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.netherite_block.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup ANCIENT_DEBRIS = new SoundGroup(Sound.create("block.ancient_debris.place", SoundCategory.BLOCKS), Sound.create("block.ancient_debris.break", SoundCategory.BLOCKS), Sound.create("block.ancient_debris.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.ancient_debris.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LODESTONE = new SoundGroup(Sound.create("block.lodestone.place", SoundCategory.BLOCKS), Sound.create("block.lodestone.break", SoundCategory.BLOCKS), Sound.create("block.lodestone.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.lodestone.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHAIN = new SoundGroup(Sound.create("block.chain.place", SoundCategory.BLOCKS), Sound.create("block.chain.break", SoundCategory.BLOCKS), Sound.create("block.chain.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.chain.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_GOLD_ORE = new SoundGroup(Sound.create("block.nether_gold_ore.place", SoundCategory.BLOCKS), Sound.create("block.nether_gold_ore.break", SoundCategory.BLOCKS), Sound.create("block.nether_gold_ore.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_gold_ore.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GILDED_BLACKSTONE = new SoundGroup(Sound.create("block.gilded_blackstone.place", SoundCategory.BLOCKS), Sound.create("block.gilded_blackstone.break", SoundCategory.BLOCKS), Sound.create("block.gilded_blackstone.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.gilded_blackstone.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CANDLE = new SoundGroup(Sound.create("block.candle.place", SoundCategory.BLOCKS), Sound.create("block.candle.break", SoundCategory.BLOCKS), Sound.create("block.candle.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.candle.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup AMETHYST = new SoundGroup(Sound.create("block.amethyst.place", SoundCategory.BLOCKS), Sound.create("block.amethyst.break", SoundCategory.BLOCKS), Sound.create("block.amethyst.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.amethyst.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup AMETHYST_CLUSTER = new SoundGroup(Sound.create("block.amethyst_cluster.place", SoundCategory.BLOCKS), Sound.create("block.amethyst_cluster.break", SoundCategory.BLOCKS), Sound.create("block.amethyst_cluster.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.amethyst_cluster.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SMALL_AMETHYST_BUD = new SoundGroup(Sound.create("block.small_amethyst_bud.place", SoundCategory.BLOCKS), Sound.create("block.small_amethyst_bud.break", SoundCategory.BLOCKS), Sound.create("block.small_amethyst_bud.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.small_amethyst_bud.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MEDIUM_AMETHYST_BUD = new SoundGroup(Sound.create("block.medium_amethyst_bud.place", SoundCategory.BLOCKS), Sound.create("block.medium_amethyst_bud.break", SoundCategory.BLOCKS), Sound.create("block.medium_amethyst_bud.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.medium_amethyst_bud.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LARGE_AMETHYST_BUD = new SoundGroup(Sound.create("block.large_amethyst_bud.place", SoundCategory.BLOCKS), Sound.create("block.large_amethyst_bud.break", SoundCategory.BLOCKS), Sound.create("block.large_amethyst_bud.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.large_amethyst_bud.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup TUFF = new SoundGroup(Sound.create("block.tuff.place", SoundCategory.BLOCKS), Sound.create("block.tuff.break", SoundCategory.BLOCKS), Sound.create("block.tuff.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.tuff.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CALCITE = new SoundGroup(Sound.create("block.calcite.place", SoundCategory.BLOCKS), Sound.create("block.calcite.break", SoundCategory.BLOCKS), Sound.create("block.calcite.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.calcite.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DRIPSTONE_BLOCK = new SoundGroup(Sound.create("block.dripstone_block.place", SoundCategory.BLOCKS), Sound.create("block.dripstone_block.break", SoundCategory.BLOCKS), Sound.create("block.dripstone_block.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.dripstone_block.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup POINTED_DRIPSTONE = new SoundGroup(Sound.create("block.pointed_dripstone.place", SoundCategory.BLOCKS), Sound.create("block.pointed_dripstone.break", SoundCategory.BLOCKS), Sound.create("block.pointed_dripstone.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.pointed_dripstone.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup COPPER = new SoundGroup(Sound.create("block.copper.place", SoundCategory.BLOCKS), Sound.create("block.copper.break", SoundCategory.BLOCKS), Sound.create("block.copper.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.copper.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CAVE_VINES = new SoundGroup(Sound.create("block.cave_vines.place", SoundCategory.BLOCKS), Sound.create("block.cave_vines.break", SoundCategory.BLOCKS), Sound.create("block.cave_vines.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.cave_vines.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SPORE_BLOSSOM = new SoundGroup(Sound.create("block.spore_blossom.place", SoundCategory.BLOCKS), Sound.create("block.spore_blossom.break", SoundCategory.BLOCKS), Sound.create("block.spore_blossom.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.spore_blossom.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup AZALEA = new SoundGroup(Sound.create("block.azalea.place", SoundCategory.BLOCKS), Sound.create("block.azalea.break", SoundCategory.BLOCKS), Sound.create("block.azalea.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.azalea.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup FLOWERING_AZALEA = new SoundGroup(Sound.create("block.flowering_azalea.place", SoundCategory.BLOCKS), Sound.create("block.flowering_azalea.break", SoundCategory.BLOCKS), Sound.create("block.flowering_azalea.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.flowering_azalea.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MOSS_CARPET = new SoundGroup(Sound.create("block.moss_carpet.place", SoundCategory.BLOCKS), Sound.create("block.moss_carpet.break", SoundCategory.BLOCKS), Sound.create("block.moss_carpet.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.moss_carpet.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup PINK_PETALS = new SoundGroup(Sound.create("block.pink_petals.place", SoundCategory.BLOCKS), Sound.create("block.pink_petals.break", SoundCategory.BLOCKS), Sound.create("block.pink_petals.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.pink_petals.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MOSS = new SoundGroup(Sound.create("block.moss.place", SoundCategory.BLOCKS), Sound.create("block.moss.break", SoundCategory.BLOCKS), Sound.create("block.moss.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.moss.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BIG_DRIPLEAF = new SoundGroup(Sound.create("block.big_dripleaf.place", SoundCategory.BLOCKS), Sound.create("block.big_dripleaf.break", SoundCategory.BLOCKS), Sound.create("block.big_dripleaf.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.big_dripleaf.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SMALL_DRIPLEAF = new SoundGroup(Sound.create("block.small_dripleaf.place", SoundCategory.BLOCKS), Sound.create("block.small_dripleaf.break", SoundCategory.BLOCKS), Sound.create("block.small_dripleaf.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.small_dripleaf.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup ROOTED_DIRT = new SoundGroup(Sound.create("block.rooted_dirt.place", SoundCategory.BLOCKS), Sound.create("block.rooted_dirt.break", SoundCategory.BLOCKS), Sound.create("block.rooted_dirt.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.rooted_dirt.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup HANGING_ROOTS = new SoundGroup(Sound.create("block.hanging_roots.place", SoundCategory.BLOCKS), Sound.create("block.hanging_roots.break", SoundCategory.BLOCKS), Sound.create("block.hanging_roots.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.hanging_roots.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup AZALEA_LEAVES = new SoundGroup(Sound.create("block.azalea_leaves.place", SoundCategory.BLOCKS), Sound.create("block.azalea_leaves.break", SoundCategory.BLOCKS), Sound.create("block.azalea_leaves.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.azalea_leaves.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK_SENSOR = new SoundGroup(Sound.create("block.sculk_sensor.place", SoundCategory.BLOCKS), Sound.create("block.sculk_sensor.break", SoundCategory.BLOCKS), Sound.create("block.sculk_sensor.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk_sensor.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK_CATALYST = new SoundGroup(Sound.create("block.sculk_catalyst.place", SoundCategory.BLOCKS), Sound.create("block.sculk_catalyst.break", SoundCategory.BLOCKS), Sound.create("block.sculk_catalyst.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk_catalyst.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK = new SoundGroup(Sound.create("block.sculk.place", SoundCategory.BLOCKS), Sound.create("block.sculk.break", SoundCategory.BLOCKS), Sound.create("block.sculk.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK_VEIN = new SoundGroup(Sound.create("block.sculk_vein.place", SoundCategory.BLOCKS), Sound.create("block.sculk_vein.break", SoundCategory.BLOCKS), Sound.create("block.sculk_vein.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk_vein.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK_SHRIEKER = new SoundGroup(Sound.create("block.sculk_shrieker.place", SoundCategory.BLOCKS), Sound.create("block.sculk_shrieker.break", SoundCategory.BLOCKS), Sound.create("block.sculk_shrieker.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk_shrieker.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GLOW_LICHEN = new SoundGroup(Sound.create("block.glow_lichen.place", SoundCategory.BLOCKS), Sound.create("block.glow_lichen.break", SoundCategory.BLOCKS), Sound.create("block.glow_lichen.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.glow_lichen.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DEEPSLATE = new SoundGroup(Sound.create("block.deepslate.place", SoundCategory.BLOCKS), Sound.create("block.deepslate.break", SoundCategory.BLOCKS), Sound.create("block.deepslate.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.deepslate.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DEEPSLATE_BRICKS = new SoundGroup(Sound.create("block.deepslate_bricks.place", SoundCategory.BLOCKS), Sound.create("block.deepslate_bricks.break", SoundCategory.BLOCKS), Sound.create("block.deepslate_bricks.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.deepslate_bricks.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DEEPSLATE_TILES = new SoundGroup(Sound.create("block.deepslate_tiles.place", SoundCategory.BLOCKS), Sound.create("block.deepslate_tiles.break", SoundCategory.BLOCKS), Sound.create("block.deepslate_tiles.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.deepslate_tiles.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup POLISHED_DEEPSLATE = new SoundGroup(Sound.create("block.polished_deepslate.place", SoundCategory.BLOCKS), Sound.create("block.polished_deepslate.break", SoundCategory.BLOCKS), Sound.create("block.polished_deepslate.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.polished_deepslate.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup FROGLIGHT = new SoundGroup(Sound.create("block.froglight.place", SoundCategory.BLOCKS), Sound.create("block.froglight.break", SoundCategory.BLOCKS), Sound.create("block.froglight.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.froglight.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup FROGSPAWN = new SoundGroup(Sound.create("block.frogspawn.place", SoundCategory.BLOCKS), Sound.create("block.frogspawn.break", SoundCategory.BLOCKS), Sound.create("block.frogspawn.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.frogspawn.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MANGROVE_ROOTS = new SoundGroup(Sound.create("block.mangrove_roots.place", SoundCategory.BLOCKS), Sound.create("block.mangrove_roots.break", SoundCategory.BLOCKS), Sound.create("block.mangrove_roots.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.mangrove_roots.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MUDDY_MANGROVE_ROOTS = new SoundGroup(Sound.create("block.muddy_mangrove_roots.place", SoundCategory.BLOCKS), Sound.create("block.muddy_mangrove_roots.break", SoundCategory.BLOCKS), Sound.create("block.muddy_mangrove_roots.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.muddy_mangrove_roots.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MUD = new SoundGroup(Sound.create("block.mud.place", SoundCategory.BLOCKS), Sound.create("block.mud.break", SoundCategory.BLOCKS), Sound.create("block.mud.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.mud.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MUD_BRICKS = new SoundGroup(Sound.create("block.mud_bricks.place", SoundCategory.BLOCKS), Sound.create("block.mud_bricks.break", SoundCategory.BLOCKS), Sound.create("block.mud_bricks.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.mud_bricks.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup PACKED_MUD = new SoundGroup(Sound.create("block.packed_mud.place", SoundCategory.BLOCKS), Sound.create("block.packed_mud.break", SoundCategory.BLOCKS), Sound.create("block.packed_mud.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.packed_mud.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup HANGING_SIGN = new SoundGroup(Sound.create("block.hanging_sign.place", SoundCategory.BLOCKS), Sound.create("block.hanging_sign.break", SoundCategory.BLOCKS), Sound.create("block.hanging_sign.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.hanging_sign.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_WOOD_HANGING_SIGN = new SoundGroup(Sound.create("block.nether_wood_hanging_sign.place", SoundCategory.BLOCKS), Sound.create("block.nether_wood_hanging_sign.break", SoundCategory.BLOCKS), Sound.create("block.nether_wood_hanging_sign.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_wood_hanging_sign.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BAMBOO_WOOD_HANGING_SIGN = new SoundGroup(Sound.create("block.bamboo_wood_hanging_sign.place", SoundCategory.BLOCKS), Sound.create("block.bamboo_wood_hanging_sign.break", SoundCategory.BLOCKS), Sound.create("block.bamboo_wood_hanging_sign.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.bamboo_wood_hanging_sign.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BAMBOO_WOOD = new SoundGroup(Sound.create("block.bamboo_wood.place", SoundCategory.BLOCKS), Sound.create("block.bamboo_wood.break", SoundCategory.BLOCKS), Sound.create("block.bamboo_wood.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.bamboo_wood.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_WOOD = new SoundGroup(Sound.create("block.nether_wood.place", SoundCategory.BLOCKS), Sound.create("block.nether_wood.break", SoundCategory.BLOCKS), Sound.create("block.nether_wood.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_wood.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHERRY_WOOD = new SoundGroup(Sound.create("block.cherry_wood.place", SoundCategory.BLOCKS), Sound.create("block.cherry_wood.break", SoundCategory.BLOCKS), Sound.create("block.cherry_wood.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.cherry_wood.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHERRY_SAPLING = new SoundGroup(Sound.create("block.cherry_sapling.place", SoundCategory.BLOCKS), Sound.create("block.cherry_sapling.break", SoundCategory.BLOCKS), Sound.create("block.cherry_sapling.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.cherry_sapling.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHERRY_LEAVES = new SoundGroup(Sound.create("block.cherry_leaves.place", SoundCategory.BLOCKS), Sound.create("block.cherry_leaves.break", SoundCategory.BLOCKS), Sound.create("block.cherry_leaves.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.cherry_leaves.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHERRY_WOOD_HANGING_SIGN = new SoundGroup(Sound.create("block.cherry_wood_hanging_sign.place", SoundCategory.BLOCKS), Sound.create("block.cherry_wood_hanging_sign.break", SoundCategory.BLOCKS), Sound.create("block.cherry_wood_hanging_sign.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.cherry_wood_hanging_sign.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHISELED_BOOKSHELF = new SoundGroup(Sound.create("block.chiseled_bookshelf.place", SoundCategory.BLOCKS), Sound.create("block.chiseled_bookshelf.break", SoundCategory.BLOCKS), Sound.create("block.chiseled_bookshelf.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.chiseled_bookshelf.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SUSPICIOUS_SAND = new SoundGroup(Sound.create("block.suspicious_sand.place", SoundCategory.BLOCKS), Sound.create("block.suspicious_sand.break", SoundCategory.BLOCKS), Sound.create("block.suspicious_sand.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.suspicious_sand.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SUSPICIOUS_GRAVEL = new SoundGroup(Sound.create("block.suspicious_gravel.place", SoundCategory.BLOCKS), Sound.create("block.suspicious_gravel.break", SoundCategory.BLOCKS), Sound.create("block.suspicious_gravel.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.suspicious_gravel.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DECORATED_POT = new SoundGroup(Sound.create("block.decorated_pot.place", SoundCategory.BLOCKS), Sound.create("block.decorated_pot.break", SoundCategory.BLOCKS), Sound.create("block.decorated_pot.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.decorated_pot.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DECORATED_POT_CRACKED = new SoundGroup(Sound.create("block.decorated_pot_cracked.place", SoundCategory.BLOCKS), Sound.create("block.decorated_pot_cracked.break", SoundCategory.BLOCKS), Sound.create("block.decorated_pot_cracked.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.decorated_pot_cracked.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SPONGE = new SoundGroup(Sound.create("block.sponge.place", SoundCategory.BLOCKS), Sound.create("block.sponge.break", SoundCategory.BLOCKS), Sound.create("block.sponge.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.sponge.step", SoundCategory.PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WET_SPONGE = new SoundGroup(Sound.create("block.wet_sponge.place", SoundCategory.BLOCKS), Sound.create("block.wet_sponge.break", SoundCategory.BLOCKS), Sound.create("block.wet_sponge.hit", SoundCategory.BLOCKS, 0.5f, 0.5f), Sound.create("block.wet_sponge.step", SoundCategory.PLAYERS, 0.3f, 0.9f));

    /**
     * Constructs a SoundGroup with the specified sounds
     *
     * @param placeSound The place sound
     * @param breakSound The break sound
     * @param hitSound   The hit sound
     * @param stepSound  The step sound
     */
    public SoundGroup(
            final @Nullable Sound placeSound,
            final @Nullable Sound breakSound,
            final @Nullable Sound hitSound,
            final @Nullable Sound stepSound
    ) {
        this.placeSound = placeSound;
        this.breakSound = breakSound;
        this.hitSound = hitSound;
        this.stepSound = stepSound;
    }

    /**
     * @return The place sound of this SoundGroup,
     *         or null if the place sound is not set
     */
    public @Nullable Sound getPlaceSound() {
        return this.placeSound;
    }

    /**
     * @return The break sound of this SoundGroup,
     *         or null if the break sound is not set
     */
    public @Nullable Sound getBreakSound() {
        return this.breakSound;
    }

    /**
     * @return The hit sound of this SoundGroup,
     *         or null if the hit sound is not set
     */
    public @Nullable Sound getHitSound() {
        return this.hitSound;
    }

    /**
     * @return The step sound of this SoundGroup,
     *         or null if the step sound is not set
     */
    public @Nullable Sound getStepSound() {
        return this.stepSound;
    }

    /**
     * Plays the place sound of this SoundGroup at the specified location.
     * If the place sound is "block.wood.place", the wood place sound from
     * the {@link Config} will be played instead. If the place sound is null,
     * nothing will be played.
     *
     * @param location The location to play the place sound at
     */
    public void playPlaceSound(final @NotNull Location location) {
        if (this.placeSound == null) return;
        location.getWorld().playSound(
                location,
                this.placeSound.key.equalsIgnoreCase("block.wood.place")
                ? MSBlock.getConfiguration().woodSoundPlace
                : this.placeSound.key,
                this.placeSound.category,
                this.placeSound.volume,
                this.placeSound.pitch
        );
    }

    /**
     * Plays the break sound of this SoundGroup at the specified location.
     * If the break sound is "block.wood.break", the wood break sound from
     * the {@link Config} will be played instead. If the break sound is null,
     * nothing will be played.
     *
     * @param location The location to play the break sound at
     */
    public void playBreakSound(final @NotNull Location location) {
        if (this.breakSound == null) return;
        location.getWorld().playSound(
                location,
                this.breakSound.key.equalsIgnoreCase("block.wood.break")
                ? MSBlock.getConfiguration().woodSoundBreak
                : this.breakSound.key,
                this.breakSound.category,
                this.breakSound.volume,
                this.breakSound.pitch
        );
    }

    /**
     * Plays the hit sound of this SoundGroup at the specified location.
     * If the hit sound is "block.wood.hit", the wood hit sound from
     * the {@link Config} will be played instead. If the hit sound is null,
     * nothing will be played.
     *
     * @param location The location to play the hit sound at
     */
    public void playHitSound(final @NotNull Location location) {
        if (this.hitSound == null) return;
        location.getWorld().playSound(
                location,
                this.hitSound.key.equalsIgnoreCase("block.wood.hit")
                ? MSBlock.getConfiguration().woodSoundHit
                : this.hitSound.key,
                this.hitSound.category,
                this.hitSound.volume,
                this.hitSound.pitch
        );
    }

    /**
     * Plays the step sound of this SoundGroup at the specified location.
     * If the step sound is "block.wood.step", the wood step sound from
     * the {@link Config} will be played instead. If the step sound is null,
     * nothing will be played.
     *
     * @param location The location to play the step sound at
     */
    public void playStepSound(final @NotNull Location location) {
        if (this.stepSound == null) return;
        location.getWorld().playSound(
                location,
                this.stepSound.key.equalsIgnoreCase("block.wood.step")
                ? MSBlock.getConfiguration().woodSoundStep
                : this.stepSound.key,
                this.stepSound.category,
                this.stepSound.volume,
                this.stepSound.pitch
        );
    }

    /**
     * Creates a clone of this SoundGroup
     * with the same sounds
     *
     * @return A clone of this SoundGroup
     */
    @Override
    public @NotNull SoundGroup clone() {
        try {
            final SoundGroup clone = (SoundGroup) super.clone();

            clone.placeSound = this.placeSound == null ? null : this.placeSound.clone();
            clone.breakSound = this.breakSound == null ? null : this.breakSound.clone();
            clone.hitSound = this.hitSound == null ? null : this.hitSound.clone();
            clone.stepSound = this.stepSound == null ? null : this.stepSound.clone();

            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError("An error occurred while cloning '" + this + "'", e);
        }
    }

    /**
     * @return A string representation of this SoundGroup
     */
    @Override
    public @NotNull String toString() {
        return "SoundGroup{" +
                "placeSound=" + this.placeSound +
                ", breakSound=" + this.breakSound +
                ", hitSound=" + this.hitSound +
                ", stepSound=" + this.stepSound +
                '}';
    }

    /**
     * @return A hash code value for this SoundGroup
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.placeSound, this.breakSound, this.hitSound, this.stepSound);
    }

    /**
     * @param obj The reference object with which to compare
     * @return True if this SoundGroup is the same as the obj argument
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj
                || (
                        obj instanceof SoundGroup soundGroup
                        && Objects.equals(this.placeSound, soundGroup.placeSound)
                        && Objects.equals(this.breakSound, soundGroup.breakSound)
                        && Objects.equals(this.hitSound, soundGroup.hitSound)
                        && Objects.equals(this.stepSound, soundGroup.stepSound)
                );
    }

    /**
     * Represents a sound with :
     * <ul>
     *     <li>A key {@link String}</li>
     *     <li>A {@link SoundCategory}</li>
     *     <li>A volume float</li>
     *     <li>A pitch float</li>
     * </ul>
     */
    public static final class Sound implements Cloneable {
        private String key;
        private SoundCategory category;
        private float volume;
        private float pitch;

        private Sound(
                final @NotNull String key,
                final @NotNull SoundCategory category,
                final float volume,
                final float pitch
        ) {
            this.key = key;
            this.category = category;
            this.volume = volume;
            this.pitch = pitch;
        }

        /**
         * Creates a new Sound instance with 1.0f volume and pitch
         *
         * @param key           The key of the sound
         * @param soundCategory The sound category of the sound
         * @return The created Sound instance,
         *         or null if the key is blank
         * @see #create(String, SoundCategory, float, float)
         */
        public static @Nullable Sound create(
                final @Nullable String key,
                final @NotNull SoundCategory soundCategory
        ) {
            return create(key, soundCategory, 1.0f, 1.0f);
        }

        /**
         * Creates a new Sound instance
         *
         * @param key           The key of the sound
         * @param soundCategory The sound category of the sound
         * @param volume        The volume of the sound
         * @param pitch         The pitch of the sound
         * @return The created Sound instance,
         *         or null if the key is blank
         */
        public static @Nullable Sound create(
                final @Nullable String key,
                final @NotNull SoundCategory soundCategory,
                final float volume,
                final float pitch
        ) {
            return StringUtils.isBlank(key)
                    ? null
                    : new Sound(key, soundCategory, volume, pitch);
        }

        /**
         * @return The key of this Sound
         */
        public @NotNull String key() {
            return this.key;
        }

        /**
         * Sets the key of this Sound
         *
         * @param key The key to set
         * @return This Sound instance with the new key
         */
        public @NotNull Sound key(final @NotNull String key) {
            this.key = key;
            return this;
        }

        /**
         * @return The sound category of this Sound
         */
        public @NotNull SoundCategory category() {
            return this.category;
        }

        /**
         * Sets the sound category of this Sound
         *
         * @param soundCategory The sound category to set
         * @return This Sound instance with the new sound category
         */
        public @NotNull Sound category(final @NotNull SoundCategory soundCategory) {
            this.category = soundCategory;
            return this;
        }

        /**
         * @return The volume of this Sound
         */
        public float volume() {
            return this.volume;
        }

        /**
         * Sets the volume of this Sound
         *
         * @param volume The volume to set
         * @return This Sound instance with the new volume
         */
        public @NotNull Sound volume(final float volume) {
            this.volume = volume;
            return this;
        }

        /**
         * @return The pitch of this Sound
         */
        public float pitch() {
            return this.pitch;
        }

        /**
         * Sets the pitch of this Sound
         *
         * @param pitch The pitch to set
         * @return This Sound instance with the new pitch
         */
        public @NotNull Sound pitch(final float pitch) {
            this.pitch = pitch;
            return this;
        }

        /**
         * Creates a clone of this Sound with the same values
         *
         * @return A clone of this Sound
         */
        @Override
        public @NotNull Sound clone() {
            try {
                return (Sound) super.clone();
            } catch (final CloneNotSupportedException e) {
                throw new AssertionError("An error occurred while cloning '" + this + "'", e);
            }
        }

        /**
         * @return A string representation of this Sound
         */
        @Override
        public @NotNull String toString() {
            return "Sound{" +
                    "key='" + this.key + '\'' +
                    ", soundCategory=" + this.category +
                    ", volume=" + this.volume +
                    ", pitch=" + this.pitch +
                    '}';
        }

        /**
         * @return A hash code value for this Sound
         */
        @Override
        public int hashCode() {
            return Objects.hash(this.key, this.category, this.volume, this.pitch);
        }

        /**
         * @param obj The reference object with which to compare
         * @return True if this Sound is the same as the obj argument
         */
        @Override
        public boolean equals(final Object obj) {
            return this == obj
                    || (
                            obj instanceof Sound sound
                            && Float.compare(sound.volume, this.volume) == 0
                            && Float.compare(sound.pitch, this.pitch) == 0
                            && this.key.equals(sound.key)
                            && this.category == sound.category
                    );
        }
    }
}
