package com.minersstudios.mscore.sound;

import com.minersstudios.msblock.Config;
import com.minersstudios.msblock.MSBlock;
import com.minersstudios.mscore.location.MSPosition;
import com.minersstudios.mscore.utility.ChatUtils;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

import static org.bukkit.SoundCategory.BLOCKS;
import static org.bukkit.SoundCategory.PLAYERS;

/**
 * Represents a group of sounds associated with a custom block
 */
@Immutable
public final class SoundGroup implements Cloneable {
    private Sound placeSound;
    private Sound breakSound;
    private Sound hitSound;
    private Sound stepSound;

    //<editor-fold desc="Vanilla sounds" defaultstate="collapsed">
    public static final SoundGroup EMPTY =                    new SoundGroup(Sound.create("intentionally_empty",                  BLOCKS),             Sound.create("intentionally_empty",                  BLOCKS),             Sound.create("intentionally_empty",                BLOCKS, 0.5f, 0.5f), Sound.create("intentionally_empty",                 PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WOOD =                     new SoundGroup(Sound.create("block.wood.place",                     BLOCKS),             Sound.create("block.wood.break",                     BLOCKS),             Sound.create("block.wood.hit",                     BLOCKS, 0.5f, 0.5f), Sound.create("block.wood.step",                     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GRAVEL =                   new SoundGroup(Sound.create("block.gravel.place",                   BLOCKS),             Sound.create("block.gravel.break",                   BLOCKS),             Sound.create("block.gravel.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.gravel.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GRASS =                    new SoundGroup(Sound.create("block.grass.place",                    BLOCKS),             Sound.create("block.grass.break",                    BLOCKS),             Sound.create("block.grass.hit",                    BLOCKS, 0.5f, 0.5f), Sound.create("block.grass.step",                    PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup STONE =                    new SoundGroup(Sound.create("block.stone.place",                    BLOCKS),             Sound.create("block.stone.break",                    BLOCKS),             Sound.create("block.stone.hit",                    BLOCKS, 0.5f, 0.5f), Sound.create("block.stone.step",                    PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup METAL =                    new SoundGroup(Sound.create("block.metal.place",                    BLOCKS),             Sound.create("block.metal.break",                    BLOCKS),             Sound.create("block.metal.hit",                    BLOCKS, 0.5f, 0.5f), Sound.create("block.metal.step",                    PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GLASS =                    new SoundGroup(Sound.create("block.glass.place",                    BLOCKS),             Sound.create("block.glass.break",                    BLOCKS),             Sound.create("block.glass.hit",                    BLOCKS, 0.5f, 0.5f), Sound.create("block.glass.step",                    PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LILY_PAD =                 new SoundGroup(Sound.create("block.lily_pad.place",                 BLOCKS),             Sound.create("block.lily_pad.break",                 BLOCKS),             Sound.create("block.lily_pad.hit",                 BLOCKS, 0.5f, 0.5f), Sound.create("block.lily_pad.step",                 PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WOOL =                     new SoundGroup(Sound.create("block.wool.place",                     BLOCKS),             Sound.create("block.wool.break",                     BLOCKS),             Sound.create("block.wool.hit",                     BLOCKS, 0.5f, 0.5f), Sound.create("block.wool.step",                     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SAND =                     new SoundGroup(Sound.create("block.sand.place",                     BLOCKS),             Sound.create("block.sand.break",                     BLOCKS),             Sound.create("block.sand.hit",                     BLOCKS, 0.5f, 0.5f), Sound.create("block.sand.step",                     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SNOW =                     new SoundGroup(Sound.create("block.snow.place",                     BLOCKS),             Sound.create("block.snow.break",                     BLOCKS),             Sound.create("block.snow.hit",                     BLOCKS, 0.5f, 0.5f), Sound.create("block.snow.step",                     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup POWDER_SNOW =              new SoundGroup(Sound.create("block.powder_snow.place",              BLOCKS),             Sound.create("block.powder_snow.break",              BLOCKS),             Sound.create("block.powder_snow.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.powder_snow.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LADDER =                   new SoundGroup(Sound.create("block.ladder.place",                   BLOCKS),             Sound.create("block.ladder.break",                   BLOCKS),             Sound.create("block.ladder.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.ladder.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup ANVIL =                    new SoundGroup(Sound.create("block.anvil.place",                    BLOCKS, 0.3f, 1.0f), Sound.create("block.anvil.break",                    BLOCKS, 0.3f, 1.0f), Sound.create("block.anvil.hit",                    BLOCKS, 0.5f, 0.5f), Sound.create("block.anvil.step",                    PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SLIME_BLOCK =              new SoundGroup(Sound.create("block.slime_block.place",              BLOCKS),             Sound.create("block.slime_block.break",              BLOCKS),             Sound.create("block.slime_block.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.slime_block.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup HONEY_BLOCK =              new SoundGroup(Sound.create("block.honey_block.place",              BLOCKS),             Sound.create("block.honey_block.break",              BLOCKS),             Sound.create("block.honey_block.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.honey_block.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WET_GRASS =                new SoundGroup(Sound.create("block.wet_grass.place",                BLOCKS),             Sound.create("block.wet_grass.break",                BLOCKS),             Sound.create("block.wet_grass.hit",                BLOCKS, 0.5f, 0.5f), Sound.create("block.wet_grass.step",                PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CORAL_BLOCK =              new SoundGroup(Sound.create("block.coral_block.place",              BLOCKS),             Sound.create("block.coral_block.break",              BLOCKS),             Sound.create("block.coral_block.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.coral_block.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BAMBOO =                   new SoundGroup(Sound.create("block.bamboo.place",                   BLOCKS),             Sound.create("block.bamboo.break",                   BLOCKS),             Sound.create("block.bamboo.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.bamboo.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BAMBOO_SAPLING =           new SoundGroup(Sound.create("block.bamboo_sapling.place",           BLOCKS),             Sound.create("block.bamboo_sapling.break",           BLOCKS),             Sound.create("block.bamboo_sapling.hit",           BLOCKS, 0.5f, 0.5f), Sound.create("block.bamboo_sapling.step",           PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCAFFOLDING =              new SoundGroup(Sound.create("block.scaffolding.place",              BLOCKS),             Sound.create("block.scaffolding.break",              BLOCKS),             Sound.create("block.scaffolding.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.scaffolding.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SWEET_BERRY_BUSH =         new SoundGroup(Sound.create("block.sweet_berry_bush.place",         BLOCKS),             Sound.create("block.sweet_berry_bush.break",         BLOCKS),             Sound.create("block.sweet_berry_bush.hit",         BLOCKS, 0.5f, 0.5f), Sound.create("block.sweet_berry_bush.step",         PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CROP =                     new SoundGroup(Sound.create("block.crop.place",                     BLOCKS),             Sound.create("block.crop.break",                     BLOCKS),             Sound.create("block.crop.hit",                     BLOCKS, 0.5f, 0.5f), Sound.create("block.crop.step",                     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup HARD_CROP =                new SoundGroup(Sound.create("block.hard_crop.place",                BLOCKS),             Sound.create("block.hard_crop.break",                BLOCKS),             Sound.create("block.hard_crop.hit",                BLOCKS, 0.5f, 0.5f), Sound.create("block.hard_crop.step",                PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup VINE =                     new SoundGroup(Sound.create("block.vine.place",                     BLOCKS),             Sound.create("block.vine.break",                     BLOCKS),             Sound.create("block.vine.hit",                     BLOCKS, 0.5f, 0.5f), Sound.create("block.vine.step",                     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_WART =              new SoundGroup(Sound.create("block.nether_wart.place",              BLOCKS),             Sound.create("block.nether_wart.break",              BLOCKS),             Sound.create("block.nether_wart.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_wart.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LANTERN =                  new SoundGroup(Sound.create("block.lantern.place",                  BLOCKS),             Sound.create("block.lantern.break",                  BLOCKS),             Sound.create("block.lantern.hit",                  BLOCKS, 0.5f, 0.5f), Sound.create("block.lantern.step",                  PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup STEM =                     new SoundGroup(Sound.create("block.stem.place",                     BLOCKS),             Sound.create("block.stem.break",                     BLOCKS),             Sound.create("block.stem.hit",                     BLOCKS, 0.5f, 0.5f), Sound.create("block.stem.step",                     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NYLIUM =                   new SoundGroup(Sound.create("block.nylium.place",                   BLOCKS),             Sound.create("block.nylium.break",                   BLOCKS),             Sound.create("block.nylium.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.nylium.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup FUNGUS =                   new SoundGroup(Sound.create("block.fungus.place",                   BLOCKS),             Sound.create("block.fungus.break",                   BLOCKS),             Sound.create("block.fungus.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.fungus.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup ROOTS =                    new SoundGroup(Sound.create("block.roots.place",                    BLOCKS),             Sound.create("block.roots.break",                    BLOCKS),             Sound.create("block.roots.hit",                    BLOCKS, 0.5f, 0.5f), Sound.create("block.roots.step",                    PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SHROOMLIGHT =              new SoundGroup(Sound.create("block.shroomlight.place",              BLOCKS),             Sound.create("block.shroomlight.break",              BLOCKS),             Sound.create("block.shroomlight.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.shroomlight.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WEEPING_VINES =            new SoundGroup(Sound.create("block.weeping_vines.place",            BLOCKS),             Sound.create("block.weeping_vines.break",            BLOCKS),             Sound.create("block.weeping_vines.hit",            BLOCKS, 0.5f, 0.5f), Sound.create("block.weeping_vines.step",            PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup TWISTING_VINES =           new SoundGroup(Sound.create("block.twisting_vines.place",           BLOCKS),             Sound.create("block.twisting_vines.break",           BLOCKS),             Sound.create("block.twisting_vines.hit",           BLOCKS, 0.5f, 0.5f), Sound.create("block.twisting_vines.step",           PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SOUL_SAND =                new SoundGroup(Sound.create("block.soul_sand.place",                BLOCKS),             Sound.create("block.soul_sand.break",                BLOCKS),             Sound.create("block.soul_sand.hit",                BLOCKS, 0.5f, 0.5f), Sound.create("block.soul_sand.step",                PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SOUL_SOIL =                new SoundGroup(Sound.create("block.soul_soil.place",                BLOCKS),             Sound.create("block.soul_soil.break",                BLOCKS),             Sound.create("block.soul_soil.hit",                BLOCKS, 0.5f, 0.5f), Sound.create("block.soul_soil.step",                PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BASALT =                   new SoundGroup(Sound.create("block.basalt.place",                   BLOCKS),             Sound.create("block.basalt.break",                   BLOCKS),             Sound.create("block.basalt.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.basalt.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WART_BLOCK =               new SoundGroup(Sound.create("block.wart_block.place",               BLOCKS),             Sound.create("block.wart_block.break",               BLOCKS),             Sound.create("block.wart_block.hit",               BLOCKS, 0.5f, 0.5f), Sound.create("block.wart_block.step",               PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHERRACK =               new SoundGroup(Sound.create("block.netherrack.place",               BLOCKS),             Sound.create("block.netherrack.break",               BLOCKS),             Sound.create("block.netherrack.hit",               BLOCKS, 0.5f, 0.5f), Sound.create("block.netherrack.step",               PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_BRICKS =            new SoundGroup(Sound.create("block.nether_bricks.place",            BLOCKS),             Sound.create("block.nether_bricks.break",            BLOCKS),             Sound.create("block.nether_bricks.hit",            BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_bricks.step",            PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_SPROUTS =           new SoundGroup(Sound.create("block.nether_sprouts.place",           BLOCKS),             Sound.create("block.nether_sprouts.break",           BLOCKS),             Sound.create("block.nether_sprouts.hit",           BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_sprouts.step",           PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_ORE =               new SoundGroup(Sound.create("block.nether_ore.place",               BLOCKS),             Sound.create("block.nether_ore.break",               BLOCKS),             Sound.create("block.nether_ore.hit",               BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_ore.step",               PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BONE_BLOCK =               new SoundGroup(Sound.create("block.bone_block.place",               BLOCKS),             Sound.create("block.bone_block.break",               BLOCKS),             Sound.create("block.bone_block.hit",               BLOCKS, 0.5f, 0.5f), Sound.create("block.bone_block.step",               PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHERITE_BLOCK =          new SoundGroup(Sound.create("block.netherite_block.place",          BLOCKS),             Sound.create("block.netherite_block.break",          BLOCKS),             Sound.create("block.netherite_block.hit",          BLOCKS, 0.5f, 0.5f), Sound.create("block.netherite_block.step",          PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup ANCIENT_DEBRIS =           new SoundGroup(Sound.create("block.ancient_debris.place",           BLOCKS),             Sound.create("block.ancient_debris.break",           BLOCKS),             Sound.create("block.ancient_debris.hit",           BLOCKS, 0.5f, 0.5f), Sound.create("block.ancient_debris.step",           PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LODESTONE =                new SoundGroup(Sound.create("block.lodestone.place",                BLOCKS),             Sound.create("block.lodestone.break",                BLOCKS),             Sound.create("block.lodestone.hit",                BLOCKS, 0.5f, 0.5f), Sound.create("block.lodestone.step",                PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHAIN =                    new SoundGroup(Sound.create("block.chain.place",                    BLOCKS),             Sound.create("block.chain.break",                    BLOCKS),             Sound.create("block.chain.hit",                    BLOCKS, 0.5f, 0.5f), Sound.create("block.chain.step",                    PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_GOLD_ORE =          new SoundGroup(Sound.create("block.nether_gold_ore.place",          BLOCKS),             Sound.create("block.nether_gold_ore.break",          BLOCKS),             Sound.create("block.nether_gold_ore.hit",          BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_gold_ore.step",          PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GILDED_BLACKSTONE =        new SoundGroup(Sound.create("block.gilded_blackstone.place",        BLOCKS),             Sound.create("block.gilded_blackstone.break",        BLOCKS),             Sound.create("block.gilded_blackstone.hit",        BLOCKS, 0.5f, 0.5f), Sound.create("block.gilded_blackstone.step",        PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CANDLE =                   new SoundGroup(Sound.create("block.candle.place",                   BLOCKS),             Sound.create("block.candle.break",                   BLOCKS),             Sound.create("block.candle.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.candle.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup AMETHYST =                 new SoundGroup(Sound.create("block.amethyst.place",                 BLOCKS),             Sound.create("block.amethyst.break",                 BLOCKS),             Sound.create("block.amethyst.hit",                 BLOCKS, 0.5f, 0.5f), Sound.create("block.amethyst.step",                 PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup AMETHYST_CLUSTER =         new SoundGroup(Sound.create("block.amethyst_cluster.place",         BLOCKS),             Sound.create("block.amethyst_cluster.break",         BLOCKS),             Sound.create("block.amethyst_cluster.hit",         BLOCKS, 0.5f, 0.5f), Sound.create("block.amethyst_cluster.step",         PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SMALL_AMETHYST_BUD =       new SoundGroup(Sound.create("block.small_amethyst_bud.place",       BLOCKS),             Sound.create("block.small_amethyst_bud.break",       BLOCKS),             Sound.create("block.small_amethyst_bud.hit",       BLOCKS, 0.5f, 0.5f), Sound.create("block.small_amethyst_bud.step",       PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MEDIUM_AMETHYST_BUD =      new SoundGroup(Sound.create("block.medium_amethyst_bud.place",      BLOCKS),             Sound.create("block.medium_amethyst_bud.break",      BLOCKS),             Sound.create("block.medium_amethyst_bud.hit",      BLOCKS, 0.5f, 0.5f), Sound.create("block.medium_amethyst_bud.step",      PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup LARGE_AMETHYST_BUD =       new SoundGroup(Sound.create("block.large_amethyst_bud.place",       BLOCKS),             Sound.create("block.large_amethyst_bud.break",       BLOCKS),             Sound.create("block.large_amethyst_bud.hit",       BLOCKS, 0.5f, 0.5f), Sound.create("block.large_amethyst_bud.step",       PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup TUFF =                     new SoundGroup(Sound.create("block.tuff.place",                     BLOCKS),             Sound.create("block.tuff.break",                     BLOCKS),             Sound.create("block.tuff.hit",                     BLOCKS, 0.5f, 0.5f), Sound.create("block.tuff.step",                     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CALCITE =                  new SoundGroup(Sound.create("block.calcite.place",                  BLOCKS),             Sound.create("block.calcite.break",                  BLOCKS),             Sound.create("block.calcite.hit",                  BLOCKS, 0.5f, 0.5f), Sound.create("block.calcite.step",                  PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DRIPSTONE_BLOCK =          new SoundGroup(Sound.create("block.dripstone_block.place",          BLOCKS),             Sound.create("block.dripstone_block.break",          BLOCKS),             Sound.create("block.dripstone_block.hit",          BLOCKS, 0.5f, 0.5f), Sound.create("block.dripstone_block.step",          PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup POINTED_DRIPSTONE =        new SoundGroup(Sound.create("block.pointed_dripstone.place",        BLOCKS),             Sound.create("block.pointed_dripstone.break",        BLOCKS),             Sound.create("block.pointed_dripstone.hit",        BLOCKS, 0.5f, 0.5f), Sound.create("block.pointed_dripstone.step",        PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup COPPER =                   new SoundGroup(Sound.create("block.copper.place",                   BLOCKS),             Sound.create("block.copper.break",                   BLOCKS),             Sound.create("block.copper.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.copper.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CAVE_VINES =               new SoundGroup(Sound.create("block.cave_vines.place",               BLOCKS),             Sound.create("block.cave_vines.break",               BLOCKS),             Sound.create("block.cave_vines.hit",               BLOCKS, 0.5f, 0.5f), Sound.create("block.cave_vines.step",               PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SPORE_BLOSSOM =            new SoundGroup(Sound.create("block.spore_blossom.place",            BLOCKS),             Sound.create("block.spore_blossom.break",            BLOCKS),             Sound.create("block.spore_blossom.hit",            BLOCKS, 0.5f, 0.5f), Sound.create("block.spore_blossom.step",            PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup AZALEA =                   new SoundGroup(Sound.create("block.azalea.place",                   BLOCKS),             Sound.create("block.azalea.break",                   BLOCKS),             Sound.create("block.azalea.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.azalea.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup FLOWERING_AZALEA =         new SoundGroup(Sound.create("block.flowering_azalea.place",         BLOCKS),             Sound.create("block.flowering_azalea.break",         BLOCKS),             Sound.create("block.flowering_azalea.hit",         BLOCKS, 0.5f, 0.5f), Sound.create("block.flowering_azalea.step",         PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MOSS_CARPET =              new SoundGroup(Sound.create("block.moss_carpet.place",              BLOCKS),             Sound.create("block.moss_carpet.break",              BLOCKS),             Sound.create("block.moss_carpet.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.moss_carpet.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup PINK_PETALS =              new SoundGroup(Sound.create("block.pink_petals.place",              BLOCKS),             Sound.create("block.pink_petals.break",              BLOCKS),             Sound.create("block.pink_petals.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.pink_petals.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MOSS =                     new SoundGroup(Sound.create("block.moss.place",                     BLOCKS),             Sound.create("block.moss.break",                     BLOCKS),             Sound.create("block.moss.hit",                     BLOCKS, 0.5f, 0.5f), Sound.create("block.moss.step",                     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BIG_DRIPLEAF =             new SoundGroup(Sound.create("block.big_dripleaf.place",             BLOCKS),             Sound.create("block.big_dripleaf.break",             BLOCKS),             Sound.create("block.big_dripleaf.hit",             BLOCKS, 0.5f, 0.5f), Sound.create("block.big_dripleaf.step",             PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SMALL_DRIPLEAF =           new SoundGroup(Sound.create("block.small_dripleaf.place",           BLOCKS),             Sound.create("block.small_dripleaf.break",           BLOCKS),             Sound.create("block.small_dripleaf.hit",           BLOCKS, 0.5f, 0.5f), Sound.create("block.small_dripleaf.step",           PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup ROOTED_DIRT =              new SoundGroup(Sound.create("block.rooted_dirt.place",              BLOCKS),             Sound.create("block.rooted_dirt.break",              BLOCKS),             Sound.create("block.rooted_dirt.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.rooted_dirt.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup HANGING_ROOTS =            new SoundGroup(Sound.create("block.hanging_roots.place",            BLOCKS),             Sound.create("block.hanging_roots.break",            BLOCKS),             Sound.create("block.hanging_roots.hit",            BLOCKS, 0.5f, 0.5f), Sound.create("block.hanging_roots.step",            PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup AZALEA_LEAVES =            new SoundGroup(Sound.create("block.azalea_leaves.place",            BLOCKS),             Sound.create("block.azalea_leaves.break",            BLOCKS),             Sound.create("block.azalea_leaves.hit",            BLOCKS, 0.5f, 0.5f), Sound.create("block.azalea_leaves.step",            PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK_SENSOR =             new SoundGroup(Sound.create("block.sculk_sensor.place",             BLOCKS),             Sound.create("block.sculk_sensor.break",             BLOCKS),             Sound.create("block.sculk_sensor.hit",             BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk_sensor.step",             PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK_CATALYST =           new SoundGroup(Sound.create("block.sculk_catalyst.place",           BLOCKS),             Sound.create("block.sculk_catalyst.break",           BLOCKS),             Sound.create("block.sculk_catalyst.hit",           BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk_catalyst.step",           PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK =                    new SoundGroup(Sound.create("block.sculk.place",                    BLOCKS),             Sound.create("block.sculk.break",                    BLOCKS),             Sound.create("block.sculk.hit",                    BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk.step",                    PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK_VEIN =               new SoundGroup(Sound.create("block.sculk_vein.place",               BLOCKS),             Sound.create("block.sculk_vein.break",               BLOCKS),             Sound.create("block.sculk_vein.hit",               BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk_vein.step",               PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SCULK_SHRIEKER =           new SoundGroup(Sound.create("block.sculk_shrieker.place",           BLOCKS),             Sound.create("block.sculk_shrieker.break",           BLOCKS),             Sound.create("block.sculk_shrieker.hit",           BLOCKS, 0.5f, 0.5f), Sound.create("block.sculk_shrieker.step",           PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup GLOW_LICHEN =              new SoundGroup(Sound.create("block.glow_lichen.place",              BLOCKS),             Sound.create("block.glow_lichen.break",              BLOCKS),             Sound.create("block.glow_lichen.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.glow_lichen.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DEEPSLATE =                new SoundGroup(Sound.create("block.deepslate.place",                BLOCKS),             Sound.create("block.deepslate.break",                BLOCKS),             Sound.create("block.deepslate.hit",                BLOCKS, 0.5f, 0.5f), Sound.create("block.deepslate.step",                PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DEEPSLATE_BRICKS =         new SoundGroup(Sound.create("block.deepslate_bricks.place",         BLOCKS),             Sound.create("block.deepslate_bricks.break",         BLOCKS),             Sound.create("block.deepslate_bricks.hit",         BLOCKS, 0.5f, 0.5f), Sound.create("block.deepslate_bricks.step",         PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DEEPSLATE_TILES =          new SoundGroup(Sound.create("block.deepslate_tiles.place",          BLOCKS),             Sound.create("block.deepslate_tiles.break",          BLOCKS),             Sound.create("block.deepslate_tiles.hit",          BLOCKS, 0.5f, 0.5f), Sound.create("block.deepslate_tiles.step",          PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup POLISHED_DEEPSLATE =       new SoundGroup(Sound.create("block.polished_deepslate.place",       BLOCKS),             Sound.create("block.polished_deepslate.break",       BLOCKS),             Sound.create("block.polished_deepslate.hit",       BLOCKS, 0.5f, 0.5f), Sound.create("block.polished_deepslate.step",       PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup FROGLIGHT =                new SoundGroup(Sound.create("block.froglight.place",                BLOCKS),             Sound.create("block.froglight.break",                BLOCKS),             Sound.create("block.froglight.hit",                BLOCKS, 0.5f, 0.5f), Sound.create("block.froglight.step",                PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup FROGSPAWN =                new SoundGroup(Sound.create("block.frogspawn.place",                BLOCKS),             Sound.create("block.frogspawn.break",                BLOCKS),             Sound.create("block.frogspawn.hit",                BLOCKS, 0.5f, 0.5f), Sound.create("block.frogspawn.step",                PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MANGROVE_ROOTS =           new SoundGroup(Sound.create("block.mangrove_roots.place",           BLOCKS),             Sound.create("block.mangrove_roots.break",           BLOCKS),             Sound.create("block.mangrove_roots.hit",           BLOCKS, 0.5f, 0.5f), Sound.create("block.mangrove_roots.step",           PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MUDDY_MANGROVE_ROOTS =     new SoundGroup(Sound.create("block.muddy_mangrove_roots.place",     BLOCKS),             Sound.create("block.muddy_mangrove_roots.break",     BLOCKS),             Sound.create("block.muddy_mangrove_roots.hit",     BLOCKS, 0.5f, 0.5f), Sound.create("block.muddy_mangrove_roots.step",     PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MUD =                      new SoundGroup(Sound.create("block.mud.place",                      BLOCKS),             Sound.create("block.mud.break",                      BLOCKS),             Sound.create("block.mud.hit",                      BLOCKS, 0.5f, 0.5f), Sound.create("block.mud.step",                      PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup MUD_BRICKS =               new SoundGroup(Sound.create("block.mud_bricks.place",               BLOCKS),             Sound.create("block.mud_bricks.break",               BLOCKS),             Sound.create("block.mud_bricks.hit",               BLOCKS, 0.5f, 0.5f), Sound.create("block.mud_bricks.step",               PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup PACKED_MUD =               new SoundGroup(Sound.create("block.packed_mud.place",               BLOCKS),             Sound.create("block.packed_mud.break",               BLOCKS),             Sound.create("block.packed_mud.hit",               BLOCKS, 0.5f, 0.5f), Sound.create("block.packed_mud.step",               PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup HANGING_SIGN =             new SoundGroup(Sound.create("block.hanging_sign.place",             BLOCKS),             Sound.create("block.hanging_sign.break",             BLOCKS),             Sound.create("block.hanging_sign.hit",             BLOCKS, 0.5f, 0.5f), Sound.create("block.hanging_sign.step",             PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_WOOD_HANGING_SIGN = new SoundGroup(Sound.create("block.nether_wood_hanging_sign.place", BLOCKS),             Sound.create("block.nether_wood_hanging_sign.break", BLOCKS),             Sound.create("block.nether_wood_hanging_sign.hit", BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_wood_hanging_sign.step", PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BAMBOO_WOOD_HANGING_SIGN = new SoundGroup(Sound.create("block.bamboo_wood_hanging_sign.place", BLOCKS),             Sound.create("block.bamboo_wood_hanging_sign.break", BLOCKS),             Sound.create("block.bamboo_wood_hanging_sign.hit", BLOCKS, 0.5f, 0.5f), Sound.create("block.bamboo_wood_hanging_sign.step", PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup BAMBOO_WOOD =              new SoundGroup(Sound.create("block.bamboo_wood.place",              BLOCKS),             Sound.create("block.bamboo_wood.break",              BLOCKS),             Sound.create("block.bamboo_wood.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.bamboo_wood.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup NETHER_WOOD =              new SoundGroup(Sound.create("block.nether_wood.place",              BLOCKS),             Sound.create("block.nether_wood.break",              BLOCKS),             Sound.create("block.nether_wood.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.nether_wood.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHERRY_WOOD =              new SoundGroup(Sound.create("block.cherry_wood.place",              BLOCKS),             Sound.create("block.cherry_wood.break",              BLOCKS),             Sound.create("block.cherry_wood.hit",              BLOCKS, 0.5f, 0.5f), Sound.create("block.cherry_wood.step",              PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHERRY_SAPLING =           new SoundGroup(Sound.create("block.cherry_sapling.place",           BLOCKS),             Sound.create("block.cherry_sapling.break",           BLOCKS),             Sound.create("block.cherry_sapling.hit",           BLOCKS, 0.5f, 0.5f), Sound.create("block.cherry_sapling.step",           PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHERRY_LEAVES =            new SoundGroup(Sound.create("block.cherry_leaves.place",            BLOCKS),             Sound.create("block.cherry_leaves.break",            BLOCKS),             Sound.create("block.cherry_leaves.hit",            BLOCKS, 0.5f, 0.5f), Sound.create("block.cherry_leaves.step",            PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHERRY_WOOD_HANGING_SIGN = new SoundGroup(Sound.create("block.cherry_wood_hanging_sign.place", BLOCKS),             Sound.create("block.cherry_wood_hanging_sign.break", BLOCKS),             Sound.create("block.cherry_wood_hanging_sign.hit", BLOCKS, 0.5f, 0.5f), Sound.create("block.cherry_wood_hanging_sign.step", PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup CHISELED_BOOKSHELF =       new SoundGroup(Sound.create("block.chiseled_bookshelf.place",       BLOCKS),             Sound.create("block.chiseled_bookshelf.break",       BLOCKS),             Sound.create("block.chiseled_bookshelf.hit",       BLOCKS, 0.5f, 0.5f), Sound.create("block.chiseled_bookshelf.step",       PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SUSPICIOUS_SAND =          new SoundGroup(Sound.create("block.suspicious_sand.place",          BLOCKS),             Sound.create("block.suspicious_sand.break",          BLOCKS),             Sound.create("block.suspicious_sand.hit",          BLOCKS, 0.5f, 0.5f), Sound.create("block.suspicious_sand.step",          PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SUSPICIOUS_GRAVEL =        new SoundGroup(Sound.create("block.suspicious_gravel.place",        BLOCKS),             Sound.create("block.suspicious_gravel.break",        BLOCKS),             Sound.create("block.suspicious_gravel.hit",        BLOCKS, 0.5f, 0.5f), Sound.create("block.suspicious_gravel.step",        PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DECORATED_POT =            new SoundGroup(Sound.create("block.decorated_pot.place",            BLOCKS),             Sound.create("block.decorated_pot.break",            BLOCKS),             Sound.create("block.decorated_pot.hit",            BLOCKS, 0.5f, 0.5f), Sound.create("block.decorated_pot.step",            PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup DECORATED_POT_CRACKED =    new SoundGroup(Sound.create("block.decorated_pot_cracked.place",    BLOCKS),             Sound.create("block.decorated_pot_cracked.break",    BLOCKS),             Sound.create("block.decorated_pot_cracked.hit",    BLOCKS, 0.5f, 0.5f), Sound.create("block.decorated_pot_cracked.step",    PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup SPONGE =                   new SoundGroup(Sound.create("block.sponge.place",                   BLOCKS),             Sound.create("block.sponge.break",                   BLOCKS),             Sound.create("block.sponge.hit",                   BLOCKS, 0.5f, 0.5f), Sound.create("block.sponge.step",                   PLAYERS, 0.3f, 0.9f));
    public static final SoundGroup WET_SPONGE =               new SoundGroup(Sound.create("block.wet_sponge.place",               BLOCKS),             Sound.create("block.wet_sponge.break",               BLOCKS),             Sound.create("block.wet_sponge.hit",               BLOCKS, 0.5f, 0.5f), Sound.create("block.wet_sponge.step",               PLAYERS, 0.3f, 0.9f));
    //</editor-fold>

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
     * @return The place sound of this SoundGroup, or null if the place sound is
     *         not set
     */
    public @Nullable Sound getPlaceSound() {
        return this.placeSound;
    }

    /**
     * @return The break sound of this SoundGroup, or null if the break sound is
     *         not set
     */
    public @Nullable Sound getBreakSound() {
        return this.breakSound;
    }

    /**
     * @return The hit sound of this SoundGroup, or null if the hit sound is not
     *         set
     */
    public @Nullable Sound getHitSound() {
        return this.hitSound;
    }

    /**
     * @return The step sound of this SoundGroup, or null if the step sound is
     *         not set
     */
    public @Nullable Sound getStepSound() {
        return this.stepSound;
    }

    /**
     * Plays the place sound of this SoundGroup at the specified location. If
     * the place sound is "block.wood.place", the wood place sound from the
     * {@link Config} will be played instead. If the place sound is null,
     * nothing will be played.
     *
     * @param position The location to play the place sound at
     * @throws IllegalStateException If the world of the position is null
     */
    public void playPlaceSound(final @NotNull MSPosition position) throws IllegalStateException {
        this.playPlaceSound(position.toLocation());
    }

    /**
     * Plays the place sound of this SoundGroup at the specified location. If
     * the place sound is "block.wood.place", the wood place sound from the
     * {@link Config} will be played instead. If the place sound is null,
     * nothing will be played.
     *
     * @param location The location to play the place sound at
     * @throws IllegalStateException If the world of the location is null
     */
    public void playPlaceSound(final @NotNull Location location) throws IllegalStateException {
        if (this.placeSound != null) {
            this.placeSound.play(location);
        }
    }

    /**
     * Plays the break sound of this SoundGroup at the specified location. If
     * the break sound is "block.wood.break", the wood break sound from the
     * {@link Config} will be played instead. If the break sound is null,
     * nothing will be played.
     *
     * @param position The position to play the break sound at
     * @throws IllegalStateException If the world of the position is null
     */
    public void playBreakSound(final @NotNull MSPosition position) throws IllegalStateException {
        this.playBreakSound(position.toLocation());
    }

    /**
     * Plays the break sound of this SoundGroup at the specified location. If
     * the break sound is "block.wood.break", the wood break sound from the
     * {@link Config} will be played instead. If the break sound is null,
     * nothing will be played.
     *
     * @param location The location to play the break sound at
     * @throws IllegalStateException If the world of the location is null
     */
    public void playBreakSound(final @NotNull Location location) throws IllegalStateException {
        if (this.breakSound != null) {
            this.breakSound.play(location);
        }
    }

    /**
     * Plays the hit sound of this SoundGroup at the specified location. If the
     * hit sound is "block.wood.hit", the wood hit sound from the {@link Config}
     * will be played instead. If the hit sound is null, nothing will be played.
     *
     * @param position The position to play the hit sound at
     * @throws IllegalStateException If the world of the position is null
     */
    public void playHitSound(final @NotNull MSPosition position) throws IllegalStateException {
        this.playHitSound(position.toLocation());
    }

    /**
     * Plays the hit sound of this SoundGroup at the specified location. If the
     * hit sound is "block.wood.hit", the wood hit sound from the {@link Config}
     * will be played instead. If the hit sound is null, nothing will be played.
     *
     * @param location The location to play the hit sound at
     * @throws IllegalStateException If the world of the location is null
     */
    public void playHitSound(final @NotNull Location location) throws IllegalStateException {
        if (this.hitSound != null) {
            this.hitSound.play(location);
        }
    }

    /**
     * Plays the step sound of this SoundGroup at the specified location. If the
     * step sound is "block.wood.step", the wood step sound from the
     * {@link Config} will be played instead. If the step sound is null, nothing
     * will be played.
     *
     * @param position The position to play the step sound at
     * @throws IllegalStateException If the world of the position is null
     */
    public void playStepSound(final @NotNull MSPosition position) throws IllegalStateException {
        this.playStepSound(position.toLocation());
    }

    /**
     * Plays the step sound of this SoundGroup at the specified location. If the
     * step sound is "block.wood.step", the wood step sound from the
     * {@link Config} will be played instead. If the step sound is null, nothing
     * will be played.
     *
     * @param location The location to play the step sound at
     * @throws IllegalStateException If the world of the location is null
     */
    public void playStepSound(final @NotNull Location location) throws IllegalStateException {
        if (this.stepSound != null) {
            this.stepSound.play(location);
        }
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
     * Creates a clone of this SoundGroup with the same sounds
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
     * Represents a sound with :
     * <ul>
     *     <li>A key {@link String}</li>
     *     <li>A {@link SoundCategory}</li>
     *     <li>A volume float</li>
     *     <li>A pitch float</li>
     * </ul>
     *
     * Sound can be created with builder, for example :
     * <pre>
     *     Sound sound = Sound.builder()
     *             .key("block.wood.place")
     *             .category(SoundCategory.BLOCKS)
     *             .volume(1.0f)
     *             .pitch(1.0f)
     *             .build();
     * </pre>
     * Or with static methods :
     * <ul>
     *     <li>{@link #create(String)}</li>
     *     <li>{@link #create(String, SoundCategory)}</li>
     *     <li>{@link #create(String, SoundCategory, float, float)}</li>
     * </ul>
     */
    @Immutable
    public static final class Sound implements Cloneable {
        private final String key;
        private final SoundCategory category;
        private final float volume;
        private final float pitch;

        private static final String WOOD_PLACE_SOUND_KEY = "block.wood.place";
        private static final String WOOD_BREAK_SOUND_KEY = "block.wood.break";
        private static final String WOOD_HIT_SOUND_KEY = "block.wood.hit";
        private static final String WOOD_STEP_SOUND_KEY = "block.wood.step";

        private Sound(final @NotNull Builder builder) {
            this.key = builder.key;
            this.category = builder.category;
            this.volume = builder.volume;
            this.pitch = builder.pitch;
        }

        /**
         * Creates a new Sound instance with 1.0f volume and pitch and
         * {@link SoundCategory#MASTER} category
         *
         * @param key The key of the sound
         * @return A new Sound instance
         * @throws IllegalArgumentException If the key is blank
         * @see #create(String, SoundCategory)
         * @see #create(String, SoundCategory, float, float)
         */
        public static @NotNull Sound create(final @NotNull String key) throws IllegalArgumentException {
            return create(
                    key,
                    SoundCategory.MASTER
            );
        }

        /**
         * Creates a new Sound instance with 1.0f volume and pitch
         *
         * @param key           The key of the sound
         * @param soundCategory The sound category of the sound
         * @return A new Sound instance
         * @throws IllegalArgumentException If the key is blank
         * @see #create(String, SoundCategory, float, float)
         */
        public static @NotNull Sound create(
                final @NotNull String key,
                final @NotNull SoundCategory soundCategory
        ) throws IllegalArgumentException {
            return create(
                    key,
                    soundCategory,
                    1.0f, 1.0f
            );
        }

        /**
         * Creates a new Sound instance
         *
         * @param key           The key of the sound
         * @param soundCategory The sound category of the sound
         * @param volume        The volume of the sound
         * @param pitch         The pitch of the sound
         * @return A new Sound instance
         * @throws IllegalArgumentException If the key is blank
         */
        public static @NotNull Sound create(
                final @NotNull String key,
                final @NotNull SoundCategory soundCategory,
                final float volume,
                final float pitch
        ) throws IllegalArgumentException {
            return new Sound.Builder()
                    .key(key)
                    .category(soundCategory)
                    .volume(volume)
                    .pitch(pitch)
                    .build();
        }

        /**
         * Creates a new builder for {@link Sound}
         *
         * @return A new builder
         */
        public static @NotNull Builder builder() {
            return new Builder();
        }

        /**
         * @return The key of this Sound
         */
        public @NotNull String getKey() {
            return this.key;
        }

        /**
         * @return The sound category of this Sound
         */
        public @NotNull SoundCategory getCategory() {
            return this.category;
        }

        /**
         * @return The volume of this Sound
         */
        public float getVolume() {
            return this.volume;
        }

        /**
         * @return The pitch of this Sound
         */
        public float getPitch() {
            return this.pitch;
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
                    "key=" + this.key +
                    ", soundCategory=" + this.category +
                    ", volume=" + this.volume +
                    ", pitch=" + this.pitch +
                    '}';
        }

        /**
         * @return A builder for this Sound
         */
        public @NotNull Builder toBuilder() {
            final Builder builder = new Builder();

            builder.key = this.key;
            builder.category = this.category;
            builder.volume = this.volume;
            builder.pitch = this.pitch;

            return builder;
        }

        /**
         * Plays this Sound at the specified position
         *
         * @param position The position to play this Sound at
         * @throws IllegalStateException If the world of the position is null
         */
        public void play(final @NotNull MSPosition position) {
            this.play(position.toLocation());
        }

        /**
         * Plays this Sound at the specified location
         *
         * @param location The location to play this Sound at
         * @throws IllegalStateException If the world of the location is null
         */
        public void play(final @NotNull Location location) {
            final World world = location.getWorld();

            if (world == null) {
                throw new IllegalStateException("The world of the location cannot be null");
            }

            world.playSound(
                    location,
                    switch (this.key) {
                        case WOOD_PLACE_SOUND_KEY -> MSBlock.config().getWoodSoundPlace();
                        case WOOD_BREAK_SOUND_KEY -> MSBlock.config().getWoodSoundBreak();
                        case WOOD_HIT_SOUND_KEY ->   MSBlock.config().getWoodSoundHit();
                        case WOOD_STEP_SOUND_KEY ->  MSBlock.config().getWoodSoundStep();
                        default -> this.key;
                    },
                    this.category,
                    this.volume,
                    this.pitch
            );
        }

        /**
         * A builder for {@link Sound}
         */
        public static class Builder {
            private String key;
            private SoundCategory category;
            private float volume;
            private float pitch;

            private Builder() {
                this.category = SoundCategory.MASTER;
                this.volume = 1.0f;
                this.pitch = 1.0f;
            }

            /**
             * @return The key of this Sound
             */
            public String key() {
                return this.key;
            }

            /**
             * Sets the key of this Sound
             *
             * @param key The key to set
             * @return This builder, for chaining
             */
            public @NotNull Builder key(final @NotNull String key) {
                this.key = key;
                return this;
            }

            /**
             * @return The sound category of this Sound
             */
            public SoundCategory category() {
                return this.category;
            }

            /**
             * Sets the sound category of this Sound
             *
             * @param category The sound category to set
             * @return This builder, for chaining
             */
            public @NotNull Builder category(final @NotNull SoundCategory category) {
                this.category = category;
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
             * @return This builder, for chaining
             */
            public @NotNull Builder volume(final float volume) {
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
             * @return This builder, for chaining
             */
            public @NotNull Builder pitch(final float pitch) {
                this.pitch = pitch;
                return this;
            }

            /**
             * Builds a Sound with the values of this builder
             *
             * @return The built Sound
             * @throws IllegalStateException If the key is blank or null
             */
            public @NotNull Sound build() throws IllegalStateException {
                if (ChatUtils.isBlank(this.key)) {
                    throw new IllegalArgumentException("Key cannot be blank or null!");
                }

                return new Sound(this);
            }
        }
    }
}
